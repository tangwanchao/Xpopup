package me.twc.popup.popup

import android.graphics.RectF
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.Size
import androidx.core.graphics.toRect
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import me.twc.popup.enum.AtViewAlign
import me.twc.popup.enum.AtViewPosition
import me.twc.popup.enum.AtViewStrategy
import java.lang.IllegalStateException

/**
 * @author 唐万超
 * @date 2023/11/15
 */
internal class PopupPositionAdjuster(
    private val mAtView: View,
    private val mAtViewPosition: AtViewPosition,
    private val mAtViewAlign: AtViewAlign,
    private val mPopupContainer: PopupContainer,
    private val mPopupView: View,
    private val mAtViewStrategies: Array<AtViewStrategy>
) : ViewTreeObserver.OnGlobalLayoutListener {
    private var mPopupViewWidth: Int = 0
    private var mPopupViewHeight: Int = 0
    private var mAtViewWidth: Int = 0
    private var mAtViewHeight: Int = 0
    private var mPopupContainerViewX: Int = 0
    private var mPopupContainerViewY: Int = 0
    private var mAtViewX: Int = 0
    private var mAtViewY: Int = 0

    private fun resetField() {
        val popupContainerLocation = getContainerViewLocationOnScreen()
        val atViewLocation = getAtViewLocationOnScreen()
        mPopupViewWidth = mPopupView.width
        mPopupViewHeight = mPopupView.height
        mAtViewWidth = mAtView.width
        mAtViewHeight = mAtView.height
        mPopupContainerViewX = popupContainerLocation[0]
        mPopupContainerViewY = popupContainerLocation[1]
        mAtViewX = atViewLocation[0]
        mAtViewY = atViewLocation[1]
    }

    fun adjust() {
        // 布局可能已经改变,每次重新计算相关参数
        resetField()

        // 1.计算位置
        val positions = if (mAtViewStrategies.contains(AtViewStrategy.AUTO_POSITION)) {
            when (mAtViewPosition) {
                AtViewPosition.LEFT -> arrayOf(AtViewPosition.LEFT, AtViewPosition.RIGHT, AtViewPosition.TOP, AtViewPosition.BOTTOM)
                AtViewPosition.TOP -> arrayOf(AtViewPosition.TOP, AtViewPosition.BOTTOM, AtViewPosition.LEFT, AtViewPosition.RIGHT)
                AtViewPosition.RIGHT -> arrayOf(AtViewPosition.RIGHT, AtViewPosition.LEFT, AtViewPosition.TOP, AtViewPosition.BOTTOM)
                AtViewPosition.BOTTOM -> arrayOf(AtViewPosition.BOTTOM, AtViewPosition.TOP, AtViewPosition.LEFT, AtViewPosition.RIGHT)
            }
        } else {
            arrayOf(mAtViewPosition)
        }
        val aligns = if (mAtViewStrategies.contains(AtViewStrategy.AUTO_ALIGN)) {
            when (mAtViewAlign) {
                AtViewAlign.START -> arrayOf(AtViewAlign.START, AtViewAlign.MIDDLE, AtViewAlign.END)
                AtViewAlign.MIDDLE -> arrayOf(AtViewAlign.MIDDLE, AtViewAlign.START, AtViewAlign.END)
                AtViewAlign.END -> arrayOf(AtViewAlign.END, AtViewAlign.START, AtViewAlign.MIDDLE)
            }
        } else {
            arrayOf(mAtViewAlign)
        }
        innerAdjust(positions, aligns)


        // 2.使用策略改变位置结果
        val containerRectF = newPopupContainerContentRectF()
        val currentPopupRectF = newCurrentPopupRectF()
        val afterComputeStrategies = mAtViewStrategies.filter { !it.isComputeEnum }
        if (afterComputeStrategies.isNotEmpty() && !containerRectF.contains(currentPopupRectF)) {
            when (afterComputeStrategies.first()) {
                AtViewStrategy.AUTO_POSITION,
                AtViewStrategy.AUTO_ALIGN -> throw IllegalStateException("不应该到达的位置")

                AtViewStrategy.FORCE -> Unit
                AtViewStrategy.SCROLL -> scroll()
                AtViewStrategy.RESIZE -> resize()
            }
        }
    }

    private fun innerAdjust(positions: Array<AtViewPosition>, aligns: Array<AtViewAlign>) {
        val containerRectF = newPopupContainerContentRectF()
        var adjustedPopupRectF: RectF? = null
        outer@ for (position in positions) {
            for (align in aligns) {
                val rectF = computePopupRectF(position, align)
                if (containerRectF.contains(rectF)) {
                    adjustedPopupRectF = rectF
                    break@outer
                }
            }
        }
        if (adjustedPopupRectF == null) {
            adjustedPopupRectF = computePopupRectF(mAtViewPosition, mAtViewAlign)
        }
        changePopupViewXY(floatArrayOf(adjustedPopupRectF.left, adjustedPopupRectF.top))
    }

    /**
     * 根据指定 [position] 和 [align] 计算 popup 在屏幕中的位置
     */
    private fun computePopupRectF(position: AtViewPosition, align: AtViewAlign) = when (position) {
        AtViewPosition.LEFT -> newAtViewLeftPopupRectF(align)
        AtViewPosition.TOP -> newAtViewTopPopupRectF(align)
        AtViewPosition.RIGHT -> newAtViewRightPopupRectF(align)
        AtViewPosition.BOTTOM -> newAtViewBottomPopupRectF(align)
    }

    private fun scroll() {
        val popupRectF = newCurrentPopupRectF()
        val contentRectF = newPopupContainerContentRectF()
        if (contentRectF.contains(popupRectF)) {
            return
        }
        val requestScroll = mAtView.requestRectangleOnScreen(popupRectF.toRect(), false)
        if (requestScroll) {
            // atView 位置已经变更,我们重新计算一次
            adjust()
        }
    }

    private fun resize() {
        val popupRectF = newCurrentPopupRectF()
        val contentRectF = newPopupContainerContentRectF()
        if (contentRectF.contains(popupRectF)) return

        val popupLayoutParams = mPopupView.layoutParams
        if (popupRectF.left < contentRectF.left) {
            popupLayoutParams.width = (popupRectF.width() - (contentRectF.left - popupRectF.left)).toInt()
        }
        if (popupRectF.top < contentRectF.top) {
            popupLayoutParams.height = (popupRectF.height() - (contentRectF.top - popupRectF.top)).toInt()
        }
        if (popupRectF.right > contentRectF.right) {
            popupLayoutParams.width = (popupRectF.width() - (popupRectF.right - contentRectF.right)).toInt()
        }
        if (popupRectF.bottom > contentRectF.bottom) {
            popupLayoutParams.height = (popupRectF.height() - (popupRectF.bottom - contentRectF.bottom)).toInt()
        }
        mPopupView.layoutParams = popupLayoutParams
        // 布局已经改变,我们需要在布局完成后重新计算一次
        mPopupView.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        adjust()
        mPopupView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    private fun changePopupViewXY(@Size(2) xy: FloatArray) {
        mPopupView.x = xy[0]
        mPopupView.y = xy[1]
    }

    private fun newAtViewLeftPopupXY(align: AtViewAlign = mAtViewAlign): FloatArray {
        return floatArrayOf(
            (mAtViewX - mPopupViewWidth).toFloat(),
            alignVertical(align)
        )
    }

    private fun newAtViewTopPopupXY(align: AtViewAlign = mAtViewAlign): FloatArray {
        return floatArrayOf(
            alignHorizontal(align),
            (mAtViewY - mPopupViewHeight).toFloat()
        )
    }

    private fun newAtViewRightPopupXY(align: AtViewAlign = mAtViewAlign): FloatArray {
        return floatArrayOf(
            (mAtViewX + mAtViewWidth).toFloat(),
            alignVertical(align)
        )
    }

    private fun newAtViewBottomPopupXY(align: AtViewAlign = mAtViewAlign): FloatArray {
        return floatArrayOf(
            alignHorizontal(align),
            (mAtViewY + mAtViewHeight).toFloat()
        )
    }

    private fun newAtViewLeftPopupRectF(align: AtViewAlign = mAtViewAlign): RectF = newPopupRectF(newAtViewLeftPopupXY(align))
    private fun newAtViewTopPopupRectF(align: AtViewAlign = mAtViewAlign): RectF = newPopupRectF(newAtViewTopPopupXY(align))
    private fun newAtViewRightPopupRectF(align: AtViewAlign = mAtViewAlign): RectF = newPopupRectF(newAtViewRightPopupXY(align))
    private fun newAtViewBottomPopupRectF(align: AtViewAlign = mAtViewAlign): RectF = newPopupRectF(newAtViewBottomPopupXY(align))
    private fun newCurrentPopupRectF(): RectF = newPopupRectF(floatArrayOf(mPopupView.x, mPopupView.y))

    private fun newPopupRectF(@Size(2) xy: FloatArray): RectF {
        val left = xy[0]
        val top = xy[1]
        val right = left + mPopupViewWidth
        val bottom = top + mPopupViewHeight
        return RectF(left, top, right, bottom)
    }

    private fun newPopupContainerContentRectF(): RectF {
        val left = mPopupContainerViewX.toFloat() + mPopupContainer.paddingStart + mPopupContainer.marginStart
        val top = mPopupContainerViewY.toFloat() + mPopupContainer.paddingTop + mPopupContainer.marginTop
        val right = left + mPopupContainer.width - mPopupContainer.paddingRight - mPopupContainer.marginEnd
        val bottom = top + mPopupContainer.height - mPopupContainer.paddingBottom - mPopupContainer.marginBottom
        return RectF(left, top, right, bottom)
    }

    private fun alignHorizontal(align: AtViewAlign = mAtViewAlign): Float = when (align) {
        AtViewAlign.START -> mAtViewX
        AtViewAlign.MIDDLE -> mAtViewX + (mAtView.width - mPopupView.width) / 2
        AtViewAlign.END -> mAtViewX + mAtView.width - mPopupView.width
    }.toFloat()

    private fun alignVertical(align: AtViewAlign = mAtViewAlign): Float = when (align) {
        AtViewAlign.START -> mAtViewY
        AtViewAlign.MIDDLE -> mAtViewY - (mPopupView.height - mAtView.height) / 2
        AtViewAlign.END -> mAtViewY + mAtView.height - mPopupView.height
    }.toFloat()

    private fun getAtViewLocationOnScreen(): IntArray {
        val result = intArrayOf(0, 0)
        mAtView.getLocationOnScreen(result)
        return result
    }

    private fun getContainerViewLocationOnScreen(): IntArray {
        val result = intArrayOf(0, 0)
        mPopupContainer.getLocationOnScreen(result)
        return result
    }
}