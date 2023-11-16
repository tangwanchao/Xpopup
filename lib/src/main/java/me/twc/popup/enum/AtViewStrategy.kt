package me.twc.popup.enum

import android.view.View

/**
 * @author 唐万超
 * @date 2023/11/20
 *
 * 使用指定的 [AtViewPosition] 和 [AtViewAlign] 后,popup 放不下策略
 *
 * @param isComputeEnum 是否是计算时策略,计算时策略将混合并优先于非计算时策略
 *
 * @see AtViewPosition
 * @see AtViewAlign
 */
enum class AtViewStrategy(
    val isComputeEnum: Boolean
) {
    /**
     * 在计算 popup 位置时,如果使用默认的 [AtViewPosition] 和 [AtViewAlign] 无法完全显示 popup,有以下两种处理方案
     * 1.如果没有同时设置 [AUTO_ALIGN],将直接改变 [AtViewPosition] 尝试完全显示 popup
     * 2.如果同时设置 [AUTO_ALIGN],将先改变 [AtViewAlign] 后改变 [AtViewPosition]
     *
     * 伪代码:
     * for(atViewPosition in atViewPositions){
     *    for(atViewAlign in atViewAligns){
     *      // 计算位置
     *    }
     * }
     *
     * 注意: 如果默认位置是 [AtViewPosition.LEFT] 将先使用 [AtViewPosition.RIGHT] 之后使用 [AtViewPosition.TOP], [AtViewPosition.BOTTOM]
     * 注意: 如果默认位置是 [AtViewPosition.RIGHT] 将先使用 [AtViewPosition.LEFT] 之后使用 [AtViewPosition.TOP], [AtViewPosition.BOTTOM]
     * 注意: 如果默认位置是 [AtViewPosition.TOP] 将先使用 [AtViewPosition.BOTTOM] 之后使用 [AtViewPosition.LEFT], [AtViewPosition.RIGHT]
     * 注意: 如果默认位置是 [AtViewPosition.BOTTOM] 将先使用 [AtViewPosition.TOP] 之后使用 [AtViewPosition.LEFT], [AtViewPosition.RIGHT]
     */
    AUTO_POSITION(true),

    /**
     * 在计算 popup 位置时,变更 popup 对齐方式,可以配置 [AUTO_POSITION] 一起使用
     *
     * 注意: 如果默认对齐是 [AtViewAlign.START] 之后尝试 [AtViewAlign.MIDDLE],[AtViewAlign.END]
     * 注意: 如果默认对齐是 [AtViewAlign.MIDDLE] 之后尝试 [AtViewAlign.START],[AtViewAlign.END]
     * 注意: 如果默认对齐是 [AtViewAlign.END] 之后尝试 [AtViewAlign.START],[AtViewAlign.MIDDLE]
     */
    AUTO_ALIGN(true),

    /**
     * 在计算 popup 位置后,如果 popup 不能完全显示
     *
     * 强制放下,可能导致部分 popup 显示到屏幕外
     * 一般发生在 popup 特别特别大的情况
     */
    FORCE(false),

    /**
     * 在计算 popup 位置后,如果 popup 不能完全显示
     *
     * 滚动,尝试滚动父 View 后显示
     *
     * @see [View.requestRectangleOnScreen]
     */
    SCROLL(false),

    /**
     * 在计算 popup 位置后,如果 popup 不能完全显示
     *
     * 改变 popup 默认宽高,使其能够放下.
     *
     * 注意:一般带弹性布局的 popup 可以使用,否则可能导致显示和设计不一致
     */
    RESIZE(false);
}