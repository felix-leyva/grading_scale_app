package de.felixlf.gradingscale.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import de.felixlf.gradingscale.data.database.GradeCalculationDao
import de.felixlf.gradingscale.data.database.GradeDatabase
import de.felixlf.gradingscale.model.GWCalculation
import de.felixlf.gradingscale.model.WCalculationsAndGrades
import de.felixlf.gradingscale.model.WeightedGrade
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.hamcrest.core.IsEqual.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.IOException

@RunWith(JUnit4::class)
class GradeWCalculationsRepositoryImplTest {
  private lateinit var dao: GradeCalculationDao
  private lateinit var db: GradeDatabase
  private lateinit var repository: GradeWCalculationsRepository

  lateinit var testDispatcher: CoroutineDispatcher
  lateinit var coroutineScope: CoroutineScope

  @ExperimentalCoroutinesApi
  @Before
  fun createDB() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(
      context,
      GradeDatabase::class.java
    ).build()
    dao = db.gradeCalculationDao()

    testDispatcher = TestCoroutineDispatcher()
    coroutineScope = TestCoroutineScope(testDispatcher)

    repository = GradeWCalculationsRepositoryImpl(dao, Dispatchers.Default)
  }

  @After
  @Throws(IOException::class)
  fun closeDb() {
    db.close()
  }

  @Test
  fun getCalculationFlow_ifEmpty_addsANewCalculation() = runBlocking {
    val flow = repository.getCalculationsFlow()
    val list = flow.first()
    assertThat(list.size, `is`(1))
  }

  @Test
  fun getCalculationFlow_ifNonEmpty_ReturnsContent() = runBlocking {
    val calculation = GWCalculation("Test")
    val wGrade = WeightedGrade(0.9, 1.0, calculation.id.toString())
    val wCalculationsAndGrades = WCalculationsAndGrades(calculation, mutableListOf(wGrade))
    repository.insertCalculation(calculation)
    repository.insertWeightedGrade(wGrade)

    val flow = repository.getCalculationsFlow()
    val list = flow.first()
    assertThat(list[0], `is`(wCalculationsAndGrades))
  }

  @Test
  fun getCalculationFlow_emptyAddsCalculation_ThenAddASecond() = runBlocking {
    val flow = repository.getCalculationsFlow()
    var list = flow.first()
    assertThat(list.size, `is`(1))

    val calculation = GWCalculation("Test")
    val wGrade = WeightedGrade(0.9, 1.0, calculation.id.toString())
    val wCalculationsAndGrades = WCalculationsAndGrades(calculation, mutableListOf(wGrade))
    repository.insertCalculation(calculation)
    repository.insertWeightedGrade(wGrade)
    list = flow.first()
    assertThat(list[1], equalTo(wCalculationsAndGrades))
  }

  @ExperimentalCoroutinesApi
  @Test
  fun deleteAllCalculations_generatesDefault() = runBlocking {

    val calculation = GWCalculation("Test")
    val wGrade = WeightedGrade(0.9, 1.0, calculation.id.toString())
    val wGrade2 = WeightedGrade(0.12, 2.0, calculation.id.toString())
    val wCalculationsAndGrades = WCalculationsAndGrades(calculation, mutableListOf(wGrade))
    repository.insertCalculation(calculation)
    repository.insertWeightedGrade(wGrade)
    repository.insertWeightedGrade(wGrade2)

    val flow = repository.getCalculationsFlow()

    assertThat(flow.first()[0].weightedGrades.size, equalTo(2))
    repository.deleteCalculation(flow.first()[0].gradeCalculation)
    assertThat(flow.first()[0].weightedGrades.size, equalTo(1))
    assertThat(flow.first()[0].weightedGrades[0].scaleId, equalTo(flow.first()[0].gradeCalculation.id.toString()))
    assertThat(flow.first()[0].gradeCalculation.name, equalTo("Default"))

  }


}