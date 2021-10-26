package de.felixlf.gradingscale.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import de.felixlf.gradingscale.data.database.GradeCalculationDao
import de.felixlf.gradingscale.data.database.GradeDatabase
import de.felixlf.gradingscale.model.GWCalculation
import de.felixlf.gradingscale.model.WCalculationsAndGrades
import de.felixlf.gradingscale.model.WeightedGrade
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class WCalculationsAndGradesRepositoryImplTest {
  private lateinit var dao: GradeCalculationDao
  private lateinit var db: GradeDatabase

  @Before
  fun createDB() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(
      context,
      GradeDatabase::class.java
    ).build()
    dao = db.gradeCalculationDao()
  }

  @After
  @Throws(IOException::class)
  fun closeDb() {
    db.close()
  }

  @Test
  @Throws(Exception::class)
  fun testWriteAndGet() = runBlocking {

    val calculation = GWCalculation("test")
    dao.insertCalculation(calculation)
    dao.insertCalculation(GWCalculation("test2"))

    val id = calculation.id.toString()

    val wGrades = listOf(
      WeightedGrade(.9, 2.0, id),
      WeightedGrade(.4, 2.0, id),
      WeightedGrade(.7, 1.0, id),
      WeightedGrade(.8, 1.0, id),
      WeightedGrade(.3, 2.0, id)
    ).map { WCalculationsAndGrades.checkedGrade(it) }

    wGrades.forEach { dao.insertWeightedGrade(it) }

    val flowWeightedGrade = dao.getCalculationsFlow()
    val listGradeWCalculations = flowWeightedGrade.first()

    assertEquals(listGradeWCalculations[0].gradeCalculation, calculation)
    assertEquals(listGradeWCalculations[0].weightedGrades, wGrades)

  }

  @Test
  @Throws(Exception::class)
  fun testWriteNewAndGetUpdated() = runBlocking {

    val calculation = GWCalculation("test")
    dao.insertCalculation(calculation)
    dao.insertCalculation(GWCalculation("test2"))

    val id = calculation.id.toString()

    val wGrades = listOf(
      WeightedGrade(.9, 2.0, id),
      WeightedGrade(.4, 2.0, id),
      WeightedGrade(.7, 1.0, id),
      WeightedGrade(.8, 1.0, id),
      WeightedGrade(.3, 2.0, id)
    ).map { WCalculationsAndGrades.checkedGrade(it) }.toMutableList()

    wGrades.forEach { dao.insertWeightedGrade(it) }

    val flowWeightedGrade = dao.getCalculationsFlow()
    val listGradeWCalculations = flowWeightedGrade.first()

    assertEquals(listGradeWCalculations[0].gradeCalculation, calculation)
    assertEquals(listGradeWCalculations[0].weightedGrades, wGrades)

    val newGrade = WeightedGrade(.99, 5.0, id)
    dao.insertWeightedGrade(newGrade)
    val updatedFlow = flowWeightedGrade.first()
    wGrades.add(newGrade)
    assertEquals(updatedFlow[0].gradeCalculation, calculation)
    assertEquals(updatedFlow[0].weightedGrades, wGrades)


  }


  @Test
  @Throws(Exception::class)
  fun testUpdateAndCheck() = runBlocking {

    val calculation = GWCalculation("test")
    dao.insertCalculation(calculation)
    dao.insertCalculation(GWCalculation("test2"))

    val id = calculation.id.toString()

    val wGrades = listOf(
      WeightedGrade(.9, 2.0, id),
      WeightedGrade(.4, 2.0, id),
      WeightedGrade(.7, 1.0, id),
      WeightedGrade(.8, 1.0, id),
      WeightedGrade(.3, 2.0, id)
    ).map { WCalculationsAndGrades.checkedGrade(it) }.toMutableList()

    wGrades.forEach { dao.insertWeightedGrade(it) }

    val flowWeightedGrade = dao.getCalculationsFlow()
    val listGradeWCalculations = flowWeightedGrade.first()

    assertEquals(listGradeWCalculations[0].gradeCalculation, calculation)
    assertEquals(listGradeWCalculations[0].weightedGrades, wGrades)

    wGrades[4].percentage = .99
    val updatedGrade = wGrades[4]
    dao.updateWeightedGrade(updatedGrade)
    val updatedFlow = flowWeightedGrade.first()
    assertEquals(updatedFlow[0].gradeCalculation, calculation)
    assertEquals(updatedFlow[0].weightedGrades, wGrades)
    assertNotEquals(listGradeWCalculations[0].weightedGrades, wGrades)

  }

  @Test
  @Throws(Exception::class)
  fun testDeleteAndCheck() = runBlocking {

    val calculation = GWCalculation("test")
    dao.insertCalculation(calculation)
    dao.insertCalculation(GWCalculation("test2"))

    val id = calculation.id.toString()

    val wGrades = listOf(
      WeightedGrade(.9, 2.0, id),
      WeightedGrade(.4, 2.0, id),
      WeightedGrade(.7, 1.0, id),
      WeightedGrade(.8, 1.0, id),
      WeightedGrade(.3, 2.0, id)
    ).map { WCalculationsAndGrades.checkedGrade(it) }.toMutableList()

    wGrades.forEach { dao.insertWeightedGrade(it) }

    val flowWeightedGrade = dao.getCalculationsFlow()
    val listGradeWCalculations = flowWeightedGrade.first()

    assertEquals(listGradeWCalculations[0].gradeCalculation, calculation)
    assertEquals(listGradeWCalculations[0].weightedGrades, wGrades)

    val gradeToDelete = wGrades[4]
    dao.deleteWeightedGrade(gradeToDelete)
    wGrades.removeAt(4)

    val updatedFlow = flowWeightedGrade.first()
    assertEquals(updatedFlow[0].gradeCalculation, calculation)
    assertEquals(updatedFlow[0].weightedGrades, wGrades)
    assertNotEquals(listGradeWCalculations[0].weightedGrades, wGrades)

  }


}