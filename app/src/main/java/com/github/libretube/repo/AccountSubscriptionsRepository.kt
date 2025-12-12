package dev.jch0029987.libretibs.repo

import dev.jch0029987.libretibs.api.RetrofitInstance
import dev.jch0029987.libretibs.api.obj.Subscribe
import dev.jch0029987.libretibs.api.obj.Subscription
import dev.jch0029987.libretibs.extensions.toID
import dev.jch0029987.libretibs.helpers.PreferenceHelper

class AccountSubscriptionsRepository : SubscriptionsRepository {
    private val token get() = PreferenceHelper.getToken()

    override suspend fun subscribe(
        channelId: String, name: String, uploaderAvatar: String?, verified: Boolean
    ) {
        runCatching {
            RetrofitInstance.authApi.subscribe(token, Subscribe(channelId))
        }
    }

    override suspend fun unsubscribe(channelId: String) {
        runCatching {
            RetrofitInstance.authApi.unsubscribe(token, Subscribe(channelId))
        }
    }

    override suspend fun isSubscribed(channelId: String): Boolean? {
        return runCatching {
            RetrofitInstance.authApi.isSubscribed(channelId, token)
        }.getOrNull()?.subscribed
    }

    override suspend fun importSubscriptions(newChannels: List<String>) {
        RetrofitInstance.authApi.importSubscriptions(false, token, newChannels)
    }

    override suspend fun getSubscriptions(): List<Subscription> {
        return RetrofitInstance.authApi.subscriptions(token)
    }

    override suspend fun getSubscriptionChannelIds(): List<String> {
        return getSubscriptions().map { it.url.toID() }
    }
}