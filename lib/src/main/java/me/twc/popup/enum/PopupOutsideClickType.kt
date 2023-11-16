package me.twc.popup.enum

/**
 * @author 唐万超
 * @date 2023/11/13
 *
 * 点击 popup 外部区域应该做什么
 *
 * @see PopupBackgroundType
 */
enum class PopupOutsideClickType(
    val dismissPopup: Boolean
) {
    /**
     * 拦截所有点击事件
     */
    INTERCEPT(false),

    /**
     * 拦截所有点击事件并关闭 popup
     */
    INTERCEPT_AND_DISMISS_POPUP(true),

    /**
     * 拦截背景区域点击事件,非背景区域点击穿透
     */
    INTERCEPT_BACKGROUND(false),

    /**
     * 拦截背景区域点击事件并关闭 popup,非背景区域点击穿透
     */
    INTERCEPT_BACKGROUND_AND_DISMISS_POPUP(true),

    /**
     * 所有区域点击穿透
     */
    CLICK_THROUGH(false);
}