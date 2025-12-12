package dev.jch0029987.libretibs.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.jch0029987.libretibs.db.dao.*
import dev.jch0029987.libretibs.db.obj.*

@Database(
    entities = [
        Subscription::class,
        WatchHistoryEntity::class,
        SearchHistoryItem::class,
        LocalPlaylist::class,
        PlaylistBookmark::class,
        Download::class,
        DownloadItem::class,
        DownloadPlaylist::class,
        DownloadPlaylistVideosCrossRef::class,
        WatchPosition::class
    ],
    version = 20,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun watchHistoryDao(): WatchHistoryDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun localPlaylistDao(): LocalPlaylistDao
    abstract fun playlistBookmarkDao(): PlaylistBookmarkDao
    abstract fun downloadDao(): DownloadDao
    abstract fun watchPositionDao(): WatchPositionDao
}
