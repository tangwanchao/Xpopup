package me.twc.popup.popup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import me.twc.popup.Constants
import me.twc.popup.enum.PopupBackgroundType
import me.twc.popup.enum.PopupOutsideClickType
import kotlin.math.pow

/**
 * @author 唐万超
 * @date 2023/11/09
 *
 * Popup 容器
 *
 */
internal class PopupContainer @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attributeSet, defStyleAttr) {


    //<editor-fold desc="私有属性">
    private var mPopupSuppressLayout: Boolean = false
    private var mPopupLayoutCalledWhileSuppressed: Boolean = false
    private val mBackgroundPaint by lazy { newPaint() }
    private val mBackgroundRectF = RectF()
    private val mTouchSlopSquare = ViewConfiguration.get(context).scaledTouchSlop.toDouble().pow(2.0)
    private var mIsOutsideDown: Boolean = false
    private var mIsBackgroundDown: Boolean = false
    private var mDownX = 0f
    private var mDownY = 0f

    // 在手指按下到抬起的过程中,手指是否一直在点击区域的标记
    private var mAlwaysInTapRegion = false
    //</editor-fold>

    //<editor-fold desc="内部属性">
    internal var mOutsideClickListener: OutsideClickListener? = null
    internal var mOutsideClickType: PopupOutsideClickType = PopupOutsideClickType.INTERCEPT_BACKGROUND_AND_DISMISS_POPUP
    internal var mPopupBackgroundType: PopupBackgroundType = PopupBackgroundType.SHADOW

    //</editor-fold>
    init {
        setWillNotDraw(false)
    }

    //<editor-fold desc="内部方法">
    internal fun setShadowColor(@ColorInt color: Int) {
        mBackgroundPaint.color = color
        postInvalidate()
    }

    internal fun popupSuppressLayout(suppress: Boolean) {
        mPopupSuppressLayout = suppress
        if (!suppress && mPopupLayoutCalledWhileSuppressed) {
            requestLayout()
            mPopupLayoutCalledWhileSuppressed = false
        }
    }
    //</editor-fold>

    //<editor-fold desc="重写方法">
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mPopupLayoutCalledWhileSuppressed = false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val actionMasked = event.actionMasked
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            mIsOutsideDown = tryCatchOutsideClick(event)
            mIsBackgroundDown = tryCatchBackgroundClick(event)
            mAlwaysInTapRegion = true
            mDownX = event.x
            mDownY = event.y
        }
        // 不是外部点击,不拦截事件
        if (!mIsOutsideDown) {
            return super.onTouchEvent(event)
        }
        val isActionMove = actionMasked == MotionEvent.ACTION_MOVE
        val isActionUp = actionMasked == MotionEvent.ACTION_UP
        if (isActionMove) {
            val deltaX = event.x - mDownX
            val deltaY = event.y - mDownY
            val distance = (deltaX * deltaX) + (deltaY * deltaY)
            if (distance > mTouchSlopSquare) {
                mAlwaysInTapRegion = false
            }
        }

        var intercept = false
        when (mOutsideClickType) {
            PopupOutsideClickType.INTERCEPT,
            PopupOutsideClickType.INTERCEPT_AND_DISMISS_POPUP -> {
                if (isActionUp && mAlwaysInTapRegion) {
                    mOutsideClickListener?.onPopupContainerOutsideClick(mOutsideClickType.dismissPopup)
                }
                intercept = true
            }

            PopupOutsideClickType.INTERCEPT_BACKGROUND,
            PopupOutsideClickType.INTERCEPT_BACKGROUND_AND_DISMISS_POPUP -> {
                if (mIsBackgroundDown) {
                    if (isActionUp && mAlwaysInTapRegion) {
                        mOutsideClickListener?.onPopupContainerOutsideClick(mOutsideClickType.dismissPopup)
                    }
                    intercept = true
                }
            }

            PopupOutsideClickType.CLICK_THROUGH -> Unit
        }
        if (intercept) {
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        drawBackground(canvas)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (!mPopupSuppressLayout) {
            super.onLayout(changed, left, top, right, bottom)
        } else {
            mPopupLayoutCalledWhileSuppressed = true
        }
    }
    //</editor-fold>

    //<editor-fold desc="绘制背景">
    private fun drawBackground(canvas: Canvas) {
        computeBackgroundRectF()
        canvas.drawRect(mBackgroundRectF, mBackgroundPaint)
    }
    //</editor-fold>

    //<editor-fold desc="私有方法">
    private fun computeBackgroundRectF() {
        val popupView = requirePopupView()
        when (mPopupBackgroundType) {
            PopupBackgroundType.TRANSPARENT -> mBackgroundRectF.setEmpty()
            PopupBackgroundType.TOP_SHADOW -> mBackgroundRectF.set(
                left.toFloat(), top.toFloat(),
                right.toFloat(), popupView.y + popupView.height
            )

            PopupBackgroundType.BOTTOM_SHADOW -> mBackgroundRectF.set(
                left.toFloat(), popupView.y,
                right.toFloat(), bottom.toFloat()
            )

            PopupBackgroundType.SHADOW -> mBackgroundRectF.set(
                left.toFloat(), top.toFloat(),
                right.toFloat(), bottom.toFloat()
            )
        }
    }

    /**
     * 尝试捕获非 popup 区域点击
     *
     * @return [true : 点击非 popup 区域]
     *         [false: 点击 popup 区域]
     */
    private fun tryCatchOutsideClick(event: MotionEvent): Boolean {
        val popupView = requirePopupView()
        val x = event.x
        val y = event.y
        val realLeft = popupView.x
        val realTop = popupView.y + popupView.height
        val realRight = realLeft + popupView.width
        val realBottom = popupView.y
        if (x in (realLeft..realRight) && y in (realTop..realBottom)) {
            return false
        }
        return true
    }

    /**
     * 尝试捕获背景区域点击
     *
     * @return [true : 点击背景区域]
     *         [false: 点击非背景区域]
     */
    private fun tryCatchBackgroundClick(event: MotionEvent): Boolean {
        computeBackgroundRectF()
        if (mBackgroundRectF.contains(event.x, event.y)) {
            return true
        }
        return false
    }

    private fun newPaint(): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Constants.DEFAULT_SHADOW_BACKGROUND_COLOR
        style = Paint.Style.FILL
    }
    //</editor-fold>

    //<editor-fold desc="公开方法">
    fun requirePopupView(): View {
        return getChildAt(0)!!
    }
    //</editor-fold>

    internal interface OutsideClickListener {
        /**
         * 非 popup 区域被点击
         */
        fun onPopupContainerOutsideClick(shouldDismiss: Boolean)
    }
}