package me.twc.popup.enum

/**
 * @author 唐万超
 * @date 2023/11/10
 *
 * popup 背景类型
 * 会根据不同的背景类型创建不同的背景
 *
 * @see PopupOutsideClickType
 */
enum class PopupBackgroundType {
    /**
     * 透明背景
     */
    TRANSPARENT,

    /**
     * 阴影背景
     */
    SHADOW,

    /**
     * 顶部阴影背景(pop bottom 到顶部有阴影)
     */
    TOP_SHADOW,

    /**
     * 底部阴影背景(pop top 到顶部有阴影)
     */
    BOTTOM_SHADOW;
}