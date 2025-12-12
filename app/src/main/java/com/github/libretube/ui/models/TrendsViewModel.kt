package dev.jch0029987.libretibs.ui.models

import android.content.Context
import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jch0029987.libretibs.api.MediaServiceRepository
import dev.jch0029987.libretibs.api.TrendingCategory
import dev.jch0029987.libretibs.api.obj.StreamItem
import dev.jch0029987.libretibs.extensions.TAG
import dev.jch0029987.libretibs.extensions.toastFromMainDispatcher
import dev.jch0029987.libretibs.helpers.PreferenceHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TrendsViewModel : ViewModel() {
    data class TrendingStreams(val region: String, val streams: List<StreamItem>)

    val trendingVideos = MutableLiveData<Map<TrendingCategory, TrendingStreams>>()
    var recyclerViewState: Parcelable? = null

    private var currentJob: Job? = null

    fun fetchTrending(context: Context, category: TrendingCategory) {
        // cancel previously started, still running requests as users can only see one tab at a time,
        // so it doesn't make sense to continue loading the previously seen (now hidden) tab data
        runCatching { currentJob?.cancel() }

        currentJob = viewModelScope.launch {
            try {
                val region = PreferenceHelper.getTrendingRegion(context)
                val response = withContext(Dispatchers.IO) {
                    MediaServiceRepository.instance.getTrending(region, category)
                }
                setStreamsForCategory(category, TrendingStreams(region, response))
            } catch (e: Exception) {
                Log.e(TAG(), e.stackTraceToString())
                context.toastFromMainDispatcher(e.localizedMessage.orEmpty())
            }
        }
    }

    fun setStreamsForCategory(category: TrendingCategory, streams: TrendingStreams) {
        val newState = trendingVideos.value.orEmpty()
            .toMutableMap()
            .apply {
                put(category, streams)
            }
        trendingVideos.postValue(newState)
    }
}
