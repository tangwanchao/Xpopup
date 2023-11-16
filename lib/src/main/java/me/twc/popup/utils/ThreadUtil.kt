package me.twc.popup.utils

import android.os.Handler
import android.os.Looper

/**
 * @author 唐万超
 * @date 2023/11/24
 */
object ThreadUtil {

    private val mMainHandler by lazy { Handler(Looper.getMainLooper()) }

    fun callInMainThread(block: () -> Unit) {
        if (Looper.getMainLooper().thread == Thread.currentThread()) {
            block()
        } else {
            mMainHandler.post(block)
        }
    }
}