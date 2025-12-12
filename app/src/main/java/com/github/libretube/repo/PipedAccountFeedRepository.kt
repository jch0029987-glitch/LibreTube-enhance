package dev.jch0029987.libretibs.repo

import dev.jch0029987.libretibs.api.RetrofitInstance
import dev.jch0029987.libretibs.api.obj.StreamItem
import dev.jch0029987.libretibs.helpers.PreferenceHelper

class PipedAccountFeedRepository : FeedRepository {
    override suspend fun getFeed(
        forceRefresh: Boolean,
        onProgressUpdate: (FeedProgress) -> Unit
    ): List<StreamItem> {
        val token = PreferenceHelper.getToken()

        return RetrofitInstance.authApi.getFeed(token)
    }
}