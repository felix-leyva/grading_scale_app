package de.felixlf.gradingscale.utils

import android.os.Build
import android.text.InputFilter
import android.text.Spanned
import android.text.method.DigitsKeyListener
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.ParseException
import java.util.*
import java.util.regex.Pattern

class DecimalDigitsInputFilter(digitsBeforeSeparator: Int, digitsAfterSeparator: Int) :
  InputFilter {
  private val mPattern: Pattern

  override fun filter(
    source: CharSequence,
    start: Int,
    end: Int,
    dest: Spanned,
    dstart: Int,
    dend: Int,
  ): CharSequence? {
    val input =
      dest.toString().substring(0, dstart) + source.subSequence(start, end) + dest.toString()
        .substring(dend)
    val matcher = mPattern.matcher(input)
    return if (!matcher.matches()) {
      ""
    } else null
  }

  init {
    val b = "(-?\\d{1,$digitsBeforeSeparator})"
    val a = "(\\d{1,$digitsAfterSeparator})"
    val s = "[\\.\\,]"
    val numberRegex = StringBuilder()
      .append("(-)")
      .append("|")
      .append("($b$s$a)")
      .append("|")
      .append("($b$s)")
      .append("|")
      .append("($b)")
      .toString()
    mPattern = Pattern.compile(numberRegex)
  }
}


fun EditText.getDouble(): Double? {
  return text.toString().getDouble()
}

fun EditText.getDoubleOrZero(): Double {
  return text.toString().getDouble() ?: 0.0
}

fun String.getDouble(): Double? {
  val text = this
  return try {
    NumberFormat.getInstance().parse(text)?.toDouble()
  } catch (e: ParseException) {
    null
  }
}

fun EditText.setDouble(number: Double) {
  setText(number.setLocString())
}

fun Double.setLocString(): String {
  val double = this
  return try {

    val numberFormat = if (Build.MANUFACTURER.equals("samsung", ignoreCase = true)) {
      NumberFormat.getNumberInstance(Locale.US)
    } else {
      NumberFormat.getNumberInstance()
    }

    numberFormat.maximumFractionDigits = 3
    numberFormat.format(double)
  } catch (aEx: ArithmeticException) {
    aEx.printStackTrace()
    ""
  }
}


fun EditText.decimalLocale() {
  val separator = if (Build.MANUFACTURER.equals("samsung", ignoreCase = true)) {
    DecimalFormatSymbols.getInstance(Locale.US).decimalSeparator
  } else {
    DecimalFormatSymbols.getInstance().decimalSeparator
  }


  keyListener = DigitsKeyListener.getInstance("1234567890$separator")
  filters = arrayOf(DecimalDigitsInputFilter(3, 3))
  imeOptions = EditorInfo.IME_FLAG_NO_FULLSCREEN
}

fun String.nonValidDecimal(): Boolean {
  return this.endsWith(".") or this.endsWith(",")
}

fun Flow<String>.notEmptyNoCommas(): Flow<String> {
  return this.filter { it.isNotEmpty() }.map { it.noCommas() }
}

fun String.noCommas(): String {
  return if (isNotEmpty()) {
    replace(',', '.')
  } else {
    ""
  }
}
