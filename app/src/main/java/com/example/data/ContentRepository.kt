package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.delay

class ContentRepository(private val contentDao: ContentDao) {
    val allContent: Flow<List<ContentEntity>> = contentDao.getAllContent()

    fun getContentById(id: Int): Flow<ContentEntity?> = contentDao.getContentById(id)

    fun getContentByType(type: String): Flow<List<ContentEntity>> = contentDao.getContentByType(type)

    fun getEpisodesForContent(contentId: Int): Flow<List<EpisodeEntity>> = contentDao.getEpisodesForContent(contentId)

    suspend fun insertContent(content: ContentEntity): Long {
        return contentDao.insertContent(content)
    }

    suspend fun updateContent(content: ContentEntity) {
        contentDao.updateContent(content)
    }

    suspend fun deleteContent(content: ContentEntity) {
        contentDao.deleteContent(content)
    }

    suspend fun insertEpisode(episode: EpisodeEntity): Long {
        return contentDao.insertEpisode(episode)
    }

    suspend fun deleteEpisode(episode: EpisodeEntity) {
        contentDao.deleteEpisode(episode)
    }

    // Simulate real Firebase operations (REST/Firestore cloud upload)
    suspend fun simulateFirebaseSync(): Boolean {
        // Set all local data as unsynced temporarily to show loading states and animations
        contentDao.updateAllContentSyncStatus(false)
        contentDao.updateAllEpisodesSyncStatus(false)
        
        // Network delay to simulate server communication
        delay(1500)
        
        // Sync everything back
        contentDao.updateAllContentSyncStatus(true)
        contentDao.updateAllEpisodesSyncStatus(true)
        return true
    }
}
