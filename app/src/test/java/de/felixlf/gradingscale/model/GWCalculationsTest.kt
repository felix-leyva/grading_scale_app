package de.felixlf.gradingscale.model

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class GWCalculationsTest {

  lateinit var WCalcAndGrades: WCalculationsAndGrades
  lateinit var id: String

  @Before
  fun setup() {
    WCalcAndGrades = WCalculationsAndGrades()
    id = WCalcAndGrades.gradeCalculation.id.toString()
  }

  @Test
  fun addWGrade_AddsAGradeToTheList_AddsCorrectedGrade() {
    WCalcAndGrades.addWeightedGrade(3.0, 2.0)
    assert(WCalcAndGrades.weightedGrades[0].equals(WeightedGrade(1.0, 2.0, id)))

    WCalcAndGrades.addWeightedGrade(.5, -2.0)
    assert(WCalcAndGrades.weightedGrades[1].equals(WeightedGrade(.5, 0.0, id)))

    WCalcAndGrades.addWeightedGrade(.5, 2.0)
    assert(WCalcAndGrades.weightedGrades[2].equals(WeightedGrade(.5, 2.0, id)))
  }

  @Test
  fun addListWGrades_AddsACorrectedListOfGrades() {
    val listOfPerAndWeight = listOf((3.0 to 2.0), (0.5 to -2.0), (0.5 to 2.0))
    WCalcAndGrades.addListOfWGrades(listOfPerAndWeight)
    assert(WCalcAndGrades.weightedGrades[0].equals(WeightedGrade(1.0, 2.0, id)))
    assert(WCalcAndGrades.weightedGrades[1].equals(WeightedGrade(.5, 0.0, id)))
    assert(WCalcAndGrades.weightedGrades[2].equals(WeightedGrade(.5, 2.0, id)))
  }

  @Test
  fun addListWGradesWithWGrades_AddsCorrectedListOfGrades() {
    val gradesAndWeights = listOf(
      WeightedGrade(3.0, 2.0, id),
      WeightedGrade(0.5, -2.0, id),
      WeightedGrade(0.5, 2.0, id)
    )

    WCalcAndGrades.addListOfWGrades(gradesAndWeights)

    assert(WCalcAndGrades.weightedGrades[0].equals(WeightedGrade(1.0, 2.0, id)))
    assert(WCalcAndGrades.weightedGrades[1].equals(WeightedGrade(.5, 0.0, id)))
    assert(WCalcAndGrades.weightedGrades[2].equals(WeightedGrade(.5, 2.0, id)))

  }

  @Test
  fun weightedGradesWithScale_returnsListOfGradesInScale() {
    lateinit var giS1: GradesInScale
    lateinit var giS2: GradesInScale
    GradeScaleFunctionsTest().apply {
      giS1 = GradesInScale(gS1, grades1)
      giS2 = GradesInScale(gS2, grades2)
    }

    val listOfPerAndWeight = listOf((.80 to 2.0), (0.85 to -2.0), (0.95 to 2.0), (0.65 to 2.0))
    WCalcAndGrades.addListOfWGrades(listOfPerAndWeight)

    val gradeList1 = WCalcAndGrades.weightedGradesWithScale(giS1)
    val gradeList2 = WCalcAndGrades.weightedGradesWithScale(giS2)

    assertThat(gradeList1[0], `is`(giS1.gradeByPercentage(0.8)))
    assertThat(gradeList2[1], `is`(giS2.gradeByPercentage(0.85)))
    assertThat(gradeList1.size, `is`(WCalcAndGrades.weightedGrades.size))
  }

  @Test
  fun removeGradeInPos_avoidsRemovingWrongPosition_removesCorrectPosition() {
    val listOfPerAndWeight = listOf((.80 to 2.0), (0.85 to -2.0), (0.95 to 2.0), (0.65 to 2.0))
    WCalcAndGrades.addListOfWGrades(listOfPerAndWeight)
    assert(WCalcAndGrades.weightedGrades[1].equals(WeightedGrade(.85, 0.0, id)))

    WCalcAndGrades.removeGradeInPos(1)
    assert(WCalcAndGrades.weightedGrades[1].equals(WeightedGrade(.95, 2.0, id)))

    WCalcAndGrades.removeGradeInPos(-1)
    assert(WCalcAndGrades.weightedGrades[2].equals(WeightedGrade(.65, 2.0, id)))

    WCalcAndGrades.removeGradeInPos(3)
    assert(WCalcAndGrades.weightedGrades[2].equals(WeightedGrade(.65, 2.0, id)))

  }

  @Test
  fun totalWeightedGrade_returnsAWeightedGradeOfTheTotalOfTheList() {
    val listOfPerAndWeight = listOf((.80 to 2.0), (0.85 to -2.0), (0.95 to 2.0), (0.65 to 2.0))
    WCalcAndGrades.addListOfWGrades(listOfPerAndWeight)

    var percentage = ((.80 * 2.0) + (0.85 * -0.0) + (0.95 * 2.0) + (0.65 * 2.0)) / (2+2+2)

    assertThat(WCalcAndGrades.totalWeightedGrade.percentage, `is`(percentage))
    assertThat(WCalcAndGrades.totalWeightedGrade.weight, `is`(6.0))

    WCalcAndGrades.addWeightedGrade(1.0, 6.0)
    percentage = ((.80 * 2.0) + (0.85 * -0.0) + (0.95 * 2.0) + (0.65 * 2.0) + (1.0 * 6.0)) / (2+2+2+6)
    assertThat(WCalcAndGrades.totalWeightedGrade.percentage, `is`(percentage))
    assertThat(WCalcAndGrades.totalWeightedGrade.weight, `is`(12.0))

    WCalcAndGrades.removeGradeInPos(0)
    percentage = ((0.85 * -0.0) + (0.95 * 2.0) + (0.65 * 2.0) + (1.0 * 6.0)) / (2+2+6)
    assertThat(WCalcAndGrades.totalWeightedGrade.percentage, `is`(percentage))
    assertThat(WCalcAndGrades.totalWeightedGrade.weight, `is`(10.0))

  }

}