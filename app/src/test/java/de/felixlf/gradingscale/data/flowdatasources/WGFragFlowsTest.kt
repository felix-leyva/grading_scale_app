package de.felixlf.gradingscale.data.flowdatasources

import de.felixlf.gradingscale.data.repository.*
import de.felixlf.gradingscale.ui.weightedcalculator.model.WGListItem
import de.felixlf.gradingscale.utils.MockScale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual.equalTo
import org.hamcrest.core.IsInstanceOf.instanceOf
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class WGFragFlowsTest {
  lateinit var giSRepository: GiSRepository
  lateinit var gradeWCalculationsRepository: GradeWCalculationsRepository
  lateinit var flowDataSource: FlowDataSource
  lateinit var wGFragFlows: WGFragFlows
  lateinit var mockScale: MockScale
  lateinit var mockWGrades : MockWGrades

  @Before
  fun setup() {

    mockScale = MockScale()
    mockWGrades = MockWGrades()
    giSRepository = FakeRepository(mockScale)
    flowDataSource = FlowDataSource(giSRepository)
    gradeWCalculationsRepository = FakeWGRepository(mockWGrades)
    wGFragFlows = WGFragFlows(flowDataSource, gradeWCalculationsRepository)
  }

  @Test
  fun selectedGradeInScaleFlow_returnsAFlowWithAGiS()= runBlockingTest{
    val selectedGradeScale: MutableStateFlow<String> = MutableStateFlow("Mini")
    val flowOfGiS = wGFragFlows.selectedGradeInScaleFlow(selectedGradeScale)
    var giS = flowOfGiS.first()
    assertThat(giS, equalTo(mockScale.gradesInScale))

    selectedGradeScale.value = "2 Mini"
    giS = flowOfGiS.first()
    assertThat(giS, equalTo(mockScale.gradesInScale2))
  }

  @Test
  fun selectedCalculation_ReturnsAFlowOfTheSelectedCalculation() = runBlockingTest {
    val selectedWGrade = MutableStateFlow("Test2")
    val flowOfWGrades = wGFragFlows.selectedCalculation(selectedWGrade)
    var wGradesCalc = flowOfWGrades.first()
    assertThat(wGradesCalc, equalTo(mockWGrades.wCalculationsAndGrades[1]))

    selectedWGrade.value = "Failed"
    wGradesCalc = flowOfWGrades.first()
    assertThat(wGradesCalc, equalTo(mockWGrades.wCalculationsAndGrades[0]))
  }

  @Test
  fun gradeInScaleNamesFlow_returnsListOfNames() = runBlockingTest {
    val flowOfGiSNames = wGFragFlows.gradeInScaleNamesFlow()
    var listofNames = flowOfGiSNames.first()
    assertThat(listofNames, equalTo(mockScale.gradeScaleList.map { it.gradeScaleName }))

  }

  @Test
  fun calculationsNamesFlow_returnsListOfNamesOfWGradesCalculations() = runBlockingTest {
    val flowOfNames = wGFragFlows.calculationsNamesFlow()
    var listofNames = flowOfNames.first()
    assertThat(listofNames, equalTo(mockWGrades.gWcalculations.map { it.name }))

  }

  @Test
  fun getWGListItemFlow_returnsWGListItems() = runBlockingTest {
    val selectedWCalc = MutableStateFlow("Test1")
    val flowOfWCalc = wGFragFlows.selectedCalculation(selectedWCalc)
    val selectedGradeScale: MutableStateFlow<String> = MutableStateFlow("2 Mini")
    val flowOfGiS = wGFragFlows.selectedGradeInScaleFlow(selectedGradeScale)

    val wGListFlow = wGFragFlows.getWGListItemFlow(flowOfWCalc, flowOfGiS)
    val WCalc = flowOfWCalc.first()
    val gIS = flowOfGiS.first()

    val list: MutableList<WGListItem> = WCalc.weightedGrades.map {
      WGListItem.WGItem(
        gradeName = gIS.gradeByPercentage(it.percentage).namedGrade,
        percentage = it.percentage * 100,
        totalPoints = it.weight,
        points = it.percentage * it.weight,
        id = it.id.toString()
      )
    }.toMutableList()
    list.add(0, WGListItem.WGHeaderViewHolder)

    val wGList = wGListFlow.first()

    assertThat(wGList[0], instanceOf(WGListItem.WGHeaderViewHolder::class.java))
    var index = 0
    list.forEach {
      assertThat(wGList[index], equalTo(it))
      index ++
    }

  }


  @Test
  fun getWGListItemFlow_updatesListWhenDataChanged() = runBlockingTest {
    val selectedWCalc = MutableStateFlow("Test1")
    val flowOfWCalc = wGFragFlows.selectedCalculation(selectedWCalc)
    val selectedGradeScale: MutableStateFlow<String> = MutableStateFlow("2 Mini")
    val flowOfGiS = wGFragFlows.selectedGradeInScaleFlow(selectedGradeScale)

    val wGListFlow = wGFragFlows.getWGListItemFlow(flowOfWCalc, flowOfGiS)
    val wCalc = flowOfWCalc.first()
    val gIS = flowOfGiS.first()

    val list: MutableList<WGListItem> = wCalc.weightedGrades.map {
      WGListItem.WGItem(
        gradeName = gIS.gradeByPercentage(it.percentage).namedGrade,
        percentage = it.percentage * 100,
        totalPoints = it.weight,
        points = it.percentage*it.weight,
        id = it.id.toString()
      )
    }.toMutableList()
    list.add(0, WGListItem.WGHeaderViewHolder)

    var wGList = wGListFlow.first()

    assertThat(wGList[0], instanceOf(WGListItem.WGHeaderViewHolder::class.java))
    var index = 0
    list.forEach {
      assertThat(wGList[index], equalTo(it))
      index ++
    }

    val gradeToUpdate = wCalc.weightedGrades[1].copy(weight = 1000.0)

    gradeWCalculationsRepository.updateWeightedGrade(gradeToUpdate)

    wGList = wGListFlow.first()
    val wGModified = wGList[2] as WGListItem.WGItem
    assertThat(wGModified.totalPoints, equalTo(1000.0))

  }


  @Test
  fun getWGradeFromID_returnsTheWGrade() = runBlockingTest {
    val selectedWCalc = MutableStateFlow("Test1")
    val flowOfWCalc = wGFragFlows.selectedCalculation(selectedWCalc)
    val wGrades = mockWGrades.weightedGrades1
    val selectedId = MutableStateFlow("")
    val wGradeToGetFlow = wGFragFlows.getWGradeFromID(flowOfWCalc, selectedId)

    for( pos in 0 until wGrades.size){
      selectedId.value = wGrades[pos].id.toString()
      val wGradeToGet = wGradeToGetFlow.first()
      //println(wGradeToGet)
      assertThat(wGradeToGet, equalTo(wGrades[pos]))

    }
  }

  @Test
  fun getTotalWeightedGrade_returnsTheTotalWlistItem() = runBlockingTest {
    val selectedWCalc = MutableStateFlow("Test1")
    val flowOfWCalc = wGFragFlows.selectedCalculation(selectedWCalc)
    val selectedGradeScale: MutableStateFlow<String> = MutableStateFlow("2 Mini")
    val flowOfGiS = wGFragFlows.selectedGradeInScaleFlow(selectedGradeScale)

    val flowOfTotalGrade = wGFragFlows.getTotalWeightedGrade(flowOfWCalc, flowOfGiS)
    val totalGrade = flowOfTotalGrade.first()

    flowOfWCalc.first().weightedGrades.forEach { println(it) }
    println(totalGrade)


  }



}