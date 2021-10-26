package de.felixlf.gradingscale.ui.weightedcalculator

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import de.felixlf.gradingscale.TestCoroutineRule
import de.felixlf.gradingscale.data.flowdatasources.FlowDataSource
import de.felixlf.gradingscale.data.flowdatasources.WGFragFlows
import de.felixlf.gradingscale.data.repository.FakeRepository
import de.felixlf.gradingscale.data.repository.FakeWGRepository
import de.felixlf.gradingscale.data.repository.GiSRepository
import de.felixlf.gradingscale.data.repository.MockWGrades
import de.felixlf.gradingscale.ui.weightedcalculator.model.WGListItem
import de.felixlf.gradingscale.utils.MockScale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

//https://blog.mindorks.com/unit-testing-viewmodel-with-kotlin-coroutines-and-livedata

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class WeightedGradeCalcViewModelTest {

  @get:Rule
  val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

  @get:Rule
  val testCoroutineRule = TestCoroutineRule()

  lateinit var giSRepository: GiSRepository
  lateinit var gradeWCalculationsRepository: FakeWGRepository
  lateinit var flowDataSource: FlowDataSource
  lateinit var wGFragFlows: WGFragFlows
  lateinit var mockScale: MockScale
  lateinit var mockWGrades: MockWGrades
  lateinit var vM: WeightedGradeCalcViewModel

  @Before
  fun setup() {
    mockScale = MockScale()
    mockWGrades = MockWGrades()
    giSRepository = FakeRepository(mockScale)
    flowDataSource = FlowDataSource(giSRepository)
    gradeWCalculationsRepository = FakeWGRepository(mockWGrades)
    wGFragFlows = WGFragFlows(flowDataSource, gradeWCalculationsRepository)
    vM = WeightedGradeCalcViewModel(wGFragFlows, gradeWCalculationsRepository)
  }


  @Test
  fun gradeScalesNamesLD_returnsListOfGradesNames() = testCoroutineRule.runBlockingTest {
    val names = vM.gradeScalesNamesLD.value
    assertThat(names, equalTo(mockScale.gradeScaleList.map { it.gradeScaleName }))
  }

  @Test
  fun wCalculationNamesLD_returnsListOfCalculationsNames() = testCoroutineRule.runBlockingTest {
    val names = vM.wCalculationNamesLD.value
    assertThat(names, equalTo(mockWGrades.gWcalculations.map { it.name }))
  }

  @Test
  fun selectGradeScaleWithName_returnsAGiSWithTheSameName() = testCoroutineRule.runBlockingTest {

    vM.selectGradeScaleWithName("Mini")
    var giS = vM.currentGradeInScaleLD.value
    assertThat(giS, equalTo(mockScale.gradesInScale))

    vM.selectGradeScaleWithName("2 Mini")
    giS = vM.currentGradeInScaleLD.value
    assertThat(giS, equalTo(mockScale.gradesInScale2))

  }

  @Test
  fun selectWeightedCalculationWithName_returnsACalculationWithTheSameName() =
    testCoroutineRule.runBlockingTest {

      vM.selectWeightedCalculationWithName("Test2")
      var calc = vM.currentWCalculationGradeLD.value
      assertThat(calc, equalTo(mockWGrades.wCalculationsAndGrades[1]))

      vM.selectWeightedCalculationWithName("Test3")
      calc = vM.currentWCalculationGradeLD.value
      assertThat(calc, equalTo(mockWGrades.wCalculationsAndGrades[2]))

      vM.selectWeightedCalculationWithName("Test1")
      calc = vM.currentWCalculationGradeLD.value
      assertThat(calc, equalTo(mockWGrades.wCalculationsAndGrades[0]))

    }

  @Test
  fun deleteWeightedGrade_deletesTheWeightedGrade() = runBlockingTest {
    val previousSize = mockWGrades.wCalculationsAndGrades[0].weightedGrades.size
    val wGradeToDelete = mockWGrades.wCalculationsAndGrades[0].weightedGrades[1]
    vM.deleteWeightedGrade(wGradeToDelete)
    assertThat(
      mockWGrades.wCalculationsAndGrades[0].weightedGrades.size,
      (equalTo(previousSize - 1))
    )

  }

  @Test
  fun updateWeightedGrade_updatesTheWeightedGrade() = runBlockingTest {
    val gradeToModify = mockWGrades.wCalculationsAndGrades[0].weightedGrades[1].copy(weight = 99.0)
    vM.updateWeightedGrade(gradeToModify)
    val currentGrade = gradeWCalculationsRepository.mW.wCalculationsAndGrades[0].weightedGrades[1]
    assertThat(currentGrade, (equalTo(gradeToModify)))
  }


  @Test
  fun insertWeightedGradeInCurrentCalculation_insertsTheWeightedGrade() = runBlockingTest {
    val previousSize = mockWGrades.wCalculationsAndGrades[0].weightedGrades.size
    vM.insertWeightedGradeInCurrentCalculation(.99, 99.0)
    assertThat(
      mockWGrades.wCalculationsAndGrades[0].weightedGrades.size,
      (equalTo(previousSize + 1))
    )
  }

  @Test
  fun getWGListItemFlow_returnsAWGList() = runBlockingTest {
    val wgListItemLD = vM.wGListItemLD
    vM.selectWeightedCalculationWithName("Test2")
    vM.selectGradeScaleWithName("Mini")

    val gradesInMockScale = mockWGrades.wCalculationsAndGrades[1].weightedGrades
    for (index: Int in 1..gradesInMockScale.size)
      assertThat(
        (wgListItemLD.value?.get(index) as WGListItem.WGItem).percentage,
        equalTo(gradesInMockScale[index - 1].percentage * 100)
      )

  }

  @Test
  fun getWGListItemFlow_returnsUpdatedList()    {
    val wgListItemLD = vM.wGListItemLD
    vM.selectWeightedCalculationWithName("Test2")
    vM.selectGradeScaleWithName("Mini")

    val gradesInMockScale = mockWGrades.wCalculationsAndGrades[1].weightedGrades

    for (index: Int in 1..gradesInMockScale.size){
      assertThat(
        (wgListItemLD.value?.get(index) as WGListItem.WGItem).percentage,
        equalTo(gradesInMockScale[index - 1].percentage * 100)
      )
    }

    val gradeToModify = gradesInMockScale[2].copy(weight = 1000.0)
    vM.updateWeightedGrade(gradeToModify)
    vM.selectGradeScaleWithName("2 Mini")


    println(vM.currentWCalculationGrade.value.weightedGrades[2])
    assertThat(
      (wgListItemLD.value?.get(3) as WGListItem.WGItem).totalPoints, equalTo(1000.0)
    )

  }


  @Test
  fun getWeightedGradeWithId_returnsFirstWGradeOrOneWithCorrespondingID() {
    val gradesInMockScale = mockWGrades.wCalculationsAndGrades[0].weightedGrades

    for (pos in 0 until gradesInMockScale.size){
      val id = gradesInMockScale[pos].id.toString()
      vM.setWeightedGradeWithId(id)
      assertThat(
        vM.currentWeightedGradeLD.value,
        equalTo(gradesInMockScale[pos])
      )
    }

  }

  @Test
  fun resultWeightedGradeLD_returnsUpdatedList()  {
    vM.selectWeightedCalculationWithName("Test2")
    vM.selectGradeScaleWithName("Mini")

    val gradesInMockScale = mockWGrades.wCalculationsAndGrades[1].weightedGrades
    val totalGradeItem = vM.resultWeightedGradeLD.value


    gradesInMockScale.forEach { println(it) }
    println(totalGradeItem)
  }
}