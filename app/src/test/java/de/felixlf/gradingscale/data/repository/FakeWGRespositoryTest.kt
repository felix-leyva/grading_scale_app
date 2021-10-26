package de.felixlf.gradingscale.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNot
import org.junit.Before
import org.junit.Test

class FakeWGRespositoryTest {

  lateinit var gradeWCalculationsRepository: FakeWGRepository
  lateinit var mW: MockWGrades

  @Before
  fun setup() {
    mW = MockWGrades()
    gradeWCalculationsRepository = FakeWGRepository(mW)
  }


  @Test
  fun testFindLocation() {
    val testIndexCalculation = 1
    val testIndexGrade = 3
    val wGrade = mW.wCalculationsAndGrades[testIndexCalculation].weightedGrades[testIndexGrade]
    val (indexCalculation, indexGrade) = gradeWCalculationsRepository.findIndex(wGrade)

    MatcherAssert.assertThat(indexCalculation, IsEqual.equalTo(testIndexCalculation))
    MatcherAssert.assertThat(indexGrade, IsEqual.equalTo(testIndexGrade))
  }

  @Test
  fun testFindLocationCalc() {
    val testIndexCalculation = 2
    val wCalc = mW.wCalculationsAndGrades[testIndexCalculation].gradeCalculation
    val indexCalculation = gradeWCalculationsRepository.findIndexCalc(wCalc)

    MatcherAssert.assertThat(indexCalculation, IsEqual.equalTo(testIndexCalculation))
  }

  @ExperimentalCoroutinesApi
  @Test
  fun updatedWeightedGrade_doesUpdates() = runBlockingTest {
    val testIndexCalculation = 1
    val testIndexGrade = 2
    val wGrade =
      mW.wCalculationsAndGrades[testIndexCalculation].weightedGrades[testIndexGrade].copy()
    wGrade.weight = 99.0
    gradeWCalculationsRepository.updateWeightedGrade(wGrade)
    MatcherAssert.assertThat(
      mW.wCalculationsAndGrades[testIndexCalculation].weightedGrades[testIndexGrade],
      Is.`is`(wGrade)
    )
  }

  @ExperimentalCoroutinesApi
  @Test
  fun getCalculationsFlow_updatesWhenModify() = runBlockingTest {
    val testIndexCalculation = 1
    val testIndexGrade = 3
    val wGrade =
      mW.wCalculationsAndGrades[testIndexCalculation].weightedGrades[testIndexGrade].copy()
    val flow = gradeWCalculationsRepository.getCalculationsFlow()

    var currentGrade = flow.first()[testIndexCalculation].weightedGrades[testIndexGrade]
    MatcherAssert.assertThat(currentGrade, IsEqual.equalTo(wGrade))

    wGrade.weight = 99.0
    gradeWCalculationsRepository.updateWeightedGrade(wGrade)
    currentGrade = flow.first()[testIndexCalculation].weightedGrades[testIndexGrade]
    MatcherAssert.assertThat(currentGrade, IsEqual.equalTo(wGrade))

  }

  @ExperimentalCoroutinesApi
  @Test
  fun getCalculationsFlow_updatesWhenDelete() = runBlockingTest {
    val testIndexCalculation = 1
    val testIndexGrade = 3
    val wGrade =
      mW.wCalculationsAndGrades[testIndexCalculation].weightedGrades[testIndexGrade].copy()
    val flow = gradeWCalculationsRepository.getCalculationsFlow()
    val (indexCalculation, indexWeight) = gradeWCalculationsRepository.findIndex(wGrade)
    val size = mW.wCalculationsAndGrades[indexCalculation].weightedGrades.size

    var currentGradesFromFlow = flow.first()[indexCalculation].weightedGrades
    MatcherAssert.assertThat(currentGradesFromFlow.size, Is.`is`(size))

    gradeWCalculationsRepository.deleteWeightedGrade(wGrade)
    currentGradesFromFlow = flow.first()[indexCalculation].weightedGrades
    MatcherAssert.assertThat(currentGradesFromFlow.size, IsNot.not(size))

  }

}