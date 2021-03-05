package ir.khosravi.searchview

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IntDef
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import ir.khosravi.searchview.mylibrary.R
import kotlinx.android.synthetic.main.view_searchview_rtl.view.*


open class RtlSearchView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    ConstraintLayout(
        context,
        attrs,
        defStyleAttr
    ) {

    @State
    private var searchState: Int =
            SearchState.None
    private lateinit var mTextChangedListener: OnTextChangedListener
    private val mClickListener = OnClickListener {
        when (it.id) {
            R.id.btn_search -> {
                searchState =
                        SearchState.ReadyToType
                updateViewsVisibility()
                input_search.requestFocus()
                showKeyboard(input_search)
            }
            R.id.btn_clear -> {
                when (searchState) {
                    SearchState.ReadyToType -> {
                        hideKeyboard()
                        input_search.clearFocus()
                        searchState =
                                SearchState.None
                        updateViewsVisibility()
                    }
                    SearchState.QueryTyping -> {
                        input_search.text?.clear()
                        input_search.clearFocus()
                        searchState =
                                SearchState.ReadyToType
                        updateViewsVisibility()
                    }
                }
            }
        }
    }


    init {
        setupAttributes(attrs!!)
    }

    fun setOnTextChangedListener(listener: OnTextChangedListener) {
        this.mTextChangedListener = listener
    }

    fun clearSearchBar() {
        input_search.text?.clear()
        hideKeyboard()
        searchState = SearchState.None
        updateViewsVisibility()
    }

    fun setTitleText(titleText: String) {
        txt_title.text = titleText
    }

    private fun setupAttributes(attrs: AttributeSet) {
        LayoutInflater.from(context).inflate(R.layout.view_searchview_rtl, this, true)

        updateViewsVisibility()

        btn_search.setOnClickListener(mClickListener)
        btn_clear.setOnClickListener(mClickListener)

        input_search.setOnEditorActionListener { _, actionId, _ ->
            Log.e("Majid", actionId.toString())
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
            }
            true
        }

        input_search.addTextChangedListener { charSequence ->
            try {
                searchState =
                        SearchState.QueryTyping
                updateViewsVisibility()
                val query = charSequence.toString()
                val drawableRes: Int =
                        if (TextUtils.isEmpty(query)) R.drawable.round_search_white_48 else 0

                input_search.setCompoundDrawablesWithIntrinsicBounds(
                        0, 0, drawableRes, 0
                )

                if (::mTextChangedListener.isInitialized) mTextChangedListener.onTextChanged(query)
            } catch (error: Throwable) {
                raiseError(error)
            }
        }
    }

    private fun showKeyboard(v: View) {
        this.requestFocus()
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun raiseError(error: Throwable) {
        Log.e(this::class.java.simpleName, error.message!!)
    }

    private fun updateViewsVisibility() {
        when (searchState) {
            SearchState.None -> {
                btn_clear.visibility = View.GONE
                input_search.visibility = View.GONE
                btn_search.visibility = View.VISIBLE
                txt_title.visibility = View.VISIBLE
            }
            SearchState.ReadyToType -> {
                btn_clear.visibility = View.VISIBLE
                input_search.visibility = View.VISIBLE
                btn_search.visibility = View.GONE
                txt_title.visibility = View.GONE
                input_search.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.round_search_white_48, 0
                )
            }
            SearchState.QueryTyping -> {
                btn_clear.visibility = View.VISIBLE
                input_search.visibility = View.VISIBLE
                btn_search.visibility = View.GONE
                txt_title.visibility = View.GONE
            }
            else -> {
                searchState =
                        SearchState.None
                updateViewsVisibility()
            }
        }
    }

    interface OnTextChangedListener {
        fun onTextChanged(query: String)
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
            SearchState.None,
            SearchState.ReadyToType,
            SearchState.QueryTyping
    )
    annotation class State

    class SearchState {
        companion object {
            const val None: Int = -1
            const val ReadyToType: Int = 0
            const val QueryTyping: Int = 1
        }
    }

}