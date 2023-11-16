package me.twc.popup.animator

import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import me.twc.popup.Constants

/**
 * @author 唐万超
 * @date 2023/11/16
 */
class XPopupAnimatorParams(
    val duration: Long = Constants.DEFAULT_ANIMATE_TIME,
    val interpolator: Interpolator = AccelerateInterpolator(),
    val startAlpha: Float = Constants.DEFAULT_START_ALPHA,
    val endAlpha: Float = Constants.DEFAULT_END_ALPHA,
    val startScaleX: Float = Constants.DEFAULT_START_SCALE_X,
    val endScaleX: Float = Constants.DEFAULT_END_SCALE_X,
    val startScaleY: Float = Constants.DEFAULT_START_SCALE_Y,
    val endScaleY: Float = Constants.DEFAULT_END_SCALE_Y,
) {

    companion object {
        val DEFAULT = XPopupAnimatorParams()
    }
}