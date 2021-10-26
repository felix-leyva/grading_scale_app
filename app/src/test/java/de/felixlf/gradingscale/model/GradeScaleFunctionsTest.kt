package de.felixlf.gradingscale.model

import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class GradeScaleFunctionsTest {

  val gradeScaleName1 = "testGS1"
  val gradeScaleName2 = "testGS2"
  val gradeScaleName3 = "testGS3"


  val gS1 = GradeScale(gradeScaleName = gradeScaleName1)
  val gS2 = GradeScale(gradeScaleName = gradeScaleName2)
  val gS3 = GradeScale(gradeScaleName = gradeScaleName3)


  val grades1 = mutableListOf(
    Grade("A", 1.0, 50.0, "1", gradeScaleName1),
    Grade("B", .90, 45.0, "2", gradeScaleName1),
    Grade("C", .80, 40.0, "3", gradeScaleName1),
    Grade("D", .70, 35.0, "4", gradeScaleName1),
    Grade("E", .60, 30.0, "5", gradeScaleName1),
    Grade("F", .50, 25.0, "6", gradeScaleName1)

  )

  val grades1Names = grades1.map { it.namedGrade }
  val grades1Names2 = grades1.map { it.namedGrade2 }

  val grades2 = mutableListOf(
    Grade("A2", 1.00, 50.0, "10", gradeScaleName2),
    Grade("B2", .90, 45.0, "9", gradeScaleName2),
    Grade("C2", .80, 40.0, "8", gradeScaleName2),
    Grade("D2", .70, 35.0, "7", gradeScaleName2),
    Grade("E2", .60, 30.0, "6", gradeScaleName2),
    Grade("F2", .50, 25.0, nameOfScale = gradeScaleName2),
    Grade("A-", .95, 49.5, "9.5", gradeScaleName2)
  )
  val grades2Names = grades2.map { it.namedGrade }
  val grades2Names2 = grades2.map { it.namedGrade2 }
  val grades2Sorted = grades2.sortedByDescending { it.percentage }

  val grades3 = mutableListOf(
    Grade("Fail", .10, 50.0, "5", gradeScaleName2),
    Grade("B2", .90, 45.0, "9", gradeScaleName2),
    Grade("C2", .80, 40.0, "8", gradeScaleName2),
    Grade("D2", .70, 35.0, "7", gradeScaleName2),
    Grade("E2", .60, 30.0, "6", gradeScaleName2),
    Grade("F2", .50, 25.0, nameOfScale = gradeScaleName2),
    Grade("Top", .950, 49.5, "Top2", gradeScaleName2)
  )


  private lateinit var giS1: GradesInScale
  private lateinit var giS2: GradesInScale
  private lateinit var giS3: GradesInScale


  @Before
  fun setup() {
    giS1 = GradesInScale(gS1, grades1)
    giS2 = GradesInScale(gS2, grades2)
    giS3 = GradesInScale(gS3, grades3)
  }

  @Test
  fun gradesNameList_returnsDescendingListOfNamesOfGrades() {
    assertEquals(grades1Names, giS1.gradesNamesList())
    assertNotEquals(grades2Names, giS2.gradesNamesList())
    assertEquals(giS2.gradesNamesList(), grades2Sorted.map { it.namedGrade })
  }

  @Test
  fun gradesNameList_ofEmptyGradesList_returnsEmptyListOfString() {
    val emptyGradesInScale = GradesInScale(gS1, mutableListOf<Grade>())
    assertEquals(emptyGradesInScale.gradesNamesList(), mutableListOf<String>())
  }

  @Test
  fun gradesName2List_returnsDescendingListOfNames2() {
    assertEquals(grades1Names2, giS1.grades2NamesList())
    assertNotEquals(grades2Names, giS2.grades2NamesList())
    assertEquals(giS2.grades2NamesList(), listOf<String>("10", "9.5", "9", "8", "7", "6"))
  }

  @Test
  fun gradesName2List_ofEmptyGradesList_returnsEmptyListOfString() {
    val emptyGradesInScale = GradesInScale(gS1, mutableListOf<Grade>())
    assertEquals(emptyGradesInScale.grades2NamesList(), mutableListOf<String>())
  }

  @Test
  fun getPercentage_returnsPercentageOfTheGradeWithTheCorrespondingName() {
    assertTrue(giS1.getPercentage("C") == .80)
    assertTrue(giS2.getPercentage("D2") == .70)
  }

  @Test
  fun getPercentage_returns0ifGradeDoesNotExists() {
    assertTrue(giS1.getPercentage("C1") == 0.0)
    assertTrue(giS2.getPercentage("D") == 0.0)
  }

  @Test
  fun getGradeNamePos_returnsThePosOfTheOrderedList() {
    val listOfGradesToCheck: MutableList<Grade> =
      giS2.grades.sortedByDescending { it.percentage } as MutableList<Grade>

    grades2Sorted.forEach { grade ->
      val pos = giS2.getGradeNamePos(grade.percentage)
      assertTrue(listOfGradesToCheck[pos] == grade)
    }

  }

  @Test
  fun getGradeNamePos_ifPercentageInTheMiddleReturnsCorrectPos() {
    var pos = giS2.getGradeNamePos(.910)
    assertTrue(pos == 2)

    pos = giS2.getGradeNamePos(.890)
    assertTrue(pos == 3)
  }

  @Test
  fun getGradeNamePos_withTooHighValueOrTooLowReturnsTheFirstOrLastElementOfList() {
    var pos = giS2.getGradeNamePos(1.022)
    assertThat(pos, `is`(0))

    pos = giS2.getGradeNamePos(.230)
    assertThat(pos, `is`(giS2.grades.size - 1))

    pos = giS2.getGradeNamePos(0.0)
    assertThat(pos, `is`(giS2.grades.size - 1))

    pos = giS2.getGradeNamePos(-5.0)
    assertThat(pos, `is`(giS2.grades.size - 1))
  }

  @Test
  fun getGradeName2Pos_ifPercentageInTheMiddleReturnsCorrectPos() {
    var pos = giS2.getGrade2NamePos(.910)
    assertTrue(pos == 2)

    pos = giS2.getGrade2NamePos(.890)
    assertTrue(pos == 3)
  }


  @Test
  fun getGradeName2Pos_withTooHighValueOrTooLowReturnsTheFirstOrLastGradedElementOfList() {
    var pos = giS2.getGrade2NamePos(1.022)
    assertThat(pos, `is`(0))

    pos = giS2.getGrade2NamePos(.230)
    assertThat(pos, `is`(5))

    pos = giS2.getGrade2NamePos(0.0)
    assertThat(pos, `is`(5))

    pos = giS2.getGrade2NamePos(-5.0)
    assertThat(pos, `is`(5))
  }

  @Test
  fun getPointsPercentage_returnsTheCalculationOfPercentageTimesThePointsInTheGradeInScale() {
    var totalPoints = 100.0
    assertThat(giS1.getPoints(.95, totalPoints), `is`(95.0))
    totalPoints = 50.0
    assertThat(giS1.getPoints(.15, totalPoints), `is`(.15 * 50.0))
  }

  @Test
  fun getPointsPercentage_returnsZeroOrMaxPoints_IfPercentageHigherThan1OrLowerThan0() {
    var totalPoints = 100.0
    assertThat(giS1.getPoints(1.2, totalPoints), `is`(100.0))
    assertThat(giS1.getPoints(-1.2, totalPoints), `is`(0.0))
    totalPoints = 50.0
    assertThat(giS1.getPoints(1.2, totalPoints), `is`(50.0))
    assertThat(giS1.getPoints(-1.2, totalPoints), `is`(0.0))

  }

  @Test
  fun getPointsName_returnsTheCalculationOfPercentageTimesThePointsInTheGradeInScale() {
    var totalPoints = 100.0
    assertThat(giS1.getPoints("C", totalPoints), `is`(80.0))
    totalPoints = 50.0
    assertThat(giS1.getPoints("C", totalPoints), `is`(0.8 * 50.0))
    assertThat(giS1.getPoints("False", totalPoints), `is`(0.0))
  }

  @Test
  fun getPointsFromGrade_returnsThePointsBasedInThePercentageOfTheGrade() {
    var totalPoints = 100.0
    assertThat(giS1.getPoints(grades1[1], totalPoints), `is`(grades1[1].percentage * totalPoints))
    assertThat(giS2.getPoints(grades2[0], totalPoints), `is`(grades2[0].percentage * totalPoints))
    assertThat(giS1.getPoints(grades1[0], totalPoints), `is`(grades1[0].percentage * totalPoints))
  }

  @Test
  fun gradeByName_returnsTheGradeWithTheNameOrTheLastGradeIfNameDoesNotExists() {
    giS1.grades.forEach { grade ->
      val gradeName = grade.namedGrade
      assertEquals(grade, giS1.gradeByName(gradeName))
    }
    assertEquals(giS3.gradeByName("Something").namedGrade, "Fail")
  }

  @Test
  fun grade2ByName_returnsTheGradeWithTheNameOrTheLastGradeIfNameDoesNotExists() {
    giS1.grades.forEach { grade ->
      val grade2Name = grade.namedGrade2
      assertEquals(grade, giS1.grade2ByName(grade2Name))
    }
    assertEquals(giS3.grade2ByName("Something").namedGrade2, "5")
  }

  @Test
  fun gradeByPercentage_returnsTheGradeBasedInPercentage_TheHighestIfPercentageHigher1OrLastIfPercentageLower0(){
    assertThat(giS1.gradeByPercentage(0.54).namedGrade, `is`("F"))
    assertThat(giS1.gradeByPercentage(0.76).namedGrade, `is`("D"))
    assertThat(giS1.gradeByPercentage(0.87).namedGrade, `is`("C"))
    assertThat(giS2.gradeByPercentage(0.66).namedGrade, `is`("E2"))
    assertThat(giS2.gradeByPercentage(0.54).namedGrade, `is`("F2"))
    assertThat(giS3.gradeByPercentage(0.154).namedGrade, `is`("Fail"))
    assertThat(giS3.gradeByPercentage(0.08).namedGrade, `is`("Fail"))
    assertThat(giS3.gradeByPercentage(0.83).namedGrade, `is`("C2"))

  }

  @Test
  fun gradeByPoints_returnsGradeByPointsAndUpdatesPoints(){
    var totalPoints = 100.0
    assertThat(giS1.gradeByPoints(54.0, totalPoints).namedGrade, `is`("F"))
    assertThat(giS1.gradeByPoints(76.0, totalPoints).namedGrade, `is`("D"))

    assertThat(giS1.gradeByPoints(-5.0, totalPoints).namedGrade, `is`("F"))

    assertThat(giS1.gradeByPoints(150.0, totalPoints).namedGrade, `is`("A"))

    assertThat(giS3.gradeByPoints(54.0, totalPoints).namedGrade, `is`("F2"))
    assertThat(giS3.gradeByPoints(76.0, totalPoints).namedGrade, `is`("D2"))

    assertThat(giS3.gradeByPoints(-5.0, totalPoints).namedGrade, `is`("Fail"))

    assertThat(giS3.gradeByPoints(150.0, totalPoints).namedGrade, `is`("Top"))
  }




}