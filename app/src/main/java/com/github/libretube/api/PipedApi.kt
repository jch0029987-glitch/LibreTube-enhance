package dev.jch0029987.libretibs.api

import dev.jch0029987.libretibs.api.obj.Channel
import dev.jch0029987.libretibs.api.obj.ChannelTabResponse
import dev.jch0029987.libretibs.api.obj.CommentsPage
import dev.jch0029987.libretibs.api.obj.DeArrowContent
import dev.jch0029987.libretibs.api.obj.Playlist
import dev.jch0029987.libretibs.api.obj.SearchResult
import dev.jch0029987.libretibs.api.obj.SegmentData
import dev.jch0029987.libretibs.api.obj.StreamItem
import dev.jch0029987.libretibs.api.obj.Streams
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PipedApi {
    @GET("trending")
    suspend fun getTrending(@Query("region") region: String): List<StreamItem>

    @GET("streams/{videoId}")
    suspend fun getStreams(@Path("videoId") videoId: String): Streams

    @GET("comments/{videoId}")
    suspend fun getComments(@Path("videoId") videoId: String): CommentsPage

    @GET("sponsors/{videoId}")
    suspend fun getSegments(
        @Path("videoId") videoId: String,
        @Query("category") category: String,
        @Query("actionType") actionType: String? = null
    ): SegmentData

    @GET("dearrow")
    suspend fun getDeArrowContent(@Query("videoIds") videoIds: String): Map<String, DeArrowContent>

    @GET("nextpage/comments/{videoId}")
    suspend fun getCommentsNextPage(
        @Path("videoId") videoId: String,
        @Query("nextpage") nextPage: String
    ): CommentsPage

    @GET("search")
    suspend fun getSearchResults(
        @Query("q") searchQuery: String,
        @Query("filter") filter: String
    ): SearchResult

    @GET("nextpage/search")
    suspend fun getSearchResultsNextPage(
        @Query("q") searchQuery: String,
        @Query("filter") filter: String,
        @Query("nextpage") nextPage: String
    ): SearchResult

    @GET("suggestions")
    suspend fun getSuggestions(@Query("query") query: String): List<String>

    @GET("channel/{channelId}")
    suspend fun getChannel(@Path("channelId") channelId: String): Channel

    @GET("channels/tabs")
    suspend fun getChannelTab(
        @Query("data") data: String,
        @Query("nextpage") nextPage: String? = null
    ): ChannelTabResponse

    @GET("user/{name}")
    suspend fun getChannelByName(@Path("name") channelName: String): Channel

    @GET("nextpage/channel/{channelId}")
    suspend fun getChannelNextPage(
        @Path("channelId") channelId: String,
        @Query("nextpage") nextPage: String
    ): Channel

    @GET("playlists/{playlistId}")
    suspend fun getPlaylist(@Path("playlistId") playlistId: String): Playlist

    @GET("nextpage/playlists/{playlistId}")
    suspend fun getPlaylistNextPage(
        @Path("playlistId") playlistId: String,
        @Query("nextpage") nextPage: String
    ): Playlist
}
