package de.felixlf.gradingscale.data.repository

import de.felixlf.gradingscale.data.flowdatasources.FlowDataSource
import de.felixlf.gradingscale.utils.MockScale
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class FlowDataSourceTest {


  lateinit var mockScale: MockScale
  lateinit var fo: FlowDataSource
  lateinit var repository: GiSRepository
  lateinit var testDispatcher: CoroutineDispatcher
  lateinit var coroutineScope: CoroutineScope


  @Before
  fun setup() {
    mockScale = MockScale("Mini")
    repository = FakeRepository(mockScale)
    testDispatcher = TestCoroutineDispatcher()
    coroutineScope = TestCoroutineScope(testDispatcher)
    fo = FlowDataSource(repository)


  }

  @After
  fun clear() {
    coroutineScope.coroutineContext.cancel()
    Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
  }


  @Test
  fun flowGradeScaleSelection_returnsAFlowOfNamesAfterUpdates() = runBlockingTest {

    val getScaleNameFlow = fo.flowGradeScaleNames()
    assertEquals(
      getScaleNameFlow.first(),
      (mockScale.gradesInScalesList.map { it.gradeScale.gradeScaleName })
    )
    mockScale.gradesInScale2.gradeScale.gradeScaleName = "Mini 3"
    assertEquals(
      getScaleNameFlow.first(),
      (mockScale.gradesInScalesList.map { it.gradeScale.gradeScaleName })
    )

  }


  @Test
  fun flowGradeInScaleSelection_returnsAFlowOfGradesInScale_withSelectedName() = runBlockingTest {

    val selectedGradeScaleFlow = MutableStateFlow("2 Mini")
    val selectedGradeInScale = fo.flowGradeInScaleSelection(selectedGradeScaleFlow)
    assertEquals(
      selectedGradeInScale.first(),
      (mockScale.gradesInScalesList.first { it.gradeScale.gradeScaleName == selectedGradeScaleFlow.value })
    )

//    selectedGradeScaleFlow.value = "NO_SCALE"
//    assertEquals(
//      selectedGradeInScale.first(),
//      (mockScale.gradesInScalesList[0])
//    )

  }


}










