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
import kotlinx.android.synthetic.main.m_searchbar_layout.view.*
import kotlin.math.hypot

class SearchBar @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var mInflater: LayoutInflater? = null

    /**
     * Menu inflate
     */
    private var menuRes: Int? = null
    private var menuItemClickListener: ((Int) -> Unit)? = null

    /**
     * EditText
     */
    private var editTextHint = "Search"

    /**
     * Toolbar
     */
    private var toolbarTitle = ""
    private var toolbarBackAction = false

    /**
     * Status SearchBar
     */
    private var searchStatus = true

    /**
     * Menu
     */
    private var textChangeClickListener: ((String) -> Unit)? = null
    private var callbackBackClickListener: (() -> Unit)? = null


    /**
     * Menu Actions Icons
     */
    private var iconSearch = R.drawable.m_ic_search_searchbar
    private var iconSearchClose = R.drawable.m_ic_close_searchbar
    private var iconBack = 0

    init {
        mInflater = LayoutInflater.from(context)
        mInflater?.inflate(R.layout.m_searchbar_layout, this, true)

        /**
         * Attrs
         */
        attrs?.let {

            /**
             * Menu inflate
             */
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchBar)
            if (typedArray.hasValue(R.styleable.SearchBar_addMenu)) {
                menuRes = typedArray.getResourceId(R.styleable.SearchBar_addMenu, 0)
            }

            /**
             * EditText
             */
            if (typedArray.hasValue(R.styleable.SearchBar_searchHint)) {
                editTextHint = typedArray.getString(R.styleable.SearchBar_searchHint) ?: "Search"
            }

            /**
             * Toolbar
             */
            if (typedArray.hasValue(R.styleable.SearchBar_toolbarTitle)) {
                toolbarTitle = typedArray.getString(R.styleable.SearchBar_toolbarTitle) ?: ""
            }

            if (typedArray.hasValue(R.styleable.SearchBar_backAction)) {
                toolbarBackAction = typedArray.getBoolean(R.styleable.SearchBar_backAction, false)
            }

            /**
             * Status SearchBar
             */
            if (typedArray.hasValue(R.styleable.SearchBar_showSearch)) {
                val b = typedArray.getBoolean(R.styleable.SearchBar_showSearch, searchStatus)
                searchStatus = !b
            }

            /**
             * Icons
             */
            if (typedArray.hasValue(R.styleable.SearchBar_iconSearch)) {
                iconSearch = typedArray.getResourceId(R.styleable.SearchBar_iconSearch, 0)
            }

            if (typedArray.hasValue(R.styleable.SearchBar_iconClose)) {
                iconSearchClose = typedArray.getResourceId(R.styleable.SearchBar_iconClose, 0)
            }

            if (typedArray.hasValue(R.styleable.SearchBar_iconBack)) {
                iconBack = typedArray.getResourceId(R.styleable.SearchBar_iconBack, 0)
            }
            typedArray.recycle()
        }

        /**
         * Menu inflate
         */
        menuRes?.let { inflateMenu(it) }

        /**
         * EditText
         */
        setSearchHint(editTextHint)

        mLayoutSearchBarEditText.keyActionDoneListener {
            textChangeClickListener?.let { it(mLayoutSearchBarEditText.text.toString()) }
            showToolbar()
        }

        /**
         * Toolbar
         */
        if (toolbarTitle.isNotEmpty()) setToolbarTitle(toolbarTitle)

        if (iconBack != 0) setToolbarBackIcon(iconBack)

        mSearchBarToolbar.setNavigationOnClickListener { callbackBackClickListener?.let { it1 -> it1() } }

        /**
         * Search Go
         */
        mLayoutSearchBarBack.setOnClickListener {
            textChangeClickListener?.let { it(mLayoutSearchBarEditText.text.toString()) }
            showToolbar()
        }

        /**
         * init
         */
        if (searchStatus) showToolbar(false) else showSearchBar(false)

        mSearchBarBtnAction.setOnClickListener {
            searchMenu()
        }
    }

    /**
     * Menu inflate and Actions
     */

    fun inflateMenu(menuResID: Int) {
        if (menuResID != 0) {
            mSearchBarToolbar.apply {
                inflateMenu(menuResID)
                setOnMenuItemClickListener {
                    menuItemClickListener?.let { it1 -> it1(it.itemId) }
                    true
                }
            }
        }
    }

    fun setMenuOnClickListener(callback: (Int) -> Unit) {
        menuItemClickListener = callback
    }

    /**
     * EditText Actions
     */

    fun setSearchHint(hint: String) {
        mLayoutSearchBarEditText.hint = hint
    }

    fun setSearchTextOkListener(callback: (String) -> Unit) {
        textChangeClickListener = callback
    }

    fun setSearchTextListener(callback: (String) -> Unit) {
        mLayoutSearchBarEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                callback(s.toString())
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun EditText.openKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun EditText.closeKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(this.windowToken, 0)
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
     * Toolbar
     */
    fun setToolbarTitle(toolbarTitle: String) {
        mSearchBarToolbar?.title = toolbarTitle
    }

    fun setToolbarBackIcon(iconResId: Int) {
        mSearchBarToolbar.navigationIcon = context.resources.getDrawable(iconResId, null)
    }

    fun setBackClickListener(callback: () -> Unit) {
        callbackBackClickListener = callback
    }

    /**
     * Status SearchBar
     */
    private fun searchMenu() = if (searchStatus) showToolbar() else showSearchBar()

    fun showSearchBar(animation: Boolean = true) {
        /**
         * set actions
         */
        searchStatus = true
        mSearchBarBtnAction.apply {
            isEnabled = false
            setImageDrawable(context.getDrawable(iconSearchClose))
        }

        mLayoutSearchBarEditText.apply {
            requestFocus()
            openKeyboard()
        }

        if (animation) {
            val x: Int = mSearchBarBtnActionCenter.x.toInt()
            val y: Int = mSearchBarBtnActionCenter.y.toInt()

            val startRadius = 0
            val endRadius = hypot(width.toDouble(), height.toDouble()).toInt()

            val anim = ViewAnimationUtils.createCircularReveal(
                mLayoutSearchBar, x, y, startRadius.toFloat(), endRadius.toFloat()
            )
            anim.addListener(object : AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}
                override fun onAnimationEnd(animator: Animator) {
                    mLayoutToolbar.visibility = View.GONE
                    mSearchBarBtnAction.isEnabled = true
                }

                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })
            mLayoutSearchBar.visibility = View.VISIBLE
            anim.start()
        } else {
            mLayoutSearchBarEditText.clearFocus()
            mLayoutSearchBar.visibility = View.VISIBLE
            mLayoutToolbar.visibility = View.GONE
            mSearchBarBtnAction.isEnabled = true
        }
    }

    fun showToolbar(animation: Boolean = true) {
        /**
         * set actions
         */
        searchStatus = false
        mLayoutToolbar.visibility = View.VISIBLE

        mSearchBarBtnAction.apply {
            isEnabled = false
            setImageDrawable(context.getDrawable(iconSearch))
        }

        mLayoutSearchBarEditText.apply {
            clearFocus()
            closeKeyboard()
        }

        if (animation) {
            val x: Int = mSearchBarBtnActionCenter.x.toInt()
            val y: Int = mSearchBarBtnActionCenter.y.toInt()
            val startRadius = hypot(width.toDouble(), height.toDouble()).toInt()
            val endRadius = 0

            val anim = ViewAnimationUtils.createCircularReveal(
                mLayoutSearchBar, x, y, startRadius.toFloat(), endRadius.toFloat()
            )

            anim.addListener(object : AnimatorListener {
                override fun onAnimationStart(animator: Animator) {}
                override fun onAnimationEnd(animator: Animator) {
                    mLayoutSearchBar.visibility = View.GONE
                    mSearchBarBtnAction.isEnabled = true
                }

                override fun onAnimationCancel(animator: Animator) {}
                override fun onAnimationRepeat(animator: Animator) {}
            })
            anim.start()
        } else {
            mLayoutSearchBar.visibility = View.GONE
            mSearchBarBtnAction.isEnabled = true
        }
    }
}