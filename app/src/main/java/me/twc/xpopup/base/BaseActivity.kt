package me.twc.xpopup.base

import android.content.res.Resources
import android.graphics.Color
import android.view.View
import android.view.WindowInsets
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.blankj.utilcode.util.AdaptScreenUtils

/**
 * @author 唐万超
 * @date 2023/11/09
 */
open class BaseActivity : AppCompatActivity(){

    companion object {
        // 设计图宽度
        private const val DESIGN_WIDTH = 375
    }
    override fun getResources(): Resources {
        var resources = super.getResources()
        resources = AdaptScreenUtils.adaptWidth(resources, DESIGN_WIDTH)
        // 适配后,如果 xdpi 和 fontScale 不是我们想要的,我们修改他并更新
        resources.apply {
            val xdpi = this.getWantXdpi()
            val conf = this.configuration
            val displayMetrics = this.displayMetrics
            if (conf.fontScale != 1.0f || displayMetrics.xdpi != xdpi) {
                if (conf.fontScale != 1.0f) {
                    conf.fontScale = 1.0f
                }
                if (displayMetrics.xdpi != xdpi) {
                    displayMetrics.xdpi = xdpi
                }
                @Suppress("DEPRECATION")
                this.updateConfiguration(conf, displayMetrics)
            }
        }
        return resources
    }

    private fun Resources.getWantXdpi(): Float {
        return this.displayMetrics.widthPixels * 72f / DESIGN_WIDTH
    }

    fun layoutFullScreen(
        decorFitsSystemWindows: Boolean = false,
        isAppearanceLightStatusBars: Boolean = false,
        applyInsets: ((inset: WindowInsetsCompat) -> WindowInsets?)? = null
    ) {
        WindowCompat.setDecorFitsSystemWindows(window, decorFitsSystemWindows)
        window.statusBarColor = Color.TRANSPARENT
        val windowInsert = WindowCompat.getInsetsController(window, window.decorView)
        windowInsert.isAppearanceLightStatusBars = isAppearanceLightStatusBars

        window.decorView.setOnApplyWindowInsetsListener(View.OnApplyWindowInsetsListener { _, insets ->
            val insetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets, window.decorView)
            return@OnApplyWindowInsetsListener applyInsets?.invoke(insetsCompat) ?: insets
        })
    }
}