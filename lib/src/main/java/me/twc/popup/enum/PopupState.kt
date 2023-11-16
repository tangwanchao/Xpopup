package me.twc.popup.enum

/**
 * @author 唐万超
 * @date 2023/11/10
 *
 * popup 状态
 *
 */
enum class PopupState {
    /**
     * 动画状态(消失->展示)
     */
    ANIMATE_SHOW,

    /**
     * 动画状态(展示->消失)
     */
    ANIMATE_DISMISS,

    /**
     * 完全展示状态(动画已经结束)
     */
    SHOW,

    /**
     * 完全消失状态(动画已经结束)
     */
    DISMISS;

    fun isShowOrAnimateShow(): Boolean {
        return this == SHOW || this == ANIMATE_SHOW
    }

    fun isDismissOrAnimateDismiss(): Boolean {
        return this == DISMISS || this == ANIMATE_DISMISS
    }
}