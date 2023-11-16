package me.twc.popup.enum

/**
 * @author 唐万超
 * @date 2023/11/15
 *
 * 对齐方式参考 [AtViewAlign]
 *
 * @see [AtViewStrategy]
 */
enum class AtViewPosition {
    /**
     * popup 在 AtView 的左边展示
     */
    LEFT,

    /**
     * popup 在 AtView 的上边展示
     */
    TOP,

    /**
     * popup 在 AtView 的右边展示
     */
    RIGHT,

    /**
     * popup 在 AtView 的下边展示
     */
    BOTTOM;
}