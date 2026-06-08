package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {
    @Query("SELECT * FROM movies_anime ORDER BY id DESC")
    fun getAllContent(): Flow<List<ContentEntity>>

    @Query("SELECT * FROM movies_anime WHERE id = :id LIMIT 1")
    fun getContentById(id: Int): Flow<ContentEntity?>

    @Query("SELECT * FROM movies_anime WHERE contentType = :type ORDER BY id DESC")
    fun getContentByType(type: String): Flow<List<ContentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: ContentEntity): Long

    @Update
    suspend fun updateContent(content: ContentEntity)

    @Delete
    suspend fun deleteContent(content: ContentEntity)

    @Query("SELECT * FROM episodes WHERE contentId = :contentId ORDER BY episodeNumber ASC")
    fun getEpisodesForContent(contentId: Int): Flow<List<EpisodeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisode(episode: EpisodeEntity): Long

    @Delete
    suspend fun deleteEpisode(episode: EpisodeEntity)

    // Helper queries for firebase sync operations
    @Query("UPDATE movies_anime SET firebaseSynced = :synced")
    suspend fun updateAllContentSyncStatus(synced: Boolean)

    @Query("UPDATE episodes SET firebaseSynced = :synced")
    suspend fun updateAllEpisodesSyncStatus(synced: Boolean)
}
