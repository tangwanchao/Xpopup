package me.twc.popup.enum

/**
 * @author 唐万超
 * @date 2023/11/20
 *
 *
 * 根据 [AtViewPosition] 的值进行对齐
 *
 * @see AtViewStrategy
 */
enum class AtViewAlign {
    /**
     * popup left/top 和 atView left/top 对齐
     */
    START,

    /**
     * popup middle 和 atView middle 对齐
     */
    MIDDLE,

    /**
     * popup right/bottom 和 atView right/bottom 对齐
     */
    END;
}