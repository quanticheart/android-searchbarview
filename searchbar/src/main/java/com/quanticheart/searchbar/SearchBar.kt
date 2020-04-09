@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.quanticheart.searchbar

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.m_toobar.view.*
import kotlin.math.hypot


class SearchBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var mInflater: LayoutInflater? = null
    /**
     * EditText
     */
    /**
     * Menu
     */
    private var textChangeClickListener: ((String) -> Unit)? = null
    private var menuItemClickListener: ((Int) -> Unit)? = null
    private var callbackBackClickListener: (() -> Unit)? = null
    private var menuRes: Int? = null

    /**
     * Menu Actions Icons
     */
    private var iconSearch = R.drawable.m_search_bar_icon
    private var iconSearchClose = R.drawable.ic_close_searchbar

    init {
        mInflater = LayoutInflater.from(context)
        mInflater?.inflate(R.layout.m_toobar, this, true)

        /**
         * Attrs
         */
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchBar)
            if (typedArray.hasValue(R.styleable.SearchBar_addMenu)) {
                menuRes = typedArray.getResourceId(R.styleable.SearchBar_addMenu, 0)
            }

            if (typedArray.hasValue(R.styleable.SearchBar_iconSearch)) {
                iconSearch = typedArray.getResourceId(R.styleable.SearchBar_iconSearch, 0)
            }

            if (typedArray.hasValue(R.styleable.SearchBar_iconClose)) {
                iconSearchClose = typedArray.getResourceId(R.styleable.SearchBar_iconClose, 0)
            }

            if (typedArray.hasValue(R.styleable.SearchBar_searchHint)) {
                mSearchEditText.hint = typedArray.getString(R.styleable.SearchBar_searchHint)
            }
            typedArray.recycle()
        }

        /**
         * Menu
         */

        menuRes?.let { res ->
            if (res != 0) {
                menuToolbarSearch.apply {
                    inflateMenu(res)
                    setOnMenuItemClickListener {
                        menuItemClickListener?.let { it1 -> it1(it.itemId) }
                        true
                    }
                }
            }
        }

        menuSearch.setImageDrawable(context.getDrawable(iconSearchClose))
        menuSearch.setImageDrawable(context.getDrawable(iconSearch))

        /**
         * Search Text
         */

        mSearchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        /**
         * Search Go
         */

        menuSearchGo.setOnClickListener {
            textChangeClickListener?.let { it(mSearchEditText.text.toString()) }
            closeSearch()
        }

        /**
         * Menu Icon Actions
         */

        menuSearch.setOnClickListener {
            searchMenu()
        }

        /**
         * Edittext
         */

        mSearchEditText.keyActionDoneListener {
            textChangeClickListener?.let { it(mSearchEditText.text.toString()) }
            closeSearch()
        }

        menuToolbarSearch.title = "Testes"
//        menuToolbarSearch.navigationIcon = context.resources.getDrawable(R.drawable.m_ic_back, null)
        menuToolbarSearch.setNavigationOnClickListener { callbackBackClickListener?.let { it1 -> it1() } }
    }

    private fun toolbarAction(
        toolbar: Toolbar,
        context: Context,
        toolbarTitle: String?
    ) {
        val bar = toolbar
        toolbarTitle?.let {
            bar?.title = it
        } ?: run { }

        toolbar.navigationIcon = context.resources.getDrawable(R.drawable.m_ic_back, null)
        toolbar.setNavigationOnClickListener { callbackBackClickListener?.let { it1 -> it1() } }
    }

    fun setBackClickListener(callback: () -> Unit) {
        callbackBackClickListener = callback
    }

    fun setMenuOnClickListener(callback: (Int) -> Unit) {
        menuItemClickListener = callback
    }

    fun setSearchTextListener(callback: (String) -> Unit) {
        textChangeClickListener = callback
    }

    private fun EditText?.keyActionDoneListener(action: () -> Unit) {
        this?.setOnEditorActionListener { _: TextView?, actionId: Int, event: KeyEvent? ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_SEARCH) {
                action()
            }
            true
        }
    }

    /**
     * Menu Actions, show and hide
     */
    private var searchStatus = false

    private fun searchMenu() = if (searchStatus) closeSearch() else openSearch()

    fun openSearch() {
        menuSearch.isEnabled = false
        menuSearch.setImageDrawable(context.getDrawable(iconSearchClose))
        mSearchEditText.apply {
            requestFocus()
            openKeyboard()
        }

        val x: Int = menuSearchCenter.x.toInt()
        val y: Int = menuSearchCenter.y.toInt()

        val startRadius = 0
        val endRadius = hypot(width.toDouble(), height.toDouble()).toInt()

        val anim = ViewAnimationUtils.createCircularReveal(
            mSearchToolbarLayout, x, y, startRadius.toFloat(), endRadius.toFloat()
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

    fun closeSearch() {
        menuSearch.isEnabled = false
        mSearchToolbar.visibility = View.VISIBLE
        menuSearch.setImageDrawable(context.getDrawable(iconSearch))
        mSearchEditText.apply {
            clearFocus()
            closeKeyboard()
        }

        val x: Int = menuSearchCenter.x.toInt()
        val y: Int = menuSearchCenter.y.toInt()

        val startRadius = hypot(width.toDouble(), height.toDouble()).toInt()
        val endRadius = 0

        val anim = ViewAnimationUtils.createCircularReveal(
            mSearchToolbarLayout, x, y, startRadius.toFloat(), endRadius.toFloat()
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

    private fun EditText.openKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun EditText.closeKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(this.windowToken, 0)
    }
}