package de.felixlf.gradingscale

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import de.felixlf.gradingscale.data.repository.GiSRepositoryImpl
import de.felixlf.gradingscale.model.GradeScale
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

private const val TAG = "TestGSVM"

@RunWith(AndroidJUnit4::class)
class GradeScaleViewModelTest  {
    private lateinit var instrumentationContext: Context
    private lateinit var giSRepImpl: GiSRepositoryImpl
    val gradeScale =
      GradeScale(gradeScaleName = "Mini")


    @Before
    fun setup() {
        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
//        GiSRepositoryImpl.initialize(instrumentationContext)
//        giSRepImpl = GiSRepositoryImpl.get()

    }

    @Test
    fun empty(){

    }


//    @Test
//    fun getGrades() {
//        val grades = giSRep.getGrades()
//        Log.i(TAG, grades.value?.map { grade -> grade.toString() + "\n" }.toString())
//    }
//
//    @Test
//    fun getGradesScale() {
//        val gradeScales = giSRep.getGradeScales()
//        Log.i(TAG, gradeScales.getOrAwaitValue().map { gradeScale -> gradeScale.toString() + "\n" }.toString())
//    }
//
//    @Test
//    fun getGradesInScale() {
//        val gradeInScales = giSRep.getGradesFromScale()
//        Log.i(TAG, gradeInScales.getOrAwaitValue().map { gradeInScale -> gradeInScale.toString() + "\n" }.toString())
//    }
//
//
//    @Test
//    fun getGradesInScaleSpecific() {
//        val gradeInScales = giSRep.getGradesFromScale("Mini")
//        Log.i(TAG, gradeInScales.value?.map { gradeInScale -> gradeInScale.toString() + "\n" }.toString())
//    }


    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data = o
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }

        this.observeForever(observer)

        // Don't wait indefinitely if the LiveData is not set.
        if (!latch.await(time, timeUnit)) {
            throw TimeoutException("LiveData value was never set.")
        }

        @Suppress("UNCHECKED_CAST")
        return data as T
    }

}