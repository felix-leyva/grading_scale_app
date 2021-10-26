package de.felixlf.gradingscale.ui.calculator

import de.felixlf.gradingscale.data.flowdatasources.FlowDataSource
import de.felixlf.gradingscale.data.repository.FakeRepository
import de.felixlf.gradingscale.data.repository.GiSRepository
import de.felixlf.gradingscale.model.GradesInScale
import de.felixlf.gradingscale.utils.MockScale
import dev.olog.flow.test.observer.test
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


@ExperimentalCoroutinesApi
class CalcFragFlowsTest {


  lateinit var mockScale: MockScale
  lateinit var fo: FlowDataSource
  lateinit var repository: GiSRepository
  lateinit var testDispatcher: CoroutineDispatcher
  lateinit var coroutineScope: CoroutineScope

  lateinit var selectedGradeScaleFlow: MutableStateFlow<String>
  lateinit var selectedGradesInScale: Flow<GradesInScale>
  lateinit var flowOpCalcFrag: CalcFragFlows


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
  fun setGradeByName_returnsAFlowOfPairGradeToGIS_avoidsDuplicated_returnsDefault() = runBlocking {
    setupCalcFragFlows()

    //Given
    val gradeNamePos: SharedFlow<Int> = flow {
      emit(5)
      emit(5)
      emit(1)
    }.shareIn(coroutineScope, SharingStarted.WhileSubscribed(), 5)

    val gradeToGIS = flowOpCalcFrag.setGradeByName(gradeNamePos)

    assertEquals(
      gradeToGIS.first(),
      (mockScale.gradesInScalesList[0].grades[1] to mockScale.gradesInScalesList[0])
    )

    assertEquals(
      gradeToGIS.drop(1).first(),
      (mockScale.gradesInScalesList[0].grades[5] to mockScale.gradesInScalesList[0])
    )

  }


  @Test
  fun setGradeByName2_returnsAFlowOfPairGradeToGIS_avoidsDuplicated_returnsDefault() = runBlocking {
    setupCalcFragFlows()

    val gradeNamePos: SharedFlow<Int> = flow {
      emit(5)
      emit(5)
      emit(1)
    }.shareIn(coroutineScope, SharingStarted.WhileSubscribed(), 5)


    val gradeToGIS = flowOpCalcFrag.setGradeByName2(gradeNamePos)

    assertEquals(
      gradeToGIS.first(),
      (mockScale.gradesInScalesList[0].grades[1] to mockScale.gradesInScalesList[0])
    )

    assertEquals(
      gradeToGIS.drop(1).first(),
      (mockScale.gradesInScalesList[0].grades[5] to mockScale.gradesInScalesList[0])
    )

  }


  @Test
  fun setGradeByPoints_returnsAFlowPairOfGradeAndGradeInScale() = runBlocking {
    setupCalcFragFlows()
    val points = (0..500).random().toString()
    val pointsF: SharedFlow<String> = flow {
      emit(points)

    }.shareIn(coroutineScope, SharingStarted.WhileSubscribed(), 3)

//    val gradeToGIS = flowOpCalcFrag.setGradeByPoints(pointsF)

//    assertEquals(
//      gradeToGIS.first(),
//      (mockScale.gradesInScalesList[0].gradeByPoints(points.toDouble()) to mockScale.gradesInScalesList[0])
//    )

  }

  @Test
  fun setGradeByPercentage_returnsAFlowPairOfGradeAndGradeInScale_AndAdjustsPercentageOfOnlyTheReturnedGrade() =
    runBlocking {
      setupCalcFragFlows()
      val percentage = (0..100).random().toString()
      val percentageF: SharedFlow<String> = flow {
        emit(percentage)

      }.shareIn(coroutineScope, SharingStarted.WhileSubscribed(), 3)

      val gradeToGIS = flowOpCalcFrag.setGradeByPercentage(percentageF)

      assertEquals(
        gradeToGIS.first(),
        (mockScale.gradesInScalesList[0].gradeByPercentage(percentage.toDouble() / 100).copy(percentage = percentage.toDouble()/100) to mockScale.gradesInScalesList[0])
      )
    }

  @FlowPreview
  @Test
  fun setCurrentGradeAndScaleFromFlowOps_JoinsOtherFunctionsAndEmitsSingleFlow() = runBlockingTest {
    selectedGradeScaleFlow = MutableStateFlow("Mini")
    selectedGradesInScale = fo.flowGradeInScaleSelection(selectedGradeScaleFlow)

    val percentage = "50.0"
    val percentageF = MutableSharedFlow<String>(16, 0, BufferOverflow.DROP_OLDEST)
    percentageF.emit(percentage)

    val position = 1
    val positionF = MutableSharedFlow<Int>(16, 0, BufferOverflow.DROP_OLDEST)
    positionF.emit(position)


    flowOpCalcFrag =
      CalcFragFlows(selectedGradesInScale, percentageSF = percentageF, gradeNamePosSF = positionF)

    val gradeToGIS = flowOpCalcFrag.setCurrentGradeAndScaleFromFlowOps()


    gradeToGIS.test(this) {
      assertEquals(
        valueAt(1),
        (mockScale.gradesInScalesList[0].gradeByPercentage(percentage.toDouble() / 100) to mockScale.gradesInScalesList[0])
      )

      assertEquals(
        valueAt(0),
        (mockScale.gradesInScalesList[0].gradeByName(mockScale.gradesInScalesList[0].grades[position].namedGrade) to mockScale.gradesInScalesList[0])
      )


    }
  }

  private fun setupCalcFragFlows() {
    selectedGradeScaleFlow = MutableStateFlow("Mini")
    selectedGradesInScale = fo.flowGradeInScaleSelection(selectedGradeScaleFlow)
    flowOpCalcFrag = CalcFragFlows(selectedGradesInScale)


  }


}