package dev.jch0029987.libretibs.api

import androidx.annotation.StringRes
import dev.jch0029987.libretibs.R
import dev.jch0029987.libretibs.api.obj.Channel
import dev.jch0029987.libretibs.api.obj.ChannelTabResponse
import dev.jch0029987.libretibs.api.obj.CommentsPage
import dev.jch0029987.libretibs.api.obj.DeArrowContent
import dev.jch0029987.libretibs.api.obj.Playlist
import dev.jch0029987.libretibs.api.obj.SearchResult
import dev.jch0029987.libretibs.api.obj.SegmentData
import dev.jch0029987.libretibs.api.obj.StreamItem
import dev.jch0029987.libretibs.api.obj.Streams
import dev.jch0029987.libretibs.helpers.PlayerHelper

interface MediaServiceRepository {
    fun getTrendingCategories(): List<TrendingCategory>

    suspend fun getTrending(region: String, category: TrendingCategory): List<StreamItem>
    suspend fun getStreams(videoId: String): Streams
    suspend fun getComments(videoId: String): CommentsPage
    suspend fun getSegments(
        videoId: String,
        category: List<String>,
        actionType: List<String>? = null
    ): SegmentData

    suspend fun getDeArrowContent(videoIds: String): Map<String, DeArrowContent>
    suspend fun getCommentsNextPage(videoId: String, nextPage: String): CommentsPage
    suspend fun getSearchResults(searchQuery: String, filter: String): SearchResult
    suspend fun getSearchResultsNextPage(
        searchQuery: String,
        filter: String,
        nextPage: String
    ): SearchResult

    suspend fun getSuggestions(query: String): List<String>
    suspend fun getChannel(channelId: String): Channel
    suspend fun getChannelTab(data: String, nextPage: String? = null): ChannelTabResponse
    suspend fun getChannelByName(channelName: String): Channel
    suspend fun getChannelNextPage(channelId: String, nextPage: String): Channel
    suspend fun getPlaylist(playlistId: String): Playlist
    suspend fun getPlaylistNextPage(playlistId: String, nextPage: String): Playlist

    companion object {
        val instance: MediaServiceRepository
            get() = when {
                PlayerHelper.fullLocalMode -> NewPipeMediaServiceRepository()
                PlayerHelper.localStreamExtraction -> LocalStreamsExtractionPipedMediaServiceRepository()
                else -> PipedMediaServiceRepository()
            }
    }
}

enum class TrendingCategory(@StringRes val titleRes: Int) {
    GAMING(R.string.gaming),
    TRAILERS(R.string.trailers),
    PODCASTS(R.string.podcasts),
    MUSIC(R.string.music),
    LIVE(R.string.live)
}