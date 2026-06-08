package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "movies_anime")
data class ContentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val bannerUrl: String,
    val contentType: String, // "Movie" or "Anime"
    val genre: String,
    val releaseYear: String,
    val rating: Double,
    val isTrending: Boolean = false,
    val firebaseSynced: Boolean = false
)

@Entity(
    tableName = "episodes",
    foreignKeys = [
        ForeignKey(
            entity = ContentEntity::class,
            parentColumns = ["id"],
            childColumns = ["contentId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class EpisodeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val contentId: Int, // Refers to ContentEntity
    val episodeNumber: Int,
    val title: String,
    val description: String,
    val duration: String,
    val streamUrl: String,
    val firebaseSynced: Boolean = false
)
