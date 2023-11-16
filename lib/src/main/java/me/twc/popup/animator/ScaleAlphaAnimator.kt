package me.twc.popup.animator

import android.view.View
import android.widget.FrameLayout
import me.twc.popup.utils.AnimatorUtil.applyXPopupParams
import me.twc.popup.utils.AnimatorUtil.bindXPopupListener

/**
 * @author 唐万超
 * @date 2023/11/13
 */
class ScaleAlphaAnimator(
    private val mParams: XPopupAnimatorParams = XPopupAnimatorParams.DEFAULT
) : XPopupAnimator {

    override fun initAnimator(containerView: FrameLayout, popupView: View) {
        containerView.alpha = mParams.startAlpha
        popupView.alpha = mParams.startAlpha
        popupView.scaleX = mParams.startScaleX
        popupView.scaleY = mParams.startScaleY
    }

    override fun showStartAnimator(containerView: FrameLayout, popupView: View, listener: XPopupAnimatorListener) {
        containerView.animate()
            .applyXPopupParams(mParams)
            .alpha(mParams.endAlpha)
        popupView.animate()
            .bindXPopupListener(listener)
            .applyXPopupParams(mParams)
            .alpha(mParams.endAlpha)
            .scaleX(mParams.endScaleX)
            .scaleY(mParams.endScaleY)
    }

    override fun showDismissAnimator(containerView: FrameLayout, popupView: View, listener: XPopupAnimatorListener) {
        containerView.animate()
            .applyXPopupParams(mParams)
            .alpha(mParams.startAlpha)
        popupView.animate()
            .applyXPopupParams(mParams)
            .bindXPopupListener(listener)
            .alpha(mParams.startAlpha)
            .scaleX(mParams.startScaleX)
            .scaleY(mParams.startScaleY)
    }
}