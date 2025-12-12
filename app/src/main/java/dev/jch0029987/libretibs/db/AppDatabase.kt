package dev.jch0029987.libretibs.db

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.jch0029987.libretibs.db.dao.CustomInstanceDao
import dev.jch0029987.libretibs.db.dao.DownloadDao
import dev.jch0029987.libretibs.db.dao.LocalPlaylistsDao
import dev.jch0029987.libretibs.db.dao.LocalSubscriptionDao
import dev.jch0029987.libretibs.db.dao.PlaylistBookmarkDao
import dev.jch0029987.libretibs.db.dao.SearchHistoryDao
import dev.jch0029987.libretibs.db.dao.SubscriptionGroupsDao
import dev.jch0029987.libretibs.db.dao.SubscriptionsFeedDao
import dev.jch0029987.libretibs.db.dao.WatchHistoryDao
import dev.jch0029987.libretibs.db.dao.WatchPositionDao
import dev.jch0029987.libretibs.db.obj.CustomInstance
import dev.jch0029987.libretibs.db.obj.Download
import dev.jch0029987.libretibs.db.obj.DownloadChapter
import dev.jch0029987.libretibs.db.obj.DownloadItem
import dev.jch0029987.libretibs.db.obj.DownloadPlaylist
import dev.jch0029987.libretibs.db.obj.DownloadPlaylistVideosCrossRef
import dev.jch0029987.libretibs.db.obj.LocalPlaylist
import dev.jch0029987.libretibs.db.obj.LocalPlaylistItem
import dev.jch0029987.libretibs.db.obj.LocalSubscription
import dev.jch0029987.libretibs.db.obj.PlaylistBookmark
import dev.jch0029987.libretibs.db.obj.SearchHistoryItem
import dev.jch0029987.libretibs.db.obj.SubscriptionGroup
import dev.jch0029987.libretibs.db.obj.SubscriptionsFeedItem
import dev.jch0029987.libretibs.db.obj.WatchHistoryItem
import dev.jch0029987.libretibs.db.obj.WatchPosition

@Database(
    entities = [
        WatchHistoryItem::class,
        WatchPosition::class,
        SearchHistoryItem::class,
        CustomInstance::class,
        LocalSubscription::class,
        PlaylistBookmark::class,
        LocalPlaylist::class,
        LocalPlaylistItem::class,
        Download::class,
        DownloadItem::class,
        DownloadChapter::class,
        DownloadPlaylist::class,
        DownloadPlaylistVideosCrossRef::class,
        SubscriptionGroup::class,
        SubscriptionsFeedItem::class
    ],
    version = 21,
