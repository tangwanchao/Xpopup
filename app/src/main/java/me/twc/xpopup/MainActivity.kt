package me.twc.xpopup

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.appcompat.widget.AppCompatSpinner
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowInsetsCompat
import me.twc.popup.animator.TranslateAnimator
import me.twc.popup.enum.AnimatorDirection
import me.twc.popup.enum.AtViewAlign
import me.twc.xpopup.base.BaseActivity
import me.twc.xpopup.databinding.ActMainBinding
import me.twc.popup.enum.AtViewPosition
import me.twc.popup.enum.AtViewStrategy
import me.twc.popup.enum.PopupBackgroundType
import me.twc.popup.enum.PopupOutsideClickType
import me.twc.popup.popup.XPopup
import me.twc.popup.popup.XPopupBuilder

/**
 * @author 唐万超
 * @date 2023/11/09
 */
class MainActivity : BaseActivity() {

    private val mBinding by lazy { ActMainBinding.inflate(layoutInflater) }
    private val mDrawerPopup by lazy { createDrawerPopup() }
    private var mAtViewPosition = AtViewPosition.LEFT
    private var mAtViewAlign = AtViewAlign.START
    private var mAtViewStrategy: AtViewStrategy = AtViewStrategy.FORCE
    private var mBackgroundType = PopupBackgroundType.SHADOW
    private var mPopupOutsideClickType: PopupOutsideClickType = PopupOutsideClickType.INTERCEPT_AND_DISMISS_POPUP
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        layoutFullScreen(isAppearanceLightStatusBars = true) { inset ->
            val topPadding = inset.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.statusBars()).top
            val bottomPadding = inset.getInsetsIgnoringVisibility(WindowInsetsCompat.Type.navigationBars()).bottom
            mBinding.toolbar.setPadding(0, topPadding, 0, 0)
            mBinding.root.setPadding(0, 0, 0, bottomPadding)
            return@layoutFullScreen null
        }
        initView()
        initListener()
    }

    private fun initView() {
        addViews()
    }

    private fun initDrawer(drawer: View) {
        drawer as ViewGroup
        setArrayValues(
            drawer,
            R.id.spinner_at_view_postion,
            AtViewPosition.values(),
            AtViewPosition.LEFT,
            ::mAtViewPosition::set
        )
        setArrayValues(
            drawer,
            R.id.spinner_at_view_align,
            AtViewAlign.values(),
            AtViewAlign.START,
            ::mAtViewAlign::set
        )
        setArrayValues(
            drawer,
            R.id.spinner_at_view_strategy,
            AtViewStrategy.values(),
            AtViewStrategy.FORCE,
            ::mAtViewStrategy::set
        )
        setArrayValues(
            drawer,
            R.id.spinner_background_type,
            PopupBackgroundType.values(),
            PopupBackgroundType.SHADOW,
            ::mBackgroundType::set
        )
        setArrayValues(
            drawer,
            R.id.spinner_outside_click_type,
            PopupOutsideClickType.values(),
            PopupOutsideClickType.INTERCEPT_AND_DISMISS_POPUP,
            ::mPopupOutsideClickType::set
        )
    }

    private fun initListener() {
        mBinding.ivMenu.setOnClickListener { toggleDrawer() }
    }

    private fun createDrawerPopup(): XPopup {
        return XPopupBuilder(window)
            .atView(mBinding.toolbar, AtViewPosition.BOTTOM, AtViewAlign.END, arrayOf(AtViewStrategy.RESIZE))
            .setPopupAnimator(TranslateAnimator(mDirection = AnimatorDirection.LEFT))
            .setBackgroundType(PopupBackgroundType.BOTTOM_SHADOW)
            .asCustomView(R.layout.pop_drawer)
            .create(::initDrawer)
    }

    private fun toggleDrawer() {
        mDrawerPopup.postToggle()
    }

    private fun <T> setArrayValues(
        parent: ViewGroup,
        @IdRes id: Int,
        values: Array<T>,
        select: T,
        onUpdate: (value: T) -> Unit
    ) = parent.findViewById<AppCompatSpinner>(id).apply {
        onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                onUpdate(values[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_spinner_item, values)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.adapter = adapter
        setSelection(values.indexOf(select))
    }


    private fun addViews() = repeat(30) {
        val view = newBlockView(it.toString())
        mBinding.clContent.addView(view)
        mBinding.flowContent.addView(view)
    }

    private fun newBlockView(text: CharSequence): TextView {
        val view = TextView(mBinding.clContent.context)
        view.layoutParams = ConstraintLayout.LayoutParams(100.pt, 100.pt)
        view.setBackgroundColor(Color.BLUE)
        view.id = View.generateViewId()
        view.setOnClickListener {
            XPopupBuilder(window)
                .asCustomView(R.layout.pop_center)
                .atView(view, mAtViewPosition, mAtViewAlign, arrayOf(
                    AtViewStrategy.AUTO_POSITION,AtViewStrategy.AUTO_ALIGN,AtViewStrategy.RESIZE
                ))
                .setBackgroundType(mBackgroundType)
                .setOutsideClickType(mPopupOutsideClickType)
                .create()
                .postShow()
        }
        view.text = text
        view.setTextColor(Color.WHITE)
        view.setTextSize(TypedValue.COMPLEX_UNIT_PT, 16f)
        return view
    }
}