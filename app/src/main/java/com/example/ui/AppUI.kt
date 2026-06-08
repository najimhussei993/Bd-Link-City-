package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.ContentEntity
import com.example.data.EpisodeEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppUI(
    viewModel: ContentViewModel,
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val listFeed by viewModel.listFeed.collectAsState()
    val trendingFeed by viewModel.trendingFeed.collectAsState()
    val watchlistIds by viewModel.watchlistIds.collectAsState()
    val activePlayingEpisode by viewModel.activePlayingEpisode.collectAsState()
    
    val selectedContentId by viewModel.selectedContentId.collectAsState()
    val selectedContent by viewModel.selectedContent.collectAsState()
    val selectedEpisodes by viewModel.selectedContentEpisodes.collectAsState()
    
    val firebaseSyncState by viewModel.firebaseSyncState.collectAsState()
    val firebaseLogs by viewModel.firebaseLogs.collectAsState()

    var showWatchlistOnly by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Core Dashboard layout with safe system padding
        Scaffold(
            topBar = {
                Column {
                    // Modern styled Custom Premium Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF13111E),
                                        MaterialTheme.colorScheme.background
                                    )
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Logo Container (Gradient Logo Box + CineStream Titles)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(Color(0xFFD0BCFF), Color(0xFF381E72))
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Logo Icon",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "CineStream",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                            letterSpacing = 0.5.sp
                                        )
                                    )
                                    Text(
                                        text = "PREMIUM ANIME",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFFD0BCFF),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp,
                                            letterSpacing = 1.5.sp
                                        )
                                    )
                                }
                            }

                            // Glass Pill Switch for User vs Admin Mode
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color(0xFF1C1B1F))
                                    .border(1.dp, Color(0xFF323038), RoundedCornerShape(24.dp))
                                    .padding(3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(if (currentRole == "User") Color(0xFF381E72) else Color.Transparent)
                                        .clickable { viewModel.switchRole("User") }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .testTag("role_user_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "User Mode",
                                        tint = if (currentRole == "User") Color(0xFFD0BCFF) else Color(0xFFC9C5D0),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(if (currentRole == "Admin") Color(0xFF381E72) else Color.Transparent)
                                        .clickable { viewModel.switchRole("Admin") }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                        .testTag("role_admin_button"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = "Admin Mode",
                                        tint = if (currentRole == "Admin") Color(0xFFD0BCFF) else Color(0xFFC9C5D0),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                    
                    // Connection Status bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0A0812))
                            .padding(vertical = 4.dp, horizontal = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00FF66))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Firebase Connector: Online",
                                fontSize = 11.sp,
                                color = Color(0xFF00FF66),
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    HorizontalDivider(color = Color(0xFF1E1C30))
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (currentRole == "Admin") {
                    AdminView(
                        viewModel = viewModel,
                        allContent = listFeed,
                        firebaseSyncState = firebaseSyncState,
                        firebaseLogs = firebaseLogs
                    )
                } else {
                    UserView(
                        viewModel = viewModel,
                        trendingList = trendingFeed,
                        allList = listFeed,
                        watchlistIds = watchlistIds,
                        showWatchlistOnly = showWatchlistOnly,
                        onToggleWatchlistFilter = { showWatchlistOnly = !showWatchlistOnly }
                    )
                }
            }
        }

        // Details Side-sheet Overlay (Bottom or overlay presentation)
        AnimatedVisibility(
            visible = selectedContentId != null && activePlayingEpisode == null,
            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .clickable { viewModel.selectedContentId.value = null }
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f)
                        .align(Alignment.BottomCenter)
                        .clickable(enabled = false) {}, // prevent click-through
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    color = Color(0xFF1C1B1F),
                    border = BorderStroke(1.dp, Color(0xFF2B2930))
                ) {
                    if (selectedContent != null) {
                        DetailScreen(
                            contentItem = selectedContent!!,
                            episodes = selectedEpisodes,
                            watchlistIds = watchlistIds,
                            onClose = { viewModel.selectedContentId.value = null },
                            onToggleWatchlist = { viewModel.toggleWatchlist(selectedContent!!.id) },
                            onPlayEpisode = { viewModel.activePlayingEpisode.value = it }
                        )
                    }
                }
            }
        }

        // Expanded Immersive Player Fullscreen Overlay
        AnimatedVisibility(
            visible = activePlayingEpisode != null,
            enter = fadeIn() + scaleIn(initialScale = 0.9f),
            exit = fadeOut() + scaleOut(targetScale = 0.9f)
        ) {
            if (activePlayingEpisode != null && selectedContent != null) {
                ImmersivePlayerScreen(
                    contentItem = selectedContent!!,
                    episode = activePlayingEpisode!!,
                    onClose = { viewModel.activePlayingEpisode.value = null }
                )
            }
        }
    }
}

// User Portal View
@Composable
fun UserView(
    viewModel: ContentViewModel,
    trendingList: List<ContentEntity>,
    allList: List<ContentEntity>,
    watchlistIds: Set<Int>,
    showWatchlistOnly: Boolean,
    onToggleWatchlistFilter: () -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    val displayList = remember(allList, watchlistIds, showWatchlistOnly) {
        if (showWatchlistOnly) {
            allList.filter { watchlistIds.contains(it.id) }
        } else {
            allList
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Search & Category row
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = {
                        Text(
                            text = "Search titles or genres...",
                            color = Color(0xFFC9C5D0)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFFD0BCFF)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = Color(0xFFC9C5D0)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1C1B1F),
                        unfocusedContainerColor = Color(0xFF1C1B1F),
                        focusedBorderColor = Color(0xFFD0BCFF),
                        unfocusedBorderColor = Color(0xFF2B2930),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_input")
                )

                // Navigation Pill Selectors for Content Types + Watchlist filter toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val categories = listOf("All", "Movie", "Anime")
                    categories.forEach { cat ->
                        val isSelected = selectedCategory == cat && !showWatchlistOnly
                        val colorProgress by animateColorAsState(
                            targetValue = if (isSelected) Color(0xFF381E72) else Color(0xFF1C1B1F),
                            label = "cat_color"
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(24.dp))
                                .background(colorProgress)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) Color.Transparent else Color(0xFF2B2930),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .clickable {
                                    if (showWatchlistOnly) {
                                        onToggleWatchlistFilter()
                                    }
                                    viewModel.selectedCategory.value = cat
                                }
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat,
                                color = if (isSelected) Color(0xFFD0BCFF) else Color(0xFFC9C5D0),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    // Watchlist pill selector
                    val isWatchlistSelected = showWatchlistOnly
                    val watchlistColorProgress by animateColorAsState(
                        targetValue = if (isWatchlistSelected) Color(0xFFFF3366) else Color(0xFF1C1B1F),
                        label = "wl_color"
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(watchlistColorProgress)
                            .border(
                                width = 1.dp,
                                color = if (isWatchlistSelected) Color.Transparent else Color(0xFF2B2930),
                                shape = RoundedCornerShape(24.dp)
                            )
                            .clickable {
                                onToggleWatchlistFilter()
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .testTag("watchlist_filter_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isWatchlistSelected) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                                contentDescription = "Watchlist",
                                modifier = Modifier.size(14.dp),
                                tint = if (isWatchlistSelected) Color.White else Color(0xFF8B8A9F)
                            )
                            Text(
                                text = "Watchlist",
                                color = if (isWatchlistSelected) Color.White else Color(0xFF8B8A9F),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Display trending horizontally (only if not viewing watchlist only)
        if (!showWatchlistOnly && trendingList.isNotEmpty() && searchQuery.isEmpty() && selectedCategory == "All") {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "TRENDING RELEASES",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFD0BCFF),
                        letterSpacing = 1.5.sp
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(trendingList) { item ->
                            TrendingPosterCard(
                                item = item,
                                onClick = { viewModel.selectedContentId.value = item.id }
                            )
                        }
                    }
                }
            }
        }

        // Standard Explore Feed Display
        item {
            val feedTitle = when {
                showWatchlistOnly -> "MY BOOKMARKED SHOWS"
                selectedCategory != "All" -> "DISCOVER ${selectedCategory.uppercase()}S"
                else -> "EXPLORE ALL TITLES"
            }
            Text(
                text = feedTitle,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFC9C5D0),
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (displayList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF1C1B1F))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Inbox,
                            contentDescription = "Empty",
                            modifier = Modifier.size(48.dp),
                            tint = Color(0xFF381E72)
                        )
                        Text(
                            text = "No content matches this query.",
                            fontSize = 14.sp,
                            color = Color(0xFFC9C5D0),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(displayList) { item ->
                StandardFeedCard(
                    item = item,
                    watchlistIds = watchlistIds,
                    onToggleWatchlist = { viewModel.toggleWatchlist(item.id) },
                    onClick = { viewModel.selectedContentId.value = item.id }
                )
            }
        }
    }
}

// Cinematic Poster for Trending List
@Composable
fun TrendingPosterCard(
    item: ContentEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .height(150.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFF2B2930))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            PosterImageFallback(
                imageUrl = item.bannerUrl,
                title = item.title,
                modifier = Modifier.fillMaxSize()
            )

            // Dynamic Gradients overlay to reveal content details
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF0F0E13).copy(alpha = 0.95f)
                            ),
                            startY = 60f
                        )
                    )
            )

            // Rating batch (HOT indicator)
            Box(
                modifier = Modifier
                    .padding(10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFFF3366))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                    .align(Alignment.TopStart)
            ) {
                Text(
                    text = "HOT",
                    fontSize = 9.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Info bottom layer
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFD0BCFF).copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = item.contentType.uppercase(),
                            color = Color(0xFFD0BCFF),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Text(
                        text = item.genre,
                        color = Color(0xFFC9C5D0),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.title,
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Feed item card representation
@Composable
fun StandardFeedCard(
    item: ContentEntity,
    watchlistIds: Set<Int>,
    onToggleWatchlist: () -> Unit,
    onClick: () -> Unit
) {
    val isBookmarked = watchlistIds.contains(item.id)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1C1B1F)
        ),
        border = BorderStroke(1.dp, Color(0xFF2B2930))
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(130.dp)
                    .fillMaxHeight()
            ) {
                PosterImageFallback(
                    imageUrl = item.bannerUrl,
                    title = item.title,
                    modifier = Modifier.fillMaxSize()
                )

                // Side scim effect
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFF1C1B1F)
                                ),
                                startX = 260f
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.title,
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF0F0E13))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = item.rating.toString(),
                                color = Color(0xFFD0BCFF),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                    Text(
                        text = "${item.contentType} | ${item.genre} | ${item.releaseYear}",
                        color = Color(0xFF8B8A9F),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (item.firebaseSynced) {
                                    Color(0xFF00FF66).copy(alpha = 0.15f)
                                } else {
                                    Color(0xFFFFCC00).copy(alpha = 0.15f)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (item.firebaseSynced) "FIREBASE SYNCED" else "PENDING SYNC",
                            color = if (item.firebaseSynced) Color(0xFF00FF66) else Color(0xFFFFCC00),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    IconButton(
                        onClick = { onToggleWatchlist() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = "Watchlist toggle",
                            tint = if (isBookmarked) Color(0xFFFF3366) else Color(0xFF8B8A9F)
                        )
                    }
                }
            }
        }
    }
}

