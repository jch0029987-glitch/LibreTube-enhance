package dev.jch0029987.libretibs.ui.sheets

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.activityViewModels
import dev.jch0029987.libretibs.ui.models.CommonPlayerViewModel

abstract class ExpandablePlayerSheet(@LayoutRes layoutResId: Int) :
    UndimmedBottomSheet(layoutResId) {
    private val commonPlayerViewModel: CommonPlayerViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commonPlayerViewModel.setSheetExpand(true)
        commonPlayerViewModel.sheetExpand.observe(viewLifecycleOwner) {
            when (it) {
                true -> expand()
                false -> expand(true)
                else -> dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        commonPlayerViewModel.setSheetExpand(null)
    }
}