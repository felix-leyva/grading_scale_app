package de.felixlf.gradingscale

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    fun testInVariable(mut1: MutableLiveData<Int>, int2: Int) {
        mut1.postValue(int2 * 2)
    }

    @Test
    fun checkChangeInVariable(){
        val mut1 = MutableLiveData<Int>()
        testInVariable(mut1, 2)
        assertEquals(4, mut1.value)
    }


}
