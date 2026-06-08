package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ContentEntity::class, EpisodeEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "movie_anime_hub_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.contentDao())
                }
            }
        }

        suspend fun populateDatabase(dao: ContentDao) {
            // Check if already populated to avoid duplicate populates
            // Feed preloaded movies and anime (NO EMOJIS ALLOWED!)
            val voyagerId = dao.insertContent(
                ContentEntity(
                    title = "The Cosmic Voyager",
                    description = "An astronaut finds himself stranded inside a massive metallic celestial body. He must solve ancient mechanical codes to chart a course back Home.",
                    bannerUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?q=80&w=600",
                    contentType = "Movie",
                    genre = "Sci-Fi",
                    releaseYear = "2024",
                    rating = 8.8,
                    isTrending = true,
                    firebaseSynced = true
                )
            )

            val samuraiId = dao.insertContent(
                ContentEntity(
                    title = "Shadow of Samurai",
                    description = "In a late-Edo neo-noir landscape, a wandering Ronin uncovers a mystical artifact that grants him control over shadows, making him a target for elite ninja clans.",
                    bannerUrl = "https://images.unsplash.com/photo-1542751371-adc38448a05e?q=80&w=600",
                    contentType = "Movie",
                    genre = "Action",
                    releaseYear = "2023",
                    rating = 9.1,
                    isTrending = true,
                    firebaseSynced = true
                )
            )

            val chronoId = dao.insertContent(
                ContentEntity(
                    title = "Chrono Nexus",
                    description = "A teenage clockmaker discovers his family clock is linked to parallel futuristic realities. Together with a mysterious rebel from year 3042, they battle a chrono-syndicate.",
                    bannerUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?q=80&w=600",
                    contentType = "Anime",
                    genre = "Sci-Fi / Shonen",
                    releaseYear = "2025",
                    rating = 9.4,
                    isTrending = true,
                    firebaseSynced = true
                )
            )

            val cyberpunkId = dao.insertContent(
                ContentEntity(
                    title = "Cyberpunk Echoes",
                    description = "A street netrunner accidentally intercepts a prototype AI that possesses physical holographic capabilities inside a neon-lit futuristic sprawl.",
                    bannerUrl = "https://images.unsplash.com/photo-1515621061946-eff1c2a352bd?q=80&w=600",
                    contentType = "Anime",
                    genre = "Cybertech / Sci-Fi",
                    releaseYear = "2024",
                    rating = 8.6,
                    isTrending = false,
                    firebaseSynced = true
                )
            )

            // Insert initial episodes for Cosmic Voyager (Movie)
            dao.insertEpisode(
                EpisodeEntity(
                    contentId = voyagerId.toInt(),
                    episodeNumber = 1,
                    title = "Part 1 Singular Descent",
                    description = "The pod crashes on the exterior of a metallic moon, sparking a desperate struggle for atmosphere calibration.",
                    duration = "45 min",
                    streamUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
                    firebaseSynced = true
                )
            )
            dao.insertEpisode(
                EpisodeEntity(
                    contentId = voyagerId.toInt(),
                    episodeNumber = 2,
                    title = "Part 2 Core Protocols",
                    description = "Discovering the ancient clockwork central terminal and decoding the navigation log.",
                    duration = "52 min",
                    streamUrl = "https://www.w3schools.com/html/movie.mp4",
                    firebaseSynced = true
                )
            )

            // Insert initial episodes for Chrono Nexus (Anime)
            dao.insertEpisode(
                EpisodeEntity(
                    contentId = chronoId.toInt(),
                    episodeNumber = 1,
                    title = "Nexus Genesis",
                    description = "Ren discovers the mainspring inside his grandfather's vintage clock can shift coordinates in space-time.",
                    duration = "24 min",
                    streamUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
                    firebaseSynced = true
                )
            )
            dao.insertEpisode(
                EpisodeEntity(
                    contentId = chronoId.toInt(),
                    episodeNumber = 2,
                    title = "The Rebel of 3042",
                    description = "Meeting Akira, a pilot from the ruined neo-tokyo timeline, and avoiding temporal enforcers.",
                    duration = "24 min",
                    streamUrl = "https://www.w3schools.com/html/movie.mp4",
                    firebaseSynced = true
                )
            )
            dao.insertEpisode(
                EpisodeEntity(
                    contentId = chronoId.toInt(),
                    episodeNumber = 3,
                    title = "Gears of Destiny",
                    description = "Akira and Ren try to repair the timegate core as dangerous hunters infiltrate the watch shop.",
                    duration = "24 min",
                    streamUrl = "https://www.w3schools.com/html/mov_bbb.mp4",
                    firebaseSynced = true
                )
            )
        }
    }
}
