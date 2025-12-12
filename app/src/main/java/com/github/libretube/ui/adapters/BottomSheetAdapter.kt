package dev.jch0029987.libretibs.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.jch0029987.libretibs.databinding.BottomSheetItemBinding
import dev.jch0029987.libretibs.obj.BottomSheetItem
import dev.jch0029987.libretibs.ui.extensions.setDrawables
import dev.jch0029987.libretibs.ui.viewholders.BottomSheetViewHolder

class BottomSheetAdapter(
    private val items: List<BottomSheetItem>,
    private val listener: (index: Int) -> Unit
) : RecyclerView.Adapter<BottomSheetViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetViewHolder {
        val binding = BottomSheetItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BottomSheetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BottomSheetViewHolder, position: Int) {
        val item = items[position]
        holder.binding.root.apply {
            val current = item.getCurrent()
            text = if (current != null) "${item.title} ($current)" else item.title
            setDrawables(start = item.drawable)

            setOnClickListener {
                item.onClick.invoke()
                listener.invoke(position)
            }
        }
    }

    override fun getItemCount() = items.size
}
