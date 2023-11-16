package me.twc.xpopup

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import me.twc.popup.utils.LogUtil

/**
 * @author 唐万超
 * @date 2023/11/10
 */
class TestView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        LogUtil.d("TestView onMeasure")
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        LogUtil.d("TestView onLayout l = $left, t = $top, r = $right, b = $bottom")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        LogUtil.d("TestView onDraw")
    }
}