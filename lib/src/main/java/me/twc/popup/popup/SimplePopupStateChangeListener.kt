package me.twc.popup.popup

import me.twc.popup.enum.PopupState

/**
 * @author 唐万超
 * @date 2023/11/24
 */
class SimplePopupStateChangeListener : PopupStateChangeListener {

    override fun onStateChanged(preState: PopupState, currentState: PopupState) {
        when (currentState) {
            PopupState.ANIMATE_SHOW -> onStartShowAnimator()
            PopupState.ANIMATE_DISMISS -> onStartDismissAnimator()
            PopupState.SHOW -> onShow()
            PopupState.DISMISS -> onDismiss()
        }
    }

    /**
     * popup 完全展示时调用
     */
    fun onShow() {}

    /**
     * popup 完全消失时调用
     */
    fun onDismiss() {}

    /**
     * popup 展示动画开始之前调用
     */
    fun onStartShowAnimator() {}


    /**
     * popup 消失动画开始之前调用
     *
     */
    fun onStartDismissAnimator() {

    }
}