package de.felixlf.gradingscale.ui

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.EditText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*


@FlowPreview
fun EditText.onSelectedTextChanged(onTouch: Flow<Int>) =
  onTyping().distinctUntilChanged()
    .combineTransform(onTouch) { text, id ->
      emit(text to id)
    }.filter { it.second == this.id }
    .map { it.first }.distinctUntilChanged()


@ExperimentalCoroutinesApi
fun <T : Adapter> AdapterView<T>.selectedPosFlow() = callbackFlow<Int> {
  val mListener = object : AdapterView.OnItemSelectedListener {
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
      offer(position)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
  }
  onItemSelectedListener = mListener
  awaitClose { adapter = null }
}


@ExperimentalCoroutinesApi
fun View.onTouch(): Flow<Int> = callbackFlow {
  val listener = View.OnTouchListener { v, _ ->
    offer(v.id)
    v.performClick()
  }
  setOnTouchListener(listener)
  awaitClose { setOnTouchListener(null) }
}



@ExperimentalCoroutinesApi
fun EditText.onTyping() = callbackFlow<String> {
  val watcher = object : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable?) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
      s?.let { offer(StringBuilder(it).toString()) }
    }
  }
  addTextChangedListener(watcher)
  awaitClose { removeTextChangedListener(watcher) }
}


@ExperimentalCoroutinesApi
fun EditText.onTouchTI() = callbackFlow<Int> {

  val listener = View.OnFocusChangeListener { v, hasFocus ->
    if (hasFocus()) offer(v.id)
  }

  val clickListener = View.OnClickListener { v ->
    offer(v.id)
  }

  setOnClickListener(clickListener)
  onFocusChangeListener = listener

  awaitClose {
    onFocusChangeListener = null
    setOnClickListener(null)
  }

}


@ExperimentalCoroutinesApi
fun EditText.onTyped() = callbackFlow<String> {
  val watcher = object : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable?) {
      s?.let { offer(StringBuilder(it).toString()) }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
  }
  addTextChangedListener(watcher)
  awaitClose { removeTextChangedListener(watcher) }
}
