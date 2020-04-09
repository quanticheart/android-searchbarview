package com.quanticheart.searchbar

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.FrameLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.m_toobar.view.*
import kotlin.math.hypot


class SearchBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var mInflater: LayoutInflater? = null
//    private var menu: MenuItem

    init {
        mInflater = LayoutInflater.from(context)
        mInflater?.inflate(R.layout.m_toobar, this, true)

//        attrs?.let {
//            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.MSeekBar)
//            if (typedArray.hasValue(R.styleable.MSeekBar_showPim)) {
//                showPim = typedArray.getBoolean(R.styleable.MSeekBar_showPim, false)
//                showPim(showPim)
//            }
//
//            if (typedArray.hasValue(R.styleable.MSeekBar_showPimOnTouch)) {
//                showPimOnTouch = typedArray.getBoolean(R.styleable.MSeekBar_showPimOnTouch, false)
//            }
//
//            if (typedArray.hasValue(R.styleable.MSeekBar_progressSeekbar)) {
//                val progressBase = typedArray.getInt(R.styleable.MSeekBar_progressSeekbar, 0)
//                val progress = if (progressBase > 100) 100 else if (progressBase < 0) 0 else progressBase
//                setProgress(progress)
//            }
//            typedArray.recycle()
//        }

        menuToolbarSearch.apply {
            inflateMenu(R.menu.m_menu_search_toolbar)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_search -> {
                        Toast.makeText(context, "DEU CERTO", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                    }
                }
                true
            }
        }

        menuSearchClose.setOnClickListener {
            Toast.makeText(context, "DEU CERTO CLOSE", Toast.LENGTH_LONG).show()
        }

        menuSearch.setOnClickListener {
            searchMenu()
        }

    }

    private var searchStatus = false

    private fun searchMenu() = if (searchStatus) searchClose() else searchOpen()

    private fun searchOpen() {
        menuSearch.isEnabled = false
        menuSearch.setImageDrawable(context.getDrawable(R.drawable.ic_close_searchbar))

        val x: Int = menuSearchCenter.x.toInt()
        val y: Int = menuSearchCenter.y.toInt()

        val startRadius = 0
        val endRadius = hypot(width.toDouble(), height.toDouble()).toInt()

        val anim = ViewAnimationUtils.createCircularReveal(
            mSearchToolbarLayout,
            x,
            y,
            startRadius.toFloat(),
            endRadius.toFloat()
        )

        anim.addListener(object : AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {
                mSearchToolbar.visibility = View.GONE
                menuSearch.isEnabled = true
            }

            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })

        mSearchToolbarLayout.visibility = View.VISIBLE
        anim.start()
        searchStatus = true
    }

    private fun searchClose() {
        menuSearch.isEnabled = false
        mSearchToolbar.visibility = View.VISIBLE
        menuSearch.setImageDrawable(context.getDrawable(R.drawable.m_search_bar_icon))

        val x: Int = menuSearchCenter.x.toInt()
        val y: Int = menuSearchCenter.y.toInt()

        val startRadius = hypot(width.toDouble(), height.toDouble()).toInt()
        val endRadius = 0

        val anim = ViewAnimationUtils.createCircularReveal(
            mSearchToolbarLayout,
            x,
            y,
            startRadius.toFloat(),
            endRadius.toFloat()
        )
        anim.addListener(object : AnimatorListener {
            override fun onAnimationStart(animator: Animator) {}
            override fun onAnimationEnd(animator: Animator) {
                mSearchToolbarLayout.visibility = View.GONE
                menuSearch.isEnabled = true
            }

            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })
        anim.start()
        searchStatus = false
    }
}