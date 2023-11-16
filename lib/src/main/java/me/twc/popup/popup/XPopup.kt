package me.twc.popup.popup

import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewGroup.MarginLayoutParams
import android.view.ViewTreeObserver.OnPreDrawListener
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcherOwner
import androidx.annotation.MainThread
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleOwner
import me.twc.popup.animator.XPopupAnimator
import me.twc.popup.animator.XPopupAnimatorListener
import me.twc.popup.enum.PopupState
import me.twc.popup.utils.ThreadUtil

/**
 * @author 唐万超
 * @date 2023/11/10
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
class XPopup internal constructor(
    private val mAttachToView: FrameLayout,
    private val mIsAttachToDecorView: Boolean,
    private val mPopupContainer: PopupContainer,
    private val mPopupAnimator: XPopupAnimator,
    private val mPopupPositionAdjuster: PopupPositionAdjuster? = null
) : PopupContainer.OutsideClickListener, OnPreDrawListener {

    private val mPopupStateChangeListenerList: MutableList<PopupStateChangeListener> = mutableListOf()
    private val mBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            dismiss()
        }
    }
    private var mPopupState: PopupState = PopupState.DISMISS
        set(value) {
            mBackPressedCallback.isEnabled = value != PopupState.DISMISS
            notifyListeners(field, value)
            field = value
        }

    private val mShowAnimatorListener = object : XPopupAnimatorListener {
        override fun onEnd() {
            if (mPopupState == PopupState.ANIMATE_SHOW) {
                mPopupContainer.popupSuppressLayout(false)
                mPopupState = PopupState.SHOW
            }
        }
    }
    private val mDismissAnimatorListener = object : XPopupAnimatorListener {
        override fun onEnd() {
            if (mPopupState == PopupState.ANIMATE_DISMISS) {
                mPopupContainer.popupSuppressLayout(false)
                mAttachToView.removeView(mPopupContainer)
                mPopupState = PopupState.DISMISS
            }
        }
    }

    init {
        mPopupContainer.mOutsideClickListener = this
    }

    //<editor-fold desc="公开方法">
    /**
     * show popup
     */
    fun postShow() = mAttachToView.post { show() }

    /**
     * dismiss popup
     */
    fun postDismiss() = mAttachToView.post { dismiss() }

    /**
     * toggle
     */
    fun postToggle() = mAttachToView.post { toggle() }

    /**
     * 获取 popupView
     */
    fun requirePopupView(): View = mPopupContainer.requirePopupView()

    /**
     * 获取当前 popup 状态
     */
    fun getCurrentState(): PopupState = mPopupState

    /**
     * 添加状态变更监听
     */
    fun addStateListener(listener: PopupStateChangeListener) {
        mPopupStateChangeListenerList.add(listener)
    }

    /**
     * 移除状态变更监听
     */
    fun removeStateListener(listener: PopupStateChangeListener) {
        mPopupStateChangeListenerList.remove(listener)
    }
    //</editor-fold>


    //<editor-fold desc="私有方法">
    internal fun interceptBackPressed(owner: Any?): XPopup {
        if (owner is LifecycleOwner && owner is OnBackPressedDispatcherOwner) {
            owner.onBackPressedDispatcher.addCallback(owner, mBackPressedCallback)
        }
        return this
    }

    /**
     * show popup
     */
    @MainThread
    private fun show() {
        if (mPopupState.isShowOrAnimateShow()) {
            return
        }
        // 设置 pre draw 监听,在绘制之前进行动画
        enableShowPopupPreDrawListener()
        if (mPopupContainer.parent == null) {
            tryConsumeBottomInset()
            ensurePopupSize()
            mPopupPositionAdjuster?.adjust()
            mAttachToView.addView(mPopupContainer)
        }
        mPopupState = PopupState.ANIMATE_SHOW
    }

    override fun onPreDraw(): Boolean {
        val popupView = requirePopupView()
        mPopupContainer.popupSuppressLayout(true)
        mPopupAnimator.initAnimator(mPopupContainer, popupView)
        mPopupAnimator.showStartAnimator(mPopupContainer, popupView, mShowAnimatorListener)
        popupView.viewTreeObserver.removeOnPreDrawListener(this)
        return true
    }


    /**
     * dismiss popup
     */
    @MainThread
    private fun dismiss() {
        if (mPopupState.isDismissOrAnimateDismiss()) {
            return
        }
        disableShowPopupPreDrawListener()
        val popupView = requirePopupView()
        mPopupState = PopupState.ANIMATE_DISMISS
        mPopupContainer.popupSuppressLayout(true)
        mPopupAnimator.showDismissAnimator(mPopupContainer, popupView, mDismissAnimatorListener)
    }

    @MainThread
    private fun toggle() {
        if (mPopupState.isDismissOrAnimateDismiss()) {
            show()
        } else {
            dismiss()
        }
    }

    private fun notifyListeners(preState: PopupState, curState: PopupState) = ThreadUtil.callInMainThread {
        for (listener in mPopupStateChangeListenerList) {
            listener.onStateChanged(preState, curState)
        }
    }

    /**
     * 尝试消耗底部插入
     */
    private fun tryConsumeBottomInset() {
        if (mIsAttachToDecorView) {
            val windowInsetsCompat = ViewCompat.getRootWindowInsets(mAttachToView)
            if (windowInsetsCompat != null) {
                val bottomInset = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom
                mPopupContainer.updateLayoutParams<MarginLayoutParams> {
                    bottomMargin = bottomInset
                }
            }
        }
    }

    /**
     * 手动测量布局 popup
     */
    private fun ensurePopupSize() {
        val marginBottom = (mPopupContainer.layoutParams as MarginLayoutParams).bottomMargin
        val width = mAttachToView.width
        val height = mAttachToView.height - marginBottom
        mPopupContainer.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
        mPopupContainer.layout(0, 0, width, height)
    }

    private fun enableShowPopupPreDrawListener() {
        requirePopupView().viewTreeObserver.addOnPreDrawListener(this)
    }

    private fun disableShowPopupPreDrawListener() {
        requirePopupView().viewTreeObserver.removeOnPreDrawListener(this)
    }
    //</editor-fold>

    override fun onPopupContainerOutsideClick(shouldDismiss: Boolean) {
        if (shouldDismiss) {
            dismiss()
        }
    }
}