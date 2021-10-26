package de.felixlf.gradingscale.utils

import junit.framework.Assert.assertNull
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Test
import java.util.*

class EditTextDecimalKtTest{

  @Test
  fun testParsingStringWithComma(){
    Locale.setDefault(Locale.GERMANY)
    val test = "23,3"
    val double = test.getDouble()
    assertThat(double, `is`(23.3))
  }

  @Test
  fun testParsingStringWithPoint(){
    Locale.setDefault(Locale.US)
    val test = "23.3"
    val double = test.getDouble()
    assertThat(double, `is`(23.3))
  }


  @Test
  fun testParsingStringWithPointWrong_IgnoresDecimalSymbol(){
    Locale.setDefault(Locale.GERMANY)
    val test = "23.3"
    val double = test.getDouble()
    assertThat(double, `is`(233.0))

  }

  @Test
  fun testParsingStringWithCommaWrong_IgnoresDecimalSymbol(){
    Locale.setDefault(Locale.US)
    val test = "23,3"
    val double = test.getDouble()
    assertThat(double, `is`(233.0))
  }

  @Test
  fun testParsingStringWithWrongInput_ReturnsNull(){
    Locale.setDefault(Locale.US)
    val test = "a23"
    assertNull(test.getDouble())

    Locale.setDefault(Locale.GERMANY)
    assertNull(test.getDouble())

  }

  @Test
  fun testFormatingDoubleWithLocaleGermany_returnsStringWithComma() {
    Locale.setDefault(Locale.GERMANY)
    var testDouble = 23.523
    var text = testDouble.setLocString()
    assertThat(text, `is`("23,523"))

    testDouble = 23.0
    text = testDouble.setLocString()
    assertThat(text, `is`("23"))

  }


  @Test
  fun testFormatingDoubleWithLocaleUS_returnsStringWithPoint() {
    Locale.setDefault(Locale.US)
    var testDouble = 23.523
    var text = testDouble.setLocString()
    assertThat(text, `is`("23.523"))

    testDouble = 23.0
    text = testDouble.setLocString()
    assertThat(text, `is`("23"))
  }


}