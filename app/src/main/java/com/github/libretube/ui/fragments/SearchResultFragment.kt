package dev.jch0029987.libretibs.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.jch0029987.libretibs.R
import dev.jch0029987.libretibs.constants.PreferenceKeys
import dev.jch0029987.libretibs.databinding.FragmentSearchResultBinding
import dev.jch0029987.libretibs.db.DatabaseHelper
import dev.jch0029987.libretibs.db.obj.SearchHistoryItem
import dev.jch0029987.libretibs.extensions.ceilHalf
import dev.jch0029987.libretibs.helpers.PreferenceHelper
import dev.jch0029987.libretibs.ui.activities.MainActivity
import dev.jch0029987.libretibs.ui.adapters.SearchResultsAdapter
import dev.jch0029987.libretibs.ui.base.DynamicLayoutManagerFragment
import dev.jch0029987.libretibs.ui.extensions.setOnBackPressed
import dev.jch0029987.libretibs.ui.models.SearchResultViewModel
import dev.jch0029987.libretibs.util.TextUtils.toTimeInSeconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class SearchResultFragment : DynamicLayoutManagerFragment(R.layout.fragment_search_result) {
    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    private val args by navArgs<SearchResultFragmentArgs>()
    private val viewModel by viewModels<SearchResultViewModel>()

    private val mainActivity get() = activity as MainActivity
    private var recyclerViewState: Parcelable? = null

    override fun setLayoutManagers(gridItems: Int) {
        _binding?.searchRecycler?.layoutManager = GridLayoutManager(context, gridItems.ceilHalf())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentSearchResultBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        // fixes a bug that the search query will stay the old one when searching for multiple
        // different queries in a row and navigating to the previous ones through back presses
        mainActivity.setQuerySilent(args.query)

        // add the query to the history
        addToHistory(args.query)

        // filter options
        binding.filterChipGroup.setOnCheckedStateChangeListener { _, _ ->
            viewModel.setFilter(
                when (
                    binding.filterChipGroup.checkedChipId
                ) {
                    R.id.chip_all -> "all"
                    R.id.chip_videos -> "videos"
                    R.id.chip_channels -> "channels"
                    R.id.chip_playlists -> "playlists"
                    R.id.chip_music_songs -> "music_songs"
                    R.id.chip_music_videos -> "music_videos"
                    R.id.chip_music_albums -> "music_albums"
                    R.id.chip_music_playlists -> "music_playlists"
                    R.id.chip_music_artists -> "music_artists"
                    else -> throw IllegalArgumentException("Filter out of range")
                }
            )
        }

        val timeStamp = args.query.toHttpUrlOrNull()?.queryParameter("t")?.toTimeInSeconds()
        val searchResultsAdapter = SearchResultsAdapter(timeStamp ?: 0)
        binding.searchRecycler.adapter = searchResultsAdapter

        binding.searchRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                searchResultsAdapter.loadStateFlow.collect {
                    val isLoading = it.source.refresh is LoadState.Loading
                    binding.progress.isVisible = isLoading
                    binding.searchResultsLayout.isGone = isLoading
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.searchResultsFlow.collectLatest {
                    searchResultsAdapter.submitData(it)
                }
            }
        }

        viewModel.searchSuggestion.observe(viewLifecycleOwner) { suggestion ->
            binding.searchSuggestionContainer.isVisible = suggestion != null
            binding.searchSuggestionContainer.setOnClickListener(null)
            if (suggestion == null) return@observe

            val (suggestion, corrected) = suggestion
            binding.searchSuggestion.text = suggestion
            binding.searchSuggestionLabel.text = if (corrected) {
                getString(R.string.showing_results_for)
            } else {
                binding.searchSuggestionContainer.setOnClickListener {
                    mainActivity.setQuery(suggestion, true)
                }
                getString(R.string.did_you_mean)
            }
        }

        setOnBackPressed {
            findNavController().popBackStack(R.id.searchFragment, true) ||
                    findNavController().popBackStack()
        }
    }

    private fun addToHistory(query: String) {
        val searchHistoryEnabled =
            PreferenceHelper.getBoolean(PreferenceKeys.SEARCH_HISTORY_TOGGLE, true)
        if (searchHistoryEnabled && query.isNotEmpty()) {
            lifecycleScope.launch(Dispatchers.IO) {
                DatabaseHelper.addToSearchHistory(SearchHistoryItem(query.trim()))
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // manually restore the recyclerview state due to https://github.com/material-components/material-components-android/issues/3473
        binding.searchRecycler.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
