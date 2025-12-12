package dev.jch0029987.libretibs.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import dev.jch0029987.libretibs.databinding.AddChannelToGroupRowBinding
import dev.jch0029987.libretibs.db.obj.SubscriptionGroup
import dev.jch0029987.libretibs.ui.adapters.callbacks.DiffUtilItemCallback
import dev.jch0029987.libretibs.ui.viewholders.AddChannelToGroupViewHolder

class AddChannelToGroupAdapter(
    private val channelId: String
) : ListAdapter<SubscriptionGroup, AddChannelToGroupViewHolder>(DiffUtilItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddChannelToGroupViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AddChannelToGroupRowBinding.inflate(layoutInflater, parent, false)
        return AddChannelToGroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddChannelToGroupViewHolder, position: Int) {
        val channelGroup = getItem(holder.bindingAdapterPosition)

        holder.binding.apply {
            groupName.text = channelGroup.name
            groupCheckbox.isChecked = channelGroup.channels.contains(channelId)

            groupCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    channelGroup.channels += channelId
                } else {
                    channelGroup.channels -= channelId
                }
            }
        }
    }
}
