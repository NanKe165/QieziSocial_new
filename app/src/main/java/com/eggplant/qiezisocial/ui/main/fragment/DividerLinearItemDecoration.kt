package com.eggplant.qiezisocial.ui.main.fragment

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Administrator on 2018/1/24.
 */
class DividerLinearItemDecoration @JvmOverloads constructor(private val spcaeing: Int, private val spaceColor: Int, private val headCount: Int = 0, private val footCount: Int = 0) : RecyclerView.ItemDecoration() {
    private val p: Paint = Paint()



    init {
        p.isAntiAlias = true
        p.color = spaceColor
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        super.onDraw(c, parent, state)
        val totalCount = parent.layoutManager.itemCount
        val lastPosition = totalCount - headCount - footCount
        val childCount = parent.childCount
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        for (i in 0 until childCount) {
            val child = parent.getChildAt(i) ?: return
            val childPos = parent.getChildAdapterPosition(child)
            if (childPos >= headCount && childPos < lastPosition) {
                val params = child.layoutParams as RecyclerView.LayoutParams
                val top = child.bottom + params.bottomMargin
                val bottom = top + spcaeing
                c.drawLine(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat(), p)
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
        val totalCount = parent.layoutManager.itemCount
        val lastPosition = totalCount - headCount - footCount
        if (parent.getChildAdapterPosition(view) >= headCount && parent.getChildAdapterPosition(view) < lastPosition) {
            outRect.set(0, 0, 0, spcaeing)//画横线，就是往下偏移一个分割线的高度
        }
    }
}
