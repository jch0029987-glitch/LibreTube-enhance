package dev.jch0029987.libretibs.repo

import dev.jch0029987.libretibs.api.RetrofitInstance
import dev.jch0029987.libretibs.api.SubscriptionHelper
import dev.jch0029987.libretibs.api.SubscriptionHelper.GET_SUBSCRIPTIONS_LIMIT
import dev.jch0029987.libretibs.api.obj.StreamItem

class PipedNoAccountFeedRepository : FeedRepository {
    override suspend fun getFeed(
        forceRefresh: Boolean,
        onProgressUpdate: (FeedProgress) -> Unit
    ): List<StreamItem> {
        val channelIds = SubscriptionHelper.getSubscriptionChannelIds()

        return when {
            channelIds.size > GET_SUBSCRIPTIONS_LIMIT ->
                RetrofitInstance.authApi
                    .getUnauthenticatedFeed(channelIds)

            else -> RetrofitInstance.authApi.getUnauthenticatedFeed(
                channelIds.joinToString(",")
            )
        }
    }
}