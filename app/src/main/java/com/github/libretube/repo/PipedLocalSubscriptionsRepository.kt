package dev.jch0029987.libretibs.repo

import dev.jch0029987.libretibs.api.RetrofitInstance
import dev.jch0029987.libretibs.api.SubscriptionHelper.GET_SUBSCRIPTIONS_LIMIT
import dev.jch0029987.libretibs.api.obj.Subscription
import dev.jch0029987.libretibs.db.DatabaseHolder.Database
import dev.jch0029987.libretibs.db.obj.LocalSubscription

class PipedLocalSubscriptionsRepository : SubscriptionsRepository {
    override suspend fun subscribe(
        channelId: String, name: String, uploaderAvatar: String?, verified: Boolean
    ) {
        // further meta info is not needed when using Piped local subscriptions
        Database.localSubscriptionDao().insert(LocalSubscription(channelId))
    }

    override suspend fun importSubscriptions(newChannels: List<String>) {
        // further meta info is not needed when using Piped local subscriptions
        Database.localSubscriptionDao().insertAll(newChannels.map { LocalSubscription(it) })
    }

    override suspend fun isSubscribed(channelId: String): Boolean {
        return Database.localSubscriptionDao().includes(channelId)
    }

    override suspend fun unsubscribe(channelId: String) {
        Database.localSubscriptionDao().deleteById(channelId)
    }

    override suspend fun getSubscriptions(): List<Subscription> {
        val channelIds = getSubscriptionChannelIds()

        return when {
            channelIds.size > GET_SUBSCRIPTIONS_LIMIT -> RetrofitInstance.authApi.unauthenticatedSubscriptions(
                    channelIds
                )

            else -> RetrofitInstance.authApi.unauthenticatedSubscriptions(
                channelIds.joinToString(",")
            )
        }
    }

    override suspend fun getSubscriptionChannelIds(): List<String> {
        return Database.localSubscriptionDao().getAll().map { it.channelId }
    }
}