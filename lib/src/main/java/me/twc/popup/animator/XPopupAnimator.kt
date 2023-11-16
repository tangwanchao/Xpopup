package me.twc.popup.animator

import android.view.View
import android.widget.FrameLayout

/**
 * @author 唐万超
 * @date 2023/11/13
 */
interface XPopupAnimator {

    /**
     * 保证 containerView,popupView 宽高已经可用,调用即可
     */
    fun initAnimator(containerView: FrameLayout, popupView: View) {}

    /**
     * @param listener 动画开始后必须调用 [XPopupAnimatorListener.onEnd]
     */
    fun showStartAnimator(containerView: FrameLayout, popupView: View, listener: XPopupAnimatorListener) {}

    /**
     * @param listener 动画结束后必须调用 [XPopupAnimatorListener.onEnd]
     */
    fun showDismissAnimator(containerView: FrameLayout, popupView: View, listener: XPopupAnimatorListener) {}
}