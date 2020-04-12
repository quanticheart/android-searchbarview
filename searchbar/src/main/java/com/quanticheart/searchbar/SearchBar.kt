@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.quanticheart.searchbar

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.PopupWindow
import android.widget.TextView
import com.quanticheart.searchbar.databaseSearch.DataBaseSearchBar
import com.quanticheart.searchbar.databaseSearch.HistorySearchAdapter
import com.quanticheart.searchbar.databaseSearch.entity.SearchHistoryModel
import kotlinx.android.synthetic.main.m_dialog_history.view.*
import kotlinx.android.synthetic.main.m_searchbar_layout.view.*
import java.util.*
import kotlin.collections.ArrayList
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
    private var textSendClickListener: ((String) -> Unit)? = null
    private var textWatcherChangeListener: ((String) -> Unit)? = null
    private var callbackBackClickListener: (() -> Unit)? = null

    /**
     * Menu Actions Icons
     */
    private var iconSearch = R.drawable.m_ic_search_searchbar
    private var iconSearchClose = R.drawable.m_ic_close_searchbar
    private var iconBack = 0

    /**
     * DataBase
     */
    private var databaseEnable = false
    private var databaseDialogEnable = true
    private var databaseSearchList: ArrayList<SearchHistoryModel>? = null
    private var database: DataBaseSearchBar? = null

    /**
     * Google Speech
     */

    private var googleSpeech = true

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

            /**
             * DataBase
             */
            if (typedArray.hasValue(R.styleable.SearchBar_historySearchDatabase)) {
                databaseEnable = typedArray.getBoolean(
                    R.styleable.SearchBar_historySearchDatabase,
                    databaseEnable
                )
                if (databaseEnable) {
                    database = DataBaseSearchBar(context)
                }
            }

            if (typedArray.hasValue(R.styleable.SearchBar_historySearchDialog)) {
                databaseDialogEnable = typedArray.getBoolean(
                    R.styleable.SearchBar_historySearchDialog,
                    databaseDialogEnable
                )
            }

            /**
             * Google Speech
             */

            if (typedArray.hasValue(R.styleable.SearchBar_showGoogleSpeech)) {
                googleSpeech = typedArray.getBoolean(
                    R.styleable.SearchBar_showGoogleSpeech,
                    googleSpeech
                )
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
            sendText(
                mLayoutSearchBarEditText.text.toString(),
                databaseEnable
            )
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
            sendText(
                mLayoutSearchBarEditText.text.toString(),
                databaseEnable
            )
            showToolbar()
        }

        /**
         * DataBase dialog
         */

        if (historyDialogStatus()) {
            createDatabaseList()

            mSearchBarBtnMenu.setOnClickListener {
                if (menuIsShowing())
                    hideDialogDeleteHistory()
                else
                    showDialogDeleteHistory()
            }
        }

        /**
         * Google Speech
         */

        if (googleSpeech) {

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
        textSendClickListener = callback
    }

    fun setSearchTextListener(callback: (String) -> Unit) {
        textWatcherChangeListener = callback
        mLayoutSearchBarEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                sendText(s.toString(), false)
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

        if (historyDialogStatus())
            showDialogHistory()

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
            setText("")
            clearFocus()
            closeKeyboard()
        }

        if (historyDialogStatus())
            closeAllDialogs()

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

    /**
     * Send action
     */
    private fun sendText(searchText: String, databaseInsert: Boolean) {
        filterHistoryList(searchText.clearString())
        if (searchText.clearString().isNotEmpty()) {
            textWatcherChangeListener?.let { it(searchText) }
            textSendClickListener?.let { it(searchText) }
            if (databaseInsert) {
                databaseSearchList?.let { list ->
                    if (!list.contains(searchText.clearString())) {
                        database?.insertInHistory(searchText.clearString())
                        createDatabaseList()
                    }
                }
            }
        }
    }

    /**
     * History Dialog
     */

    private fun historyDialogStatus(): Boolean = databaseEnable && databaseDialogEnable

    private var dialogHistory: PopupWindow? = null
    private var historyAdapter: HistorySearchAdapter? = null

    private fun showDialogHistory() {
        databaseSearchList?.let { list ->
            dialogHistory?.let {
                dialogHistory?.showAsDropDown(mLayoutToolbar, Gravity.CENTER, 0, 0)
            } ?: run {
                val view = LayoutInflater.from(context)
                    .inflate(R.layout.m_dialog_history, mLayoutToolbar, false)

                historyAdapter = HistorySearchAdapter(view.mListHistorySearchBar, {
                    sendText(it, false)
                    showToolbar()
                }, {
                    database?.deleteHistoryRowById(it)
                    createDatabaseList()
                })

                historyAdapter?.addList(list)

                dialogHistory =
                    PopupWindow(
                        view,
                        mLayoutToolbar.width,
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        true
                    )
                dialogHistory?.apply {
                    elevation = 5f
                    isFocusable = false
                    isOutsideTouchable = false
                    softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
                    showAsDropDown(mLayoutToolbar, Gravity.CENTER, 0, 0)
                }
            }
        }

    }

    private fun hideDialogHistory() {
        dialogHistory?.dismiss()
        dialogHistory = null
    }

    private fun createDatabaseList() {
        databaseSearchList?.clear()
        databaseSearchList = database?.getHistoryList()
        if (databaseSearchList?.size ?: 0 > 0) {
            databaseSearchList?.reverse()
            mSearchBarBtnMenu.visibility = View.VISIBLE
        } else
            mSearchBarBtnMenu.visibility = View.GONE
    }

    private fun String.clearString(): String = this.replace("\\s+".toRegex(), " ").trim()

    private fun ArrayList<SearchHistoryModel>.contains(text: String): Boolean {
        for (model in this) {
            if (model.historyText == text) return true
        }
        return false
    }

    /**
     * Dialog delete history
     */

    private var dialogMenuHistory: PopupWindow? = null

    private fun showDialogDeleteHistory() {
        val view: View =
            LayoutInflater.from(context)
                .inflate(R.layout.m_dialog_history_clear_history, mLayoutToolbar, false)

        view.setOnClickListener {
            database?.deleteTable()
            createDatabaseList()
            closeAllDialogs()
        }

        dialogMenuHistory = PopupWindow(
            view,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            false
        )

        dialogMenuHistory?.apply {
            elevation = 5f
            isFocusable = false
            isOutsideTouchable = false
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
            showAsDropDown(mSearchBarBtnMenu, Gravity.CENTER, 0, 0)
        }
    }

    private fun menuIsShowing(): Boolean = dialogMenuHistory?.isShowing ?: false

    private fun hideDialogDeleteHistory() {
        dialogMenuHistory?.dismiss()
        dialogMenuHistory = null
    }

    /**
     * Close all dialogs
     */

    private fun closeAllDialogs() {
        hideDialogDeleteHistory()
        hideDialogHistory()
    }

    /**
     * Filter database
     */
    private fun filterHistoryList(name: String) {
        databaseSearchList?.let {
            if (name.isEmpty()) {
                historyAdapter?.addList(it)
            } else {
                val filterList = ArrayList<SearchHistoryModel>()
                for (history in it) {
                    if (history.historyText.toLowerCase(Locale.getDefault()).contains(
                            name.toLowerCase(Locale.getDefault())
                        )
                    ) {
                        filterList.add(history)
                    }
                }
                historyAdapter?.addList(filterList)
            }
        }
    }
}