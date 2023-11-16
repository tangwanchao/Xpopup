package me.twc.xpopup

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * @author 唐万超
 * @date 2023/11/20
 */
class TestFrameLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //MeasureSpec.EXACTLY
        //LogUtil.d("TestFrameLayout onMeasure mode = ${MeasureSpec.getMode(widthMeasureSpec)},${MeasureSpec.getMode(heightMeasureSpec)}")
        //LogUtil.d("TestFrameLayout onMeasure size = ${MeasureSpec.getSize(widthMeasureSpec)},${MeasureSpec.getSize(heightMeasureSpec)}")
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        //LogUtil.d("TestFrameLayout onLayout left = $left, top = $top, right = $right, bottom = $bottom")
    }
}