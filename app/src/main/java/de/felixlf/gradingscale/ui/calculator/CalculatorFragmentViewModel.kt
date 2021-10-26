package de.felixlf.gradingscale.ui.calculator

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import de.felixlf.gradingscale.data.flowdatasources.FlowDataSource
import de.felixlf.gradingscale.model.Grade
import de.felixlf.gradingscale.model.GradesInScale
import de.felixlf.gradingscale.utils.checkPoints
import de.felixlf.gradingscale.utils.checkTotalPoints
import de.felixlf.gradingscale.utils.combine
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
@FlowPreview
class CalculatorFragmentViewModel @Inject constructor(
  private val dataSource: FlowDataSource,
) : ViewModel() {

  lateinit var gradeScalesNamesLD: LiveData<List<String>>
  lateinit var selectedGradeInScaleLD: LiveData<GradesInScale>
  lateinit var currentGradeAndScale: LiveData<Pair<Grade, GradesInScale>>

  lateinit var percentageLD: LiveData<Double>
  lateinit var points: LiveData<Double>
  private val _totalPointsLD = MutableLiveData<Double>(100.0)
  val totalPointsLD: LiveData<Double> get() = _totalPointsLD

  private val selectedGradeScale = MutableStateFlow("Mini")
  private val gradeNamePosSF = MutableSharedFlow<Int>()
  private val percentageSF = MutableSharedFlow<String>()


  init {
    viewModelScope.launch {
      gradeScalesNamesLD = dataSource.flowGradeScaleNames().asLiveData()
      val selectedGradeInScaleFlow = dataSource.flowGradeInScaleSelection(selectedGradeScale)
      selectedGradeInScaleLD = selectedGradeInScaleFlow.distinctUntilChanged().asLiveData()

      val calcFragFlows = CalcFragFlows(
        selectedGradeInScaleFlow = selectedGradeInScaleFlow,
        gradeNamePosSF = gradeNamePosSF,
        percentageSF = percentageSF
      )

      currentGradeAndScale = calcFragFlows.setCurrentGradeAndScaleFromFlowOps().asLiveData()

      percentageLD = currentGradeAndScale.map { it.first.percentage }

      points =
        Transformations.map(percentageLD.combine(totalPointsLD)) { (percentage, totalpoints) ->
          return@map (percentage ?: 1.0) * (totalpoints ?: 100.0)
        }
    }
  }

  fun setGradeScaleInPos(name: String) {
    gradeScalesNamesLD.value?.let { gradeScaleList ->
      if (gradeScaleList.contains(name)) {
        selectedGradeScale.value = gradeScaleList.first { it == name }
      }
    }
  }

  fun setGradeByName(name: String) {
    val pos = selectedGradeInScaleLD.value?.grades?.indexOfFirst { it.namedGrade == name } ?: -1
    if (pos != -1) viewModelScope.launch { gradeNamePosSF.emit(pos) }
  }

  fun setGradeByPoints(points: String) {
    if (points.isBlank()) return
    val totalPoints = _totalPointsLD.value ?: 100.0
    val checkedPoints = checkPoints(points, totalPoints)
    val percentage = (checkedPoints / totalPoints) * 100

    viewModelScope.launch { percentageSF.emit(percentage.toString()) }
  }

  fun setGradeByPercentage(percentage: String) {
    if (percentage.isBlank()) return
    viewModelScope.launch { percentageSF.emit(percentage) }
  }

  fun setGradeByTotalPoints(totalPoints: String) {
    if (totalPoints.isBlank()) return
    _totalPointsLD.value = checkTotalPoints(totalPoints)
  }

}