// Admin Portal View
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminView(
    viewModel: ContentViewModel,
    allContent: List<ContentEntity>,
    firebaseSyncState: FirebaseSyncState,
    firebaseLogs: List<String>
) {
    var titleInput by remember { mutableStateOf("") }
    var descriptionInput by remember { mutableStateOf("") }
    var bannerUrlInput by remember { mutableStateOf("") }
    var contentTypeInput by remember { mutableStateOf("Movie") } // "Movie" or "Anime"
    var genreInput by remember { mutableStateOf("") }
    var releaseYearInput by remember { mutableStateOf("") }
    var ratingInput by remember { mutableStateOf("7.5") }
    var isTrendingInput by remember { mutableStateOf(false) }

    var selectedContentForEpisode by remember { mutableStateOf<ContentEntity?>(null) }
    var epNumberInput by remember { mutableStateOf("1") }
    var epTitleInput by remember { mutableStateOf("") }
    var epDescriptionInput by remember { mutableStateOf("") }
    var epDurationInput by remember { mutableStateOf("24 min") }
    var epStreamUrlInput by remember { mutableStateOf("") }

    var adminFormTab by remember { mutableStateOf("CONTENT") } // "CONTENT", "EPISODE", "FIREBASE"

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Firebase Cloud Operations Center
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0A0815)
                ),
                border = BorderStroke(1.dp, Color(0xFFE50914).copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "FIREBASE SYNC CONTROLLER",
                                fontSize = 11.sp,
                                color = Color(0xFFFF3366),
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "Real-time Node Manager",
                                fontSize = 14.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Connected indicator tag
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF00FF66).copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                color = Color(0xFF00FF66),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Text(
                        text = "Sync pending additions or episodes securely to Firebase Firestore and Real-time Database cluster trees.",
                        fontSize = 11.sp,
                        color = Color(0xFF8B8A9F)
                    )

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.triggerFirebaseSync() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE50914),
                                contentColor = Color.White
                            ),
                            enabled = firebaseSyncState !is FirebaseSyncState.Syncing,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("sync_firebase_button")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CloudSync,
                                    contentDescription = "Sync",
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = if (firebaseSyncState is FirebaseSyncState.Syncing) "Syncing..." else "Sync Local to Firebase",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (firebaseSyncState is FirebaseSyncState.Success || firebaseSyncState is FirebaseSyncState.Error) {
                            TextButton(
                                onClick = { viewModel.resetSyncState() },
                                colors = ButtonDefaults.textButtonColors(
                                    contentColor = Color(0xFF8B8A9F)
                                )
                            ) {
                                Text("Acknowledge", fontSize = 12.sp)
                            }
                        }
                    }

                    // Sync state indicators
                    AnimatedVisibility(visible = firebaseSyncState !is FirebaseSyncState.Idle) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF13111E))
                                .border(1.dp, Color(0xFF2C2A44), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            when (firebaseSyncState) {
                                is FirebaseSyncState.Syncing -> {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            color = Color(0xFFFFCC00),
                                            strokeWidth = 2.dp
                                        )
                                        Text(
                                            text = "Syncing local sqlite nodes to Google Cloud Firebase nodes...",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFFFFCC00)
                                        )
                                    }
                                }
                                is FirebaseSyncState.Success -> {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Success",
                                            tint = Color(0xFF00FF66),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "Firebase update stream finalized! Sync index sets match.",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFF00FF66)
                                        )
                                    }
                                }
                                is FirebaseSyncState.Error -> {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Error,
                                            contentDescription = "Error",
                                            tint = Color(0xFFFF3366),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "System alert: Sync aborted",
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFFFF3366)
                                        )
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                }
            }
        }

        // Section Tabs for Forms
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF13111E))
                    .border(1.dp, Color(0xFF2C2A44), RoundedCornerShape(8.dp)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tabs = listOf("CONTENT" to "Create Show", "EPISODE" to "Add Episode", "FIREBASE" to "Web console")
                tabs.forEach { (tabKey, tabVal) ->
                    val active = adminFormTab == tabKey
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (active) Color(0xFFFFCC00) else Color.Transparent)
                            .clickable { adminFormTab = tabKey }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tabVal,
                            color = if (active) Color(0xFF0F0E17) else Color(0xFF8B8A9F),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Form Presentation depending on Tab Selection
        when (adminFormTab) {
            "CONTENT" -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF161424)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF2C2A44))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "ADD NEW MOVIE OR ANIME",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFCC00),
                                fontFamily = FontFamily.Monospace
                            )

                            // Title
                            OutlinedTextField(
                                value = titleInput,
                                onValueChange = { titleInput = it },
                                label = { Text("Title") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Description
                            OutlinedTextField(
                                value = descriptionInput,
                                onValueChange = { descriptionInput = it },
                                label = { Text("Synopsis / Description") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                            )

                            // Content Selector Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Category Type:  ",
                                    color = Color(0xFF8B8A9F),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF0D0C15))
                                        .border(1.dp, Color(0xFF2C2A44), RoundedCornerShape(8.dp))
                                ) {
                                    val opt = listOf("Movie", "Anime")
                                    opt.forEach { o ->
                                        val selected = contentTypeInput == o
                                        Box(
                                            modifier = Modifier
                                                .background(if (selected) Color(0xFFFFCC00) else Color.Transparent)
                                                .clickable { contentTypeInput = o }
                                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = o,
                                                color = if (selected) Color(0xFF08070F) else Color(0xFF8B8A9F),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            // Genre
                            OutlinedTextField(
                                value = genreInput,
                                onValueChange = { genreInput = it },
                                label = { Text("Genre (e.g. Action, Sci-Fi)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Row of 2: Release year + Rating
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = releaseYearInput,
                                    onValueChange = { releaseYearInput = it },
                                    label = { Text("Release Year") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = ratingInput,
                                    onValueChange = { ratingInput = it },
                                    label = { Text("Rating (0.0 - 10.0)") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Banner URL
                            OutlinedTextField(
                                value = bannerUrlInput,
                                onValueChange = { bannerUrlInput = it },
                                label = { Text("Banner Image URL (Optional)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Switch isTrending
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Tag as Featured/Trending Release?",
                                    color = Color(0xFF8B8A9F),
                                    fontSize = 12.sp
                                )
                                Switch(
                                    checked = isTrendingInput,
                                    onCheckedChange = { isTrendingInput = it }
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Submit Button
                            Button(
                                onClick = {
                                    if (titleInput.isNotBlank() && genreInput.isNotBlank()) {
                                        val rt = ratingInput.toDoubleOrNull() ?: 7.5
                                        viewModel.addContent(
                                            title = titleInput,
                                            description = descriptionInput,
                                            bannerUrl = bannerUrlInput,
                                            contentType = contentTypeInput,
                                            genre = genreInput,
                                            releaseYear = releaseYearInput,
                                            rating = rt,
                                            isTrending = isTrendingInput
                                        )
                                        // Clear all inputs safely
                                        titleInput = ""
                                        descriptionInput = ""
                                        bannerUrlInput = ""
                                        genreInput = ""
                                        releaseYearInput = ""
                                        ratingInput = "7.5"
                                        isTrendingInput = false
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFFFCC00),
                                    contentColor = Color(0xFF0F0E17)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("add_content_button")
                            ) {
                                Text(
                                    text = "Create and Launch Content",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }

            "EPISODE" -> {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF161424)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF2C2A44))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "ADD EPISODE SEQUENCE",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFCC00),
                                fontFamily = FontFamily.Monospace
                            )

                            // Selected content dropdown simulated
                            Text(
                                text = "Select Parent Show:",
                                color = Color(0xFF8B8A9F),
                                fontSize = 12.sp
                            )

                            // Show list
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .border(1.dp, Color(0xFF2C2A44), RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0D0C15))
                                    .padding(4.dp)
                            ) {
                                if (allContent.isEmpty()) {
                                    Text(
                                        text = "Please create a show first inside the Content creator tab.",
                                        color = Color(0xFF8B8A9F),
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                } else {
                                    LazyColumn {
                                        items(allContent) { item ->
                                            val isSel = selectedContentForEpisode?.id == item.id
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(if (isSel) Color(0xFFFFCC00).copy(alpha = 0.15f) else Color.Transparent)
                                                    .clickable { selectedContentForEpisode = item }
                                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = item.title,
                                                    color = if (isSel) Color(0xFFFFCC00) else Color.White,
                                                    fontSize = 13.sp,
                                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                                )
                                                Text(
                                                    text = item.contentType.uppercase(),
                                                    color = Color(0xFF8B8A9F),
                                                    fontSize = 10.sp,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            if (selectedContentForEpisode != null) {
                                Text(
                                    text = "Target: ${selectedContentForEpisode!!.title}",
                                    color = Color(0xFF00FF66),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = epNumberInput,
                                        onValueChange = { epNumberInput = it },
                                        label = { Text("Episode Number") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.weight(1f)
                                    )

                                    OutlinedTextField(
                                        value = epDurationInput,
                                        onValueChange = { epDurationInput = it },
                                        label = { Text("Duration (e.g. 24 min)") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                OutlinedTextField(
                                    value = epTitleInput,
                                    onValueChange = { epTitleInput = it },
                                    label = { Text("Episode Title") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = epDescriptionInput,
                                    onValueChange = { epDescriptionInput = it },
                                    label = { Text("Synopsis / Description") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(70.dp)
                                )

                                OutlinedTextField(
                                    value = epStreamUrlInput,
                                    onValueChange = { epStreamUrlInput = it },
                                    label = { Text("Streaming Stream URL (Optional)") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Button(
                                    onClick = {
                                        val parent = selectedContentForEpisode
                                        if (parent != null) {
                                            val num = epNumberInput.toIntOrNull() ?: 1
                                            viewModel.addEpisode(
                                                contentId = parent.id,
                                                episodeNumber = num,
                                                title = epTitleInput,
                                                description = epDescriptionInput,
                                                duration = epDurationInput,
                                                streamUrl = epStreamUrlInput
                                            )
                                            // Increment ep count automatically
                                            epNumberInput = (num + 1).toString()
                                            epTitleInput = ""
                                            epDescriptionInput = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFFCC00),
                                        contentColor = Color(0xFF0F0E17)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("add_episode_button")
                                ) {
                                    Text(
                                        text = "Add Episode Sequence Node",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            "FIREBASE" -> {
                // Digital read-out console for simulated backend logs
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF0A0914)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF2C2A44))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "FIREBASE LIVE CLOUD LOGS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00FF66),
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = "Buffer: OK",
                                    fontSize = 10.sp,
                                    color = Color(0xFF8B8A9F),
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            // Terminal console layout
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF04030A))
                                    .border(1.dp, Color(0xFF1E1C30), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                LazyColumn {
                                    items(firebaseLogs) { log ->
                                        Text(
                                            text = log,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp,
                                            color = Color(0xFF00FF66).copy(alpha = 0.85f),
                                            lineHeight = 16.sp,
                                            modifier = Modifier.padding(vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                            
                            Text(
                                text = "This live console prints data operations mapped securely to cloud directories. Emojis are disabled in administrative outputs.",
                                fontSize = 11.sp,
                                color = Color(0xFF8B8A9F)
                            )
                        }
                    }
                }
            }
        }

        // Section header with list of items currently on SQLite
        item {
            Text(
                text = "EXISTING DATABASE ITEMS (${allContent.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF8B8A9F),
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        if (allContent.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SQLite database contents empty.",
                        fontSize = 13.sp,
                        color = Color(0xFF8B8A9F)
                    )
                }
            }
        } else {
            items(allContent) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF12101E)
                    ),
                    border = BorderStroke(1.dp, Color(0xFF221F35))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = item.title,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${item.contentType} | ${item.genre}",
                                color = Color(0xFF8B8A9F),
                                fontSize = 11.sp
                            )
                        }

                        IconButton(
                            onClick = { viewModel.deleteContent(item) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete content item",
                                tint = Color(0xFFFF3366)
                            )
                        }
                    }
                }
            }
        }
    }
}

// Immersive Detail sheet
@Composable
fun DetailScreen(
    contentItem: ContentEntity,
    episodes: List<EpisodeEntity>,
    watchlistIds: Set<Int>,
    onClose: () -> Unit,
    onToggleWatchlist: () -> Unit,
    onPlayEpisode: (EpisodeEntity) -> Unit
) {
    val isBookmarked = watchlistIds.contains(contentItem.id)

    Column(modifier = Modifier.fillMaxSize()) {
        // Upper banner backdrop image block with close button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            PosterImageFallback(
                imageUrl = contentItem.bannerUrl,
                title = contentItem.title,
                modifier = Modifier.fillMaxSize()
            )

            // Custom cinematic shadow overlays
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFF131122)
                            ),
                            startY = 100f
                        )
                    )
            )

            // Header close and watchlist control buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Close screen",
                        tint = Color.White
                    )
                }

                IconButton(
                    onClick = onToggleWatchlist,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.6f))
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Toggle bookmark watchlist",
                        tint = if (isBookmarked) Color(0xFFFFCC00) else Color.White
                    )
                }
            }

            // Info overlaid on backdrop
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFCC00))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = contentItem.contentType.uppercase(),
                            color = Color(0xFF0F0E17),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Text(
                        text = "Year ${contentItem.releaseYear}",
                        color = Color(0xFF8B8A9F),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFCC00),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = contentItem.rating.toString(),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = contentItem.title,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }

        // Lower body section containing layout details & episode sequence lists
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Genre tag line
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Genre:  ",
                        color = Color(0xFF8B8A9F),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = contentItem.genre,
                        color = Color(0xFFFFCC00),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Synopsis Text
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "SYNOPSIS",
                        fontSize = 11.sp,
                        color = Color(0xFF8B8A9F),
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = contentItem.description,
                        fontSize = 13.sp,
                        color = Color(0xFFD4D3E6),
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Justify
                    )
                }
            }

            // Sync Status
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1B192A))
                        .border(1.dp, Color(0xFF2C2A44), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Cloud replication node",
                            fontSize = 12.sp,
                            color = Color(0xFF8B8A9F)
                        )
                        Text(
                            text = if (contentItem.firebaseSynced) "ACTIVE INSTANCE" else "OFFLINE LOCAL",
                            color = if (contentItem.firebaseSynced) Color(0xFF00FF66) else Color(0xFFFFCC00),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Episode item blocks
            item {
                Text(
                    text = "EPISODES LISTING (${episodes.size})",
                    fontSize = 11.sp,
                    color = Color(0xFF8B8A9F),
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )
            }

            if (episodes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1B192A))
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No episodes released yet for this show.",
                            fontSize = 13.sp,
                            color = Color(0xFF8B8A9F)
                        )
                    }
                }
            } else {
                items(episodes) { ep ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPlayEpisode(ep) },
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1B182B)
                        ),
                        border = BorderStroke(1.dp, Color(0xFF2C274E))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Video Icon representation with visual play button style
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F0E17))
                                    .border(1.dp, Color(0xFF2C274E), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play Episode",
                                    tint = Color(0xFFFFCC00),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Node ${ep.episodeNumber}: ${ep.title}",
                                        color = Color.White,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = ep.duration,
                                        color = Color(0xFF8B8A9F),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = ep.description,
                                    color = Color(0xFF8B8A9F),
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Fullscreen Immersive TV/Video Player Screen Simulation
@Composable
fun ImmersivePlayerScreen(
    contentItem: ContentEntity,
    episode: EpisodeEntity,
    onClose: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var streamProgress by remember { mutableFloatStateOf(15.0f) }
    var bufferingFraction by remember { mutableStateOf(24) }

    // Periodically update progress slider + buffer simulator to feel real
    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (true) {
                kotlinx.coroutines.delay(1000)
                if (streamProgress < 100f) {
                    streamProgress += 0.5f
                }
                if (bufferingFraction < 100) {
                    bufferingFraction += (2..8).random()
                    if (bufferingFraction > 100) bufferingFraction = 100
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF06050A))
    ) {
        // High contrast grid background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellWidth = 40.dp.toPx()
            val totalX = size.width / cellWidth
            val totalY = size.height / cellWidth
            for (i in 0..totalX.toInt()) {
                drawLine(
                    color = Color(0xFF161424).copy(alpha = 0.3f),
                    start = Offset(i * cellWidth, 0f),
                    end = Offset(i * cellWidth, size.height),
                    strokeWidth = 1f
                )
            }
            for (j in 0..totalY.toInt()) {
                drawLine(
                    color = Color(0xFF161424).copy(alpha = 0.3f),
                    start = Offset(0f, j * cellWidth),
                    end = Offset(size.width, j * cellWidth),
                    strokeWidth = 1f
                )
            }
        }

        // Glowing center player loop
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Rotating graphic disk/visual feedback representing audio/video wave (since actual video not rendered client-side)
            val rotationTransition = rememberInfiniteTransition(label = "rot_trans")
            val angle by rotationTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(4000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rot_angle"
            )

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .drawBehind {
                        drawCircle(
                            Brush.sweepGradient(
                                colors = listOf(
                                    Color(0xFFFFCC00),
                                    Color(0xFFFF3366),
                                    Color(0xFF00AAFF),
                                    Color(0xFFFFCC00)
                                )
                            ),
                            alpha = if (isPlaying) 0.8f else 0.2f
                        )
                    }
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF000000))
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.MusicVideo else Icons.Default.Pause,
                        contentDescription = "Buffering stream",
                        tint = if (isPlaying) Color(0xFFFFCC00) else Color(0xFF8B8A9F),
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = if (isPlaying) "PLAYING" else "PAUSED",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = if (isPlaying) Color(0xFFFFCC00) else Color(0xFF8B8A9F),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Read-out buffering telemetry details (NO EMOJIS)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Source Stream: ${episode.streamUrl}",
                    fontSize = 11.sp,
                    color = Color(0xFF8B8A9F),
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = if (bufferingFraction < 100) {
                        "Buffering node structures: $bufferingFraction percent"
                    } else {
                        "Connection secured: stable telemetry stream"
                    },
                    fontSize = 11.sp,
                    color = if (bufferingFraction < 100) Color(0xFFFFCC00) else Color(0xFF00FF66),
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Floating Back close trigger top left
        IconButton(
            onClick = onClose,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .testTag("close_player_button")
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close immersive player",
                tint = Color.White
            )
        }

        // Streaming details top center
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = contentItem.title.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFFFF3366),
                letterSpacing = 1.sp
            )
            Text(
                text = "EPISODE ${episode.episodeNumber}: ${episode.title}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // TV Player Panel block at footer bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF04030A)
                        )
                    )
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Slider timeline progress bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val progressMinutes = (streamProgress * 0.24).toInt()
                    val progressSeconds = ((streamProgress * 0.24 - progressMinutes) * 60).toInt()
                    val formattedElapsed = String.format("%02d:%02d", progressMinutes, progressSeconds)
                    
                    Text(
                        text = formattedElapsed,
                        fontSize = 11.sp,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = episode.duration,
                        fontSize = 11.sp,
                        color = Color(0xFF8B8A9F),
                        fontFamily = FontFamily.Monospace
                    )
                }

                Slider(
                    value = streamProgress,
                    onValueChange = { streamProgress = it },
                    valueRange = 0.0f..100.0f,
                    colors = SliderDefaults.colors(
                        activeTrackColor = Color(0xFFFFCC00),
                        inactiveTrackColor = Color(0xFF1A162B),
                        thumbColor = Color(0xFFFF3366)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Core control trigger actions (Return, Play/Pause, Skip)
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { streamProgress = (streamProgress - 10f).coerceAtLeast(0f) },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF13111E))
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay10,
                        contentDescription = "Rewind 10 seconds",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFCC00))
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause flow" else "Resume flow",
                        tint = Color(0xFF0A0914),
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(
                    onClick = { streamProgress = (streamProgress + 10f).coerceIn(0f, 100f) },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF13111E))
                ) {
                    Icon(
                        imageVector = Icons.Default.Forward10,
                        contentDescription = "Forward 10 seconds",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

// Resilient Poster visual builder that parses image or falls back to stylized grid canvas
@Composable
fun PosterImageFallback(
    imageUrl: String,
    title: String,
    modifier: Modifier = Modifier
) {
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        if (!isError) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                onLoading = {
                    isLoading = true
                    isError = false
                },
                onSuccess = {
                    isLoading = false
                    isError = false
                },
                onError = {
                    isLoading = false
                    isError = true
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Resilient customized tech canvas fallback designed with cyber slates
        if (isError || isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0F0E17),
                                Color(0xFF1A162B)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Cross geometric lines to represent cinema art projection
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawLine(
                        color = Color(0xFF342E52).copy(alpha = 0.15f),
                        start = Offset(0f, 0f),
                        end = Offset(size.width, size.height),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = Color(0xFF342E52).copy(alpha = 0.15f),
                        start = Offset(size.width, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = 2f
                    )
                }

                // Title overlay letters fallback
                Text(
                    text = if (title.length > 2) title.take(2).uppercase() else "MA",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFFCC00).copy(alpha = 0.45f),
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
                
                // Continuous small buffering visual on top of graphic
                if (isLoading && !isError) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color(0xFFFFCC00),
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}
