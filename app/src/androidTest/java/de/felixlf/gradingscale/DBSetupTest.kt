package de.felixlf.gradingscale

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.felixlf.gradingscale.data.database.GradeDatabase
import de.felixlf.gradingscale.data.repository.GiSRepository
import de.felixlf.gradingscale.model.GradeScale
import org.junit.Test
import org.junit.runner.RunWith

//private const val TAG = "TestGSVM"

@RunWith(AndroidJUnit4::class)
class DBSetupTest {
    lateinit var instrumentationContext: Context
    lateinit var giSRepImpl: GiSRepository
    lateinit var database: GradeDatabase
    val gradeScale =
      GradeScale(gradeScaleName = "Mini")


//    @ExperimentalCoroutinesApi
//    @Before
//    fun setup() {
//        instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext
//        database = Room.databaseBuilder(
//            instrumentationContext,
//            GradeDatabase::class.java,
//            "grade_in_scale_database"
//        ).fallbackToDestructiveMigration().build()
//
//        runBlockingTest {
//            giSRepImpl = GiSRepositoryImpl(gradesInScaleDao = database.gradesInScaleDao(),
//                coroutineContext = this.coroutineContext
//            )
//
//        }
//
//    }

  @Test
  fun empty(){
    println()
    assert(true)
  }

//    @ExperimentalCoroutinesApi
//    @Test
//    fun addGradeScaleInDB() = runBlockingTest{
//        giSRepImpl.addGradeScale(
//            gradeScale
//        )
//    }
//
//
//    @ExperimentalCoroutinesApi
//    @Test
//    fun addGradesInDB() = runBlockingTest {
//
//        MockScale.minGrades.forEach {
//            it.nameOfScale = gradeScale.gradeScaleName
//            giSRepImpl.addGrade(it)
//        }
//    }
//

}