package me.twc.popup

import android.graphics.Color

/**
 * @author 唐万超
 * @date 2023/11/09
 */
object Constants {
    /**
     * 默认阴影背景颜色
     */
    val DEFAULT_SHADOW_BACKGROUND_COLOR = Color.parseColor("#7F000000")

    /**
     * 默认动画时长
     */
    const val DEFAULT_ANIMATE_TIME = 350L

    /**
     * alpha 动画默认起始值
     */
    const val DEFAULT_START_ALPHA = 0f

    /**
     * alpha 动画默认结束值
     */
    const val DEFAULT_END_ALPHA = 1f

    /**
     * scale 动画默认起始值
     */
    const val DEFAULT_START_SCALE_X = 0.75f
    const val DEFAULT_START_SCALE_Y = 0.75f

    /**
     * scale 动画默认结束值
     */
    const val DEFAULT_END_SCALE_X = 1f
    const val DEFAULT_END_SCALE_Y = 1f
}