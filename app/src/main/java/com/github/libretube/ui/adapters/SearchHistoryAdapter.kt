package dev.jch0029987.libretibs.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import dev.jch0029987.libretibs.databinding.SuggestionRowBinding
import dev.jch0029987.libretibs.db.DatabaseHolder.Database
import dev.jch0029987.libretibs.db.obj.SearchHistoryItem
import dev.jch0029987.libretibs.ui.adapters.callbacks.DiffUtilItemCallback
import dev.jch0029987.libretibs.ui.viewholders.SuggestionsViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class SearchHistoryAdapter(
    private val onRootClickListener: (String) -> Unit,
    private val onArrowClickListener: (String) -> Unit,
) : ListAdapter<String, SuggestionsViewHolder>(DiffUtilItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuggestionsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = SuggestionRowBinding.inflate(layoutInflater, parent, false)
        return SuggestionsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SuggestionsViewHolder, position: Int) {
        val historyQuery = getItem(holder.bindingAdapterPosition)
        holder.binding.apply {
            suggestionText.text = historyQuery

            deleteHistory.isVisible = true

            deleteHistory.setOnClickListener {
                val updatedList =  currentList.toMutableList().also {
                    it.remove(historyQuery)
                }
                runBlocking(Dispatchers.IO) {
                    Database.searchHistoryDao().delete(SearchHistoryItem(historyQuery))
                }

                submitList(updatedList)
            }

            root.setOnClickListener {
                onRootClickListener(historyQuery)
            }
            arrow.setOnClickListener {
                onArrowClickListener(historyQuery)
            }
        }
    }
}
