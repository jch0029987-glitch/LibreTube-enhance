package dev.jch0029987.libretibs.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.ListAdapter
import dev.jch0029987.libretibs.api.obj.Subscription
import dev.jch0029987.libretibs.constants.IntentData
import dev.jch0029987.libretibs.databinding.ChannelSubscriptionRowBinding
import dev.jch0029987.libretibs.extensions.toID
import dev.jch0029987.libretibs.helpers.ContextHelper
import dev.jch0029987.libretibs.helpers.ImageHelper
import dev.jch0029987.libretibs.helpers.NavigationHelper
import dev.jch0029987.libretibs.ui.adapters.callbacks.DiffUtilItemCallback
import dev.jch0029987.libretibs.ui.base.BaseActivity
import dev.jch0029987.libretibs.ui.extensions.setupSubscriptionButton
import dev.jch0029987.libretibs.ui.sheets.ChannelOptionsBottomSheet
import dev.jch0029987.libretibs.ui.viewholders.SubscriptionChannelViewHolder

class SubscriptionChannelAdapter :
    ListAdapter<Subscription, SubscriptionChannelViewHolder>(DiffUtilItemCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SubscriptionChannelViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ChannelSubscriptionRowBinding.inflate(layoutInflater, parent, false)
        return SubscriptionChannelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubscriptionChannelViewHolder, position: Int) {
        val subscription = getItem(holder.bindingAdapterPosition)

        holder.binding.apply {
            subscriptionChannelName.text = subscription.name
            ImageHelper.loadImage(subscription.avatar, subscriptionChannelImage, true)

            root.setOnClickListener {
                NavigationHelper.navigateChannel(root.context, subscription.url)
            }
            root.setOnLongClickListener {
                val channelOptionsSheet = ChannelOptionsBottomSheet()
                channelOptionsSheet.arguments = bundleOf(
                    IntentData.channelId to subscription.url.toID(),
                    IntentData.channelName to subscription.name,
                    IntentData.isSubscribed to true
                )
                val activity = ContextHelper.unwrapActivity<BaseActivity>(root.context)
                channelOptionsSheet.show(activity.supportFragmentManager)
                true
            }

            subscriptionSubscribe.setupSubscriptionButton(
                subscription.url.toID(),
                subscription.name,
                subscription.avatar,
                subscription.verified,
                notificationBell,
                true
            )
        }
    }
}
