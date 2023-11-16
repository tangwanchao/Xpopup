package me.twc.popup.popup

import me.twc.popup.enum.PopupState

/**
 * @author 唐万超
 * @date 2023/11/24
 */
interface PopupStateChangeListener {


    /**
     * 状态改变调用
     *
     * 回调将在主线程调用
     */
    fun onStateChanged(preState: PopupState, currentState: PopupState) {}
}