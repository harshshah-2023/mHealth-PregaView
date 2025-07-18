package com.example.myapplication

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class HorizontalDividerItemDecoration(
    private val context: Context,
    private val margin: Int = context.resources.getDimensionPixelSize(R.dimen.item_margin)
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) != parent.adapter?.itemCount?.minus(1)) {
                right = margin
            }
        }
    }
}