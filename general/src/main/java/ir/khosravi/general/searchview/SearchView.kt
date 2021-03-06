package ir.khosravi.general.searchview

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.view_searchview_rtl.view.*



class SearchView @JvmOverloads constructor(
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
    private var searchState: Int = SearchState.CLEAR
    private lateinit var mTextChangedListener: OnTextChangedListener
    private val mClickListener = OnClickListener {
        when (it.id) {
            R.id.btn_search -> {
                searchState = SearchState.READY_TO_TYPE
                updateViewsVisibility()
                input_search.requestFocus()
                showKeyboard(input_search)
            }
            R.id.btn_clear -> {
                when (searchState) {
                    SearchState.READY_TO_TYPE -> {
                        hideKeyboard()
                        input_search.clearFocus()
                        searchState = SearchState.CLEAR
                        updateViewsVisibility()
                    }
                    SearchState.TYPING -> {
                        input_search.text?.clear()
                        input_search.clearFocus()
                        searchState = SearchState.READY_TO_TYPE
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
        searchState = SearchState.CLEAR
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
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideKeyboard()
            }
            true
        }

        input_search.addTextChangedListener { charSequence ->
            try {
                searchState =
                        SearchState.TYPING
                updateViewsVisibility()
                val query = charSequence.toString()
                val drawableRes: Int =
                        if (TextUtils.isEmpty(query)) R.drawable.round_search_white_48 else 0

                setInputDrawable(drawableRes)

                if (::mTextChangedListener.isInitialized) mTextChangedListener.onTextChanged(query)
            } catch (error: Throwable) {
                raiseError(error)
            }
        }
    }

    private fun setInputDrawable(@DrawableRes drawableRes: Int) {
        val drawable : Drawable? = ContextCompat.getDrawable(context, drawableRes)
        drawable?.setBounds(0, 0, 12, 12)
        input_search.setCompoundDrawablesWithIntrinsicBounds(
                null, null, drawable, null
        )
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
            SearchState.CLEAR -> {
                btn_clear.visibility = View.GONE
                input_search.visibility = View.GONE
                btn_search.visibility = View.VISIBLE
                txt_title.visibility = View.VISIBLE
            }
            SearchState.READY_TO_TYPE -> {
                btn_clear.visibility = View.VISIBLE
                input_search.visibility = View.VISIBLE
                btn_search.visibility = View.GONE
                txt_title.visibility = View.GONE
                setInputDrawable(R.drawable.round_search_white_48)
            }
            SearchState.TYPING -> {
                btn_clear.visibility = View.VISIBLE
                input_search.visibility = View.VISIBLE
                btn_search.visibility = View.GONE
                txt_title.visibility = View.GONE
            }
            else -> {
                searchState = SearchState.CLEAR
                updateViewsVisibility()
            }
        }
    }

    interface OnTextChangedListener {
        fun onTextChanged(query: String)
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(
            SearchState.CLEAR,
            SearchState.READY_TO_TYPE,
            SearchState.TYPING
    )
    annotation class State

    class SearchState {
        companion object {
            const val CLEAR: Int = -1
            const val READY_TO_TYPE: Int = 0
            const val TYPING: Int = 1
        }
    }

}