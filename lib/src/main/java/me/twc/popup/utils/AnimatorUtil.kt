package me.twc.popup.utils

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.ViewPropertyAnimator
import androidx.core.animation.doOnEnd
import me.twc.popup.animator.XPopupAnimatorListener
import me.twc.popup.animator.XPopupAnimatorParams

/**
 * @author 唐万超
 * @date 2023/11/17
 */
@Suppress("unused")
object AnimatorUtil {
    inline fun <reified T : Animator> T.bindXPopupListener(listener: XPopupAnimatorListener): T {
        removeAllListeners()
        doOnEnd { listener.onEnd() }
        return this
    }


    fun ViewPropertyAnimator.bindXPopupListener(listener: XPopupAnimatorListener): ViewPropertyAnimator {
        setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                listener.onEnd()
            }
        })
        return this
    }

    inline fun <reified T : Animator> T.applyXPopupParams(params: XPopupAnimatorParams): T {
        duration = params.duration
        interpolator = params.interpolator
        return this
    }

    fun ViewPropertyAnimator.applyXPopupParams(params: XPopupAnimatorParams): ViewPropertyAnimator {
        duration = params.duration
        interpolator = params.interpolator
        return this
    }
}