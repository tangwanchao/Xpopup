package me.twc.popup.popup

import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.annotation.ColorInt
import androidx.annotation.LayoutRes
import androidx.lifecycle.LifecycleOwner
import me.twc.popup.Constants
import me.twc.popup.animator.ScaleAlphaAnimator
import me.twc.popup.animator.XPopupAnimator
import me.twc.popup.enum.AtViewAlign
import me.twc.popup.enum.AtViewPosition
import me.twc.popup.enum.AtViewStrategy
import me.twc.popup.enum.PopupBackgroundType
import me.twc.popup.enum.PopupOutsideClickType
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException

/**
 * @author 唐万超
 * @date 2023/11/13
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class XPopupBuilder(
    private val mWindow: Window
) {
    //<editor-fold desc="私有属性">
    private val mPopupContainer = PopupContainer(mWindow.context)
    private lateinit var mPopupView: View
    private var mAttachToDecorView: Boolean = true
    private var mPopupAnimator: XPopupAnimator = ScaleAlphaAnimator()
    private var mShadowColor: Int = Constants.DEFAULT_SHADOW_BACKGROUND_COLOR
    private var mCustomLayoutRes: Int? = null
    private var mCustomViewLayoutParams: LayoutParams? = null
    private var mAttachBackPressedDispatcherOwner: Any? = null
    private var mAtView: View? = null
    private var mAtViewPosition: AtViewPosition? = null
    private var mAtViewAlign: AtViewAlign? = null
    private var mAtViewStrategies: Array<AtViewStrategy>? = null
    //</editor-fold>

    /**
     * popup 将添加到 AndroidContentView(android.R.id.content)
     *
     * 默认值: popup 将添加到 window.decorView
     *
     * 注意:
     * 添加到 AndroidContentView 可能会导致底部导航栏和状态栏没有阴影背景(如果window 没有全屏)
     */
    fun attachToAndroidContentView(): XPopupBuilder {
        mAttachToDecorView = false
        return this
    }

    /**
     * 设置阴影背景颜色
     *
     * 默认值: [Constants.DEFAULT_SHADOW_BACKGROUND_COLOR]
     */
    fun setShadowColor(@ColorInt color: Int): XPopupBuilder {
        mShadowColor = color
        return this
    }

    /**
     * 设置 popup 动画
     *
     * 默认值: [ScaleAlphaAnimator]
     */
    fun setPopupAnimator(animator: XPopupAnimator): XPopupBuilder {
        mPopupAnimator = animator
        return this
    }

    /**
     * 设置背景类型
     *
     * 默认值: [PopupBackgroundType.SHADOW]
     */
    fun setBackgroundType(type: PopupBackgroundType): XPopupBuilder {
        mPopupContainer.mPopupBackgroundType = type
        return this
    }

    /**
     * 非 popup 区域点击类型
     *
     * 默认值: [PopupOutsideClickType.INTERCEPT_BACKGROUND_AND_DISMISS_POPUP]
     */
    fun setOutsideClickType(type: PopupOutsideClickType): XPopupBuilder {
        mPopupContainer.mOutsideClickType = type
        return this
    }

    /**
     * popup 未消失的时候是否拦截返回事件让 popup 消失
     *
     * 注意:宿主必须是 [LifecycleOwner] 及 [OnBackPressedDispatcherOwner] 的子类
     *
     * 默认值: null,不拦截返回事件
     *
     * @param attachTo 拦截返回事件的宿主
     *
     * @see [androidx.activity.ComponentActivity]
     * @see [androidx.activity.ComponentDialog]
     */
    fun interceptBackPressed(attachTo: Any?): XPopupBuilder {
        mAttachBackPressedDispatcherOwner = attachTo
        return this
    }

    /**
     * 作为自定义 popup 弹出
     */
    fun asCustomView(@LayoutRes layoutRes: Int): XPopupBuilder {
        mCustomLayoutRes = layoutRes
        return this
    }

    /**
     * 作为自动以 popup 弹出
     */
    fun asCustomView(view: View, layoutParams: LayoutParams?): XPopupBuilder {
        mPopupView = view
        mCustomViewLayoutParams = layoutParams
        return this
    }

    /**
     * 依附在 view 上
     *
     * @param atViewPosition popup 在 [atView] 哪边
     * @param atViewAlign popup 和 [atView] 如何对齐
     * @param atViewStrategies popup 使用指定的 [atViewPosition] 和 [atViewAlign] 不能完全显示时如何处理.
     * [AtViewStrategy.isComputeEnum] 为 true 的枚举能同时传递多个,可以和[AtViewStrategy.isComputeEnum]为 false 的枚举一起传递
     * [AtViewStrategy.isComputeEnum] 为 false 的枚举只能传递一个,可以和[AtViewStrategy.isComputeEnum]为 true 的枚举一起传递
     */
    fun atView(
        atView: View,
        atViewPosition: AtViewPosition,
        atViewAlign: AtViewAlign,
        atViewStrategies: Array<AtViewStrategy> = arrayOf(AtViewStrategy.AUTO_POSITION, AtViewStrategy.AUTO_ALIGN)
    ): XPopupBuilder {
        if (atViewStrategies.filter { !it.isComputeEnum }.size > 1) {
            throw IllegalArgumentException("[AtViewStrategy.isComputeEnum] 为 false 的枚举只能传递一个")
        }
        mAtView = atView
        mAtViewPosition = atViewPosition
        mAtViewAlign = atViewAlign
        mAtViewStrategies = atViewStrategies
        return this
    }

    /**
     * @param initPopupBlock 如果调用了[asCustomView],将回调该函数用来初始化 popup.
     *
     * @see [XPopup.requirePopupView]
     *
     * @throws IllegalStateException 如果没有调用 asCustomView
     */
    fun create(initPopupBlock: ((view: View) -> Unit)? = null): XPopup {
        initPopupContainer()
        initPopupView()
        initPopupBlock?.invoke(mPopupView)
        return XPopup(
            mAttachToView = getAttachToView(),
            mIsAttachToDecorView = mAttachToDecorView,
            mPopupContainer = mPopupContainer,
            mPopupAnimator = mPopupAnimator,
            mPopupPositionAdjuster = getPopupPositionAdjuster()
        ).interceptBackPressed(mAttachBackPressedDispatcherOwner)
    }

    //<editor-fold desc="私有方法">
    private fun getAttachToView(): FrameLayout {
        if (mAttachToDecorView) {
            return mWindow.decorView as FrameLayout
        }
        return mWindow.findViewById(android.R.id.content)
    }

    private fun initPopupContainer() {
        mPopupContainer.id = View.generateViewId()
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        mPopupContainer.layoutParams = layoutParams
        mPopupContainer.setShadowColor(mShadowColor)
    }

    private fun initPopupView() {
        if (mCustomLayoutRes != null) {
            val layoutInflater = LayoutInflater.from(mPopupContainer.context)
            mPopupView = layoutInflater.inflate(mCustomLayoutRes!!, mPopupContainer, false)
        } else {
            if (!::mPopupView.isInitialized) {
                throw IllegalStateException("需要调用 asCustomView 定义 popup")
            }
            mPopupView.layoutParams = mCustomViewLayoutParams
        }
        mPopupView.isClickable = true
        mPopupContainer.addView(mPopupView)
    }

    private fun getPopupPositionAdjuster(): PopupPositionAdjuster? {
        if (mAtView != null && mAtViewPosition != null && mAtViewAlign != null && mAtViewStrategies != null) {
            return PopupPositionAdjuster(mAtView!!, mAtViewPosition!!, mAtViewAlign!!, mPopupContainer, mPopupView, mAtViewStrategies!!)
        }
        return null
    }
    //</editor-fold>
}