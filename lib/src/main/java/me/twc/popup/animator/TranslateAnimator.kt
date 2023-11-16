package me.twc.popup.animator

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.View
import android.widget.FrameLayout
import me.twc.popup.enum.AnimatorDirection
import me.twc.popup.utils.AnimatorUtil.applyXPopupParams
import me.twc.popup.utils.AnimatorUtil.bindXPopupListener

/**
 * @author 唐万超
 * @date 2023/11/13
 *
 * @param mDirection 平移目标方向
 */
class TranslateAnimator(
    private val mParams: XPopupAnimatorParams = XPopupAnimatorParams.DEFAULT,
    private val mDirection: AnimatorDirection = AnimatorDirection.BOTTOM
) : XPopupAnimator {

    private lateinit var mTranslateAnimator: ObjectAnimator

    @SuppressLint("Recycle")
    override fun initAnimator(containerView: FrameLayout, popupView: View) {
        if (!::mTranslateAnimator.isInitialized) {
            val propertyName: String
            val start: Int
            val end: Int
            when (mDirection) {
                AnimatorDirection.LEFT -> {
                    propertyName = "left"
                    start = popupView.right
                    end = popupView.left
                    popupView.left = popupView.right
                }

                AnimatorDirection.TOP -> {
                    propertyName = "top"
                    start = popupView.bottom
                    end = popupView.top
                    popupView.top = popupView.bottom
                }

                AnimatorDirection.RIGHT -> {
                    propertyName = "right"
                    start = popupView.left
                    end = popupView.right
                    popupView.right = popupView.left
                }

                AnimatorDirection.BOTTOM -> {
                    propertyName = "bottom"
                    start = popupView.top
                    end = popupView.bottom
                    popupView.bottom = popupView.top
                }
            }
            mTranslateAnimator = ObjectAnimator.ofInt(popupView, propertyName, start, end)
                .applyXPopupParams(mParams)
        }
    }

    override fun showStartAnimator(containerView: FrameLayout, popupView: View, listener: XPopupAnimatorListener) {
        mTranslateAnimator.bindXPopupListener(listener)
        if (mTranslateAnimator.isRunning) {
            mTranslateAnimator.reverse()
        } else {
            mTranslateAnimator.start()
        }
    }

    override fun showDismissAnimator(containerView: FrameLayout, popupView: View, listener: XPopupAnimatorListener) {
        mTranslateAnimator.bindXPopupListener(listener)
        mTranslateAnimator.reverse()
    }
}