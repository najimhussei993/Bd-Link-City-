package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

sealed class FirebaseSyncState {
    object Idle : FirebaseSyncState()
    object Syncing : FirebaseSyncState()
    object Success : FirebaseSyncState()
    data class Error(val message: String) : FirebaseSyncState()
}

class ContentViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = ContentRepository(database.contentDao())

    // UI Inputs & Filters
    val searchQuery = MutableStateFlow("")
    val selectedCategory = MutableStateFlow("All") // "All", "Movie", "Anime"
    val currentRole = MutableStateFlow("User")    // "User" or "Admin"
    
    // Bookmark and watch logic
    val watchlistIds = MutableStateFlow<Set<Int>>(emptySet())
    val activePlayingEpisode = MutableStateFlow<EpisodeEntity?>(null)

    // Sync State
    private val _firebaseSyncState = MutableStateFlow<FirebaseSyncState>(FirebaseSyncState.Idle)
    val firebaseSyncState: StateFlow<FirebaseSyncState> = _firebaseSyncState.asStateFlow()

    // Log messages for the simulated Firebase Console inside the Admin Panel
    private val _firebaseLogs = MutableStateFlow<List<String>>(
        listOf(
            "Security rules initialized",
            "Firebase connection established",
            "Database ready for transactions"
        )
    )
    val firebaseLogs: StateFlow<List<String>> = _firebaseLogs.asStateFlow()

    // All available contents
    val allContent: StateFlow<List<ContentEntity>> = repository.allContent
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val listFeed: StateFlow<List<ContentEntity>> = combine(
        allContent, searchQuery, selectedCategory
    ) { list, query, category ->
        list.filter { item ->
            val matchesQuery = item.title.contains(query, ignoreCase = true) ||
                    item.genre.contains(query, ignoreCase = true)
            val matchesCategory = category == "All" || item.contentType.equals(category, ignoreCase = true)
            matchesQuery && matchesCategory
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val trendingFeed: StateFlow<List<ContentEntity>> = allContent
        .map { list -> list.filter { it.isTrending } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Selection observables (collected safely inside coroutines to avoid flatMapLatest)
    val selectedContentId = MutableStateFlow<Int?>(null)
    
    private val _selectedContent = MutableStateFlow<ContentEntity?>(null)
    val selectedContent: StateFlow<ContentEntity?> = _selectedContent.asStateFlow()

    private val _selectedContentEpisodes = MutableStateFlow<List<EpisodeEntity>>(emptyList())
    val selectedContentEpisodes: StateFlow<List<EpisodeEntity>> = _selectedContentEpisodes.asStateFlow()

    init {
        // Safe observation of selected item
        viewModelScope.launch {
            selectedContentId.collect { id ->
                if (id != null) {
                    // Update content detail StateFlow
                    repository.getContentById(id).collect { content ->
                        _selectedContent.value = content
                    }
                } else {
                    _selectedContent.value = null
                }
            }
        }

        // Safe observation of episodes
        viewModelScope.launch {
            selectedContentId.collect { id ->
                if (id != null) {
                    repository.getEpisodesForContent(id).collect { eps ->
                        _selectedContentEpisodes.value = eps
                    }
                } else {
                    _selectedContentEpisodes.value = emptyList()
                }
            }
        }
    }

    // Administrative operations
    fun addContent(
        title: String,
        description: String,
        bannerUrl: String,
        contentType: String,
        genre: String,
        releaseYear: String,
        rating: Double,
        isTrending: Boolean
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val correctBannerUrl = bannerUrl.ifBlank {
                if (contentType == "Anime") {
                    "https://images.unsplash.com/photo-1578632767115-351597cf2477?q=80&w=600"
                } else {
                    "https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=600"
                }
            }
            
            val item = ContentEntity(
                title = title,
                description = description,
                bannerUrl = correctBannerUrl,
                contentType = contentType,
                genre = genre,
                releaseYear = releaseYear.ifBlank { "2026" },
                rating = if (rating in 0.0..10.0) rating else 7.5,
                isTrending = isTrending,
                firebaseSynced = false
            )
            val newId = repository.insertContent(item)
            
            // Add automated default starter episode
            repository.insertEpisode(
                EpisodeEntity(
                    contentId = newId.toInt(),
                    episodeNumber = 1,
                    title = "Episode 1 Launch",
                    description = "Initial pilot episode for ${title}.",
                    duration = "24 min",
                    streamUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
                    firebaseSynced = false
                )
            )
            addLog("Created content: $title (ID: $newId) with initial episode")
        }
    }

    fun addEpisode(
        contentId: Int,
        episodeNumber: Int,
        title: String,
        description: String,
        duration: String,
        streamUrl: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val episode = EpisodeEntity(
                contentId = contentId,
                episodeNumber = episodeNumber,
                title = title.ifBlank { "Episode $episodeNumber" },
                description = description.ifBlank { "Episode details are pending update." },
                duration = duration.ifBlank { "24 min" },
                streamUrl = streamUrl.ifBlank { "https://www.w3schools.com/html/movie.mp4" },
                firebaseSynced = false
            )
            repository.insertEpisode(episode)
            addLog("Added episode $episodeNumber to content ID $contentId")
        }
    }

    fun deleteContent(item: ContentEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            if (selectedContentId.value == item.id) {
                selectedContentId.value = null
            }
            repository.deleteContent(item)
            addLog("Deleted content: ${item.title}")
        }
    }

    fun deleteEpisode(episode: EpisodeEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteEpisode(episode)
            addLog("Deleted episode ID ${episode.id} from content")
        }
    }

    // Bookmark / Watchlist toggle
    fun toggleWatchlist(id: Int) {
        val current = watchlistIds.value.toMutableSet()
        if (current.contains(id)) {
            current.remove(id)
        } else {
            current.add(id)
        }
        watchlistIds.value = current
    }

    // Role switcher
    fun switchRole(role: String) {
        currentRole.value = role
        // Deselect or end playback when switching to clean screens
        activePlayingEpisode.value = null
    }

    // Simulated Firebase live sync invocation
    fun triggerFirebaseSync() {
        viewModelScope.launch {
            _firebaseSyncState.value = FirebaseSyncState.Syncing
            addLog("Beginning sync stream with Firebase Firestore...")
            
            try {
                val success = repository.simulateFirebaseSync()
                if (success) {
                    _firebaseSyncState.value = FirebaseSyncState.Success
                    addLog("Firebase synchronized successfully: nodes updated")
                } else {
                    _firebaseSyncState.value = FirebaseSyncState.Error("Sync timed out")
                    addLog("Firebase Sync Error: Operation aborted")
                }
            } catch (e: Exception) {
                _firebaseSyncState.value = FirebaseSyncState.Error(e.message ?: "Sync aborted")
                addLog("Firebase Sync Exception: ${e.localizedMessage}")
            }
        }
    }

    fun resetSyncState() {
        _firebaseSyncState.value = FirebaseSyncState.Idle
    }

    private fun addLog(message: String) {
        val uuidTail = UUID.randomUUID().toString().take(6)
        val fullMsg = "[Remote Logs] $message ($uuidTail)"
        val currentLogs = _firebaseLogs.value.toMutableList()
        currentLogs.add(0, fullMsg) // Prepends to keep chronological at top
        if (currentLogs.size > 20) {
            currentLogs.removeAt(currentLogs.size - 1)
        }
        _firebaseLogs.value = currentLogs
    }
}
