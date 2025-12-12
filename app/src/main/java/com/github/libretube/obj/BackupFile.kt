package dev.jch0029987.libretibs.obj

import dev.jch0029987.libretibs.db.obj.CustomInstance
import dev.jch0029987.libretibs.db.obj.LocalPlaylistWithVideos
import dev.jch0029987.libretibs.db.obj.LocalSubscription
import dev.jch0029987.libretibs.db.obj.PlaylistBookmark
import dev.jch0029987.libretibs.db.obj.SearchHistoryItem
import dev.jch0029987.libretibs.db.obj.SubscriptionGroup
import dev.jch0029987.libretibs.db.obj.WatchHistoryItem
import dev.jch0029987.libretibs.db.obj.WatchPosition
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class BackupFile(
    //
    // some stuff for compatibility with Piped imports
    //
    val format: String = "Piped",
    val version: Int = 1,

    //
    // only compatible with LibreTube itself, database objects
    //
    var watchHistory: List<WatchHistoryItem>? = emptyList(),
    var watchPositions: List<WatchPosition>? = emptyList(),
    var searchHistory: List<SearchHistoryItem>? = emptyList(),
    var customInstances: List<CustomInstance>? = emptyList(),
    var playlistBookmarks: List<PlaylistBookmark>? = emptyList(),

    //
    // Preferences, stored as a key value map
    //
    var preferences: List<PreferenceItem>? = emptyList(),

    //
    // Database objects with compatibility for Piped imports/exports
    //
    @JsonNames("groups", "channelGroups")
    var groups: List<SubscriptionGroup>? = emptyList(),

    @JsonNames("subscriptions", "localSubscriptions")
    var subscriptions: List<LocalSubscription>? = emptyList(),

    // playlists are exported in two different formats because the formats differ too much unfortunately
    var localPlaylists: List<LocalPlaylistWithVideos>? = emptyList(),
    var playlists: List<PipedImportPlaylist>? = emptyList()
)
