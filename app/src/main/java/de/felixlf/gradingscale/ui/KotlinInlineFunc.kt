package de.felixlf.gradingscale.ui

import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import com.google.android.material.textfield.TextInputLayout

//Comfort functions
fun Double.round(decimals: Int = 4): Double {
  var multiplier = 1.0
  repeat(decimals) { multiplier *= 10 }
  return kotlin.math.round(this * multiplier) / multiplier
}


fun Spinner.setListNames(listInSpinner: List<String>) {
  if (adapter == null) {
    adapter = ArrayAdapter(this.context, android.R.layout.simple_spinner_dropdown_item, listInSpinner)
    return
  }

  (adapter as ArrayAdapter<String>).apply {
    clear()
    addAll(listInSpinner)

  }
}

fun TextInputLayout.setListNames(listInSpinner: List<String>) {
  (this.editText as? AutoCompleteTextView)?.setAdapter(
    ArrayAdapter(this.context, de.felixlf.gradingscale.R.layout.list_item, listInSpinner)
  )
}


