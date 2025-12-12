package dev.jch0029987.libretibs.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import dev.jch0029987.libretibs.databinding.FastForwardViewBinding

class FastForwardView(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    val binding = FastForwardViewBinding.inflate(LayoutInflater.from(context), this, true)
}