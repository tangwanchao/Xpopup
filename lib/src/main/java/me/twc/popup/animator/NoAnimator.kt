package me.twc.popup.animator

import android.view.View
import android.widget.FrameLayout

/**
 * @author 唐万超
 * @date 2023/11/13
 */
object NoAnimator : XPopupAnimator {
    override fun showStartAnimator(containerView: FrameLayout, popupView: View, listener: XPopupAnimatorListener) {
        listener.onEnd()
    }

    override fun showDismissAnimator(containerView: FrameLayout, popupView: View, listener: XPopupAnimatorListener) {
        listener.onEnd()
    }
}