package me.twc.xpopup

import com.blankj.utilcode.util.AdaptScreenUtils

/**
 * @author 唐万超
 * @date 2023/11/22
 */
val Number.pt
    get() = try {
        AdaptScreenUtils.pt2Px(this.toFloat())
    } catch (th: Throwable) {
        th.printStackTrace()
        this.toFloat().toInt()
    }