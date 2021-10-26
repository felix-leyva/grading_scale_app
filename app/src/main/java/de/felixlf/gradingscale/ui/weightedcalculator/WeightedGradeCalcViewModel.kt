package de.felixlf.gradingscale.ui.weightedcalculator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.felixlf.gradingscale.data.flowdatasources.WGFragFlows
import de.felixlf.gradingscale.data.repository.GradeWCalculationsRepository
import de.felixlf.gradingscale.model.GradesInScale
import de.felixlf.gradingscale.model.WCalculationsAndGrades
import de.felixlf.gradingscale.model.WeightedGrade
import de.felixlf.gradingscale.ui.weightedcalculator.model.WGListItem
import de.felixlf.gradingscale.utils.updateMutableLD
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeightedGradeCalcViewModel @Inject constructor(
  private val wgFragFlows: WGFragFlows,
  private val gradeWCalculationsRepository: GradeWCalculationsRepository,
) : ViewModel() {

  private lateinit var gradeScalesNames: StateFlow<List<String>>
  private val _gradeScalesNamesLD = MutableLiveData<List<String>>(listOf())
  val gradeScalesNamesLD: LiveData<List<String>> get() = _gradeScalesNamesLD

  private val selectedGradeScaleWithName = MutableStateFlow("")
  private lateinit var currentGradeInScale: SharedFlow<GradesInScale>
  private val _currentGradeInScaleLD = MutableLiveData<GradesInScale>()
  val currentGradeInScaleLD: LiveData<GradesInScale> get() = _currentGradeInScaleLD

  private lateinit var wCalculationsNames: StateFlow<List<String>>
  private val _wCalculationNamesLD = MutableLiveData<List<String>>()
  val wCalculationNamesLD: LiveData<List<String>> get() = _wCalculationNamesLD

  private val selectedWCalculationAndGradesWithId = MutableStateFlow("")
  lateinit var currentWCalculationGrade: StateFlow<WCalculationsAndGrades>
  private val _currentWCalculationGradeLD = MutableLiveData<WCalculationsAndGrades>()
  val currentWCalculationGradeLD: LiveData<WCalculationsAndGrades> get() = _currentWCalculationGradeLD

  private val selectedWGradeId = MutableStateFlow("")
  private lateinit var currentWeightedGrade: StateFlow<WeightedGrade>
  private val _currentWeightedGradeLD = MutableLiveData<WeightedGrade>()
  val currentWeightedGradeLD: LiveData<WeightedGrade> get() = _currentWeightedGradeLD

  private lateinit var wgListItems: StateFlow<List<WGListItem>>
  private val _wGListItemLD = MutableLiveData<List<WGListItem>>()
  val wGListItemLD: LiveData<List<WGListItem>> get() = _wGListItemLD

  private lateinit var resultWeightedGrade: StateFlow<WGListItem.WGItem>
  private val _resultWeightedGradeLD = MutableLiveData<WGListItem.WGItem>()
  val resultWeightedGradeLD: LiveData<WGListItem.WGItem> get() = _resultWeightedGradeLD

  init {
    viewModelScope.launch {

      gradeScalesNames = wgFragFlows.gradeInScaleNamesFlow().stateIn(this)

      selectedGradeScaleWithName.value = gradeScalesNames.value[0]

      currentGradeInScale = wgFragFlows.selectedGradeInScaleFlow(selectedGradeScaleWithName)
        .shareIn(this, SharingStarted.WhileSubscribed(), 1)

      wCalculationsNames = wgFragFlows.calculationsNamesFlow().stateIn(this)

      selectedWCalculationAndGradesWithId.value = wCalculationsNames.value[0]

      //Sharedflow to avoid slow collectors by fast updates: https://youtu.be/RoGAb0iWljg?t=1582
      val currentWCalculationShF =
        wgFragFlows.selectedCalculation(selectedWCalculationAndGradesWithId)
          .shareIn(this, SharingStarted.WhileSubscribed(), 1)

      currentWCalculationGrade = currentWCalculationShF.stateIn(this)

      wgListItems = wgFragFlows.getWGListItemFlow(currentWCalculationShF, currentGradeInScale)
        .stateIn(this)

      currentWeightedGrade = wgFragFlows.getWGradeFromID(currentWCalculationShF, selectedWGradeId)
        .stateIn(this)

      resultWeightedGrade =
        wgFragFlows.getTotalWeightedGrade(currentWCalculationShF, currentGradeInScale)
          .stateIn(this)

      gradeScalesNames.updateMutableLD(_gradeScalesNamesLD, this)
      currentGradeInScale.updateMutableLD(_currentGradeInScaleLD, this)
      wCalculationsNames.updateMutableLD(_wCalculationNamesLD, this)
      currentWCalculationGrade.updateMutableLD(_currentWCalculationGradeLD, this)
      wgListItems.updateMutableLD(_wGListItemLD, this)
      currentWeightedGrade.updateMutableLD(_currentWeightedGradeLD, this)
      resultWeightedGrade.updateMutableLD(_resultWeightedGradeLD, this)

    }


  }


  fun selectGradeScaleWithName(nameOfGradeScale: String) {
    if (gradeScalesNames.value.contains(nameOfGradeScale))
      selectedGradeScaleWithName.value = nameOfGradeScale
  }

  fun selectWeightedCalculationWithName(nameOfWeightedCalculation: String) {
    if (wCalculationsNames.value.contains(nameOfWeightedCalculation))
      selectedWCalculationAndGradesWithId.value = nameOfWeightedCalculation
  }

  fun setWeightedGradeWithId(id: String) = viewModelScope.launch {
    selectedWGradeId.value = id
  }

  fun deleteWeightedGrade(weightedGrade: WeightedGrade) = viewModelScope.launch {
    if (currentWCalculationGrade.value.weightedGrades.contains(weightedGrade))
      gradeWCalculationsRepository.deleteWeightedGrade(weightedGrade)
  }

  fun updateWeightedGrade(weightedGrade: WeightedGrade) {
    viewModelScope.launch {
      gradeWCalculationsRepository.updateWeightedGrade(weightedGrade)
    }
  }

  fun insertWeightedGradeInCurrentCalculation(percentage: Double, weight: Double) =
    viewModelScope.launch {
      val id = currentWCalculationGrade.value.gradeCalculation.id.toString()
      val weightedGrade = WCalculationsAndGrades.checkedGrade(WeightedGrade(percentage, weight, id))
      gradeWCalculationsRepository.insertWeightedGrade(weightedGrade)
    }

}




































