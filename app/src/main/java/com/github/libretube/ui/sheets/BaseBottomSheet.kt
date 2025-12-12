package dev.jch0029987.libretibs.ui.sheets

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import dev.jch0029987.libretibs.R
import dev.jch0029987.libretibs.databinding.BottomSheetBinding
import dev.jch0029987.libretibs.extensions.dpToPx
import dev.jch0029987.libretibs.obj.BottomSheetItem
import dev.jch0029987.libretibs.ui.adapters.BottomSheetAdapter
import kotlinx.coroutines.launch
import dev.jch0029987.libretibs.ui.extensions.onSystemInsets


open class BaseBottomSheet(@LayoutRes layoutResId: Int = R.layout.bottom_sheet) : ExpandedBottomSheet(layoutResId) {

    private var title: String? = null
    private var preselectedItem: String? = null
    private lateinit var items: List<BottomSheetItem>
    private lateinit var listener: (index: Int) -> Unit

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = BottomSheetBinding.bind(view)

        if (title != null) {
            binding.bottomSheetTitleLayout.isVisible = true

            binding.bottomSheetTitle.text = title
            binding.bottomSheetTitle.textSize = titleTextSize
            binding.bottomSheetTitle.updateLayoutParams<MarginLayoutParams> {
                marginStart = titleMargin
                marginEnd = titleMargin
            }
        }

        // set the selected item
        for (item in items) {
            Log.e(item.title, preselectedItem.toString())
        }
        for (item in items.filter { it.title == preselectedItem }) {
            item.title = "${item.title} ✓"
        }
        binding.optionsRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.optionsRecycler.adapter = BottomSheetAdapter(items, listener)

        // add bottom padding to the list, to ensure that the last item is not overlapped by the system bars
        binding.optionsRecycler.onSystemInsets { v, systemInsets ->
            v.setPadding(
                v.paddingLeft,
                v.paddingTop,
                v.paddingRight,
                systemInsets.bottom
            )
        }

    }

    fun setItems(items: List<BottomSheetItem>, listener: (suspend (index: Int) -> Unit)?) = apply {
        this.items = items
        this.listener = { index ->
            lifecycleScope.launch {
                dialog?.hide()
                listener?.invoke(index)
                runCatching {
                    dismiss()
                }
            }
        }
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun setSimpleItems(
        titles: List<String>,
        preselectedItem: String? = null,
        listener: (suspend (index: Int) -> Unit)?
    ) = apply {
        setItems(titles.map { BottomSheetItem(it) }, listener)
        this.preselectedItem = preselectedItem
    }

    companion object {
        private val titleTextSize = 7f.dpToPx().toFloat()
        private val titleMargin = 24f.dpToPx()
    }
}
