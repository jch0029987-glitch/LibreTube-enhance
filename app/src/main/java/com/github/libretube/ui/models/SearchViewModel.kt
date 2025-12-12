package dev.jch0029987.libretibs.ui.models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import dev.jch0029987.libretibs.api.MediaServiceRepository
import dev.jch0029987.libretibs.constants.PreferenceKeys
import dev.jch0029987.libretibs.db.DatabaseHolder
import dev.jch0029987.libretibs.helpers.PreferenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext

class SearchViewModel : ViewModel() {
    val searchQuery = MutableLiveData<String>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchSuggestions = searchQuery.asFlow()
        .mapLatest { query ->
            if (query == null) return@mapLatest emptyList()

            if (PreferenceHelper.getBoolean(
                    PreferenceKeys.SEARCH_SUGGESTIONS,
                    true
                )
            ) withContext(Dispatchers.IO) {
                try {
                    MediaServiceRepository.instance.getSuggestions(query)
                } catch (e: Exception) {
                    Log.e("failed to fetch suggestions", e.stackTraceToString())

                    return@withContext emptyList()
                }
            }
            else emptyList()
        }

    val searchHistory = DatabaseHolder.Database.searchHistoryDao().getAllFlow()

    fun setQuery(query: String?) {
        this.searchQuery.value = query
    }
}
