package de.felixlf.gradingscale.ui.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.felixlf.gradingscale.data.flowdatasources.FlowDataSource
import de.felixlf.gradingscale.data.flowdatasources.GSFragFlows
import de.felixlf.gradingscale.data.repository.GiSRepository
import de.felixlf.gradingscale.model.Grade
import de.felixlf.gradingscale.model.GradeScale
import de.felixlf.gradingscale.model.GradesInScale
import de.felixlf.gradingscale.utils.updateMutableLD
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class GSViewModel @Inject constructor(
  private val repository: GiSRepository,
  val dataSource: FlowDataSource,
) : ViewModel() {

  private lateinit var recentlyDeletedGrade: Grade

  private val _instructionsLD = MutableLiveData<OperationState>()
  val instructionsLD: LiveData<OperationState> get() = _instructionsLD

  val gradeScalesNamesLDF = MutableLiveData<List<String>>()
  val selectedGradeListItemsLDF = MutableLiveData<List<GradeListItem>>()
  val currentGradeLDF = MutableLiveData<Grade>()

  lateinit var gradeScalesNames: StateFlow<List<String>>
  lateinit var currentGradeInScale: StateFlow<GradesInScale>
  private lateinit var selectedGrade: StateFlow<Grade>
  private val selectGradeScaleWithName = MutableStateFlow("")
  private val setTotalPoints = MutableStateFlow(100.0)
  private val selectGradeInPos = MutableStateFlow(-1)
  private val gsFragFlows =
    GSFragFlows(dataSource, selectGradeScaleWithName, setTotalPoints, selectGradeInPos)

  init {
    viewModelScope.launch {
      gradeScalesNames = dataSource.flowGradeScaleNames().stateIn(this)
      selectedGrade = gsFragFlows.getSelectedGrade().stateIn(this)
      selectGradeScaleWithName.value = gradeScalesNames.value[0]
      gradeScalesNames.updateMutableLD(gradeScalesNamesLDF, this)
      currentGradeInScale = gsFragFlows.selectedGradeInScaleFlow.stateIn(this)
      gsFragFlows.setGradeListItemStream().updateMutableLD(selectedGradeListItemsLDF, this)
      selectedGrade.updateMutableLD(currentGradeLDF, this)
    }
  }

  fun resetInstructions() = _instructionsLD.postValue(OperationState.OperationFinished)

  fun updateGrade(namedGrade: String, percentage: Double) {
    val updateGrade = currentGradeLDF.value?.copy(
      namedGrade = namedGrade,

      percentage = percentage
    )
    if (updateGrade != null) {
      viewModelScope.launch { repository.updateGrade(updateGrade) }
    }
  }

  fun addNewGradeFromCurrent(namedGrade: String, percentage: Double) {

    val newGrade = Grade().apply {
      this.namedGrade = namedGrade
      this.percentage = percentage
      currentGradeLDF.value?.let {
        this.nameOfScale = it.nameOfScale
        this.pointedGrade = it.pointedGrade
      }
    }
    viewModelScope.launch { repository.addGrade(newGrade) }

  }

  private fun removeGrade(grade: Grade) {
    recentlyDeletedGrade = grade
    viewModelScope.launch { repository.removeGrade(grade) }
  }

  fun removeCurrentGradeScale() { //Delete dialog
    selectGradeInPos.value = 1
    val gradeScaleName = selectedGrade.value.nameOfScale
    setGradeScaleInPos(0)
    viewModelScope.launch { repository.deleteGradesFromScales(gradeScaleName) }
    _instructionsLD.postValue(OperationState.LoadFirstGradeScale)
  }

  fun undoDelete() {  //Fragment
    viewModelScope.launch { repository.addGrade(recentlyDeletedGrade) }
  }

  fun gsSetTotalPoints(name: String) {  //GSFragment
    setTotalPoints.value = name.toDouble()
  }

  fun dialogEditGrade(pos: Int) {
    selectGradeInPos.value = pos
    _instructionsLD.postValue(OperationState.EditGrade)
  }

  fun addNewGradeScale(name: String) {
    val gradeScale = GradeScale(gradeScaleName = name)
    viewModelScope.launch { repository.addGradeScale(gradeScale) }
    val grade = Grade(namedGrade = "New Name", nameOfScale = name, percentage = 0.5)
    viewModelScope.launch { repository.addGrade(grade) }
    _instructionsLD.postValue(OperationState.LoadLastGradeScale)
  }

  fun updateGradeScale(newName: String) {
    val currentGradeScale = currentGradeInScale.value.gradeScale
    currentGradeScale.gradeScaleName = newName
    viewModelScope.launch { repository.updateGradeScale(currentGradeScale) }
    _instructionsLD.postValue(OperationState.UpdateCurrentGradeScaleName(newName))

  }

  fun dialogNewGrade() {
    val nameOfScale = selectGradeScaleWithName.value
    currentGradeLDF.value = Grade(nameOfScale = nameOfScale)
    _instructionsLD.postValue(OperationState.AddNewGrade)
  }

  fun dialogNewGradeScale() = _instructionsLD.postValue(OperationState.AddNewGradeScale)
  fun dialogModifyGradeScale() = _instructionsLD.postValue(OperationState.UpdateGradeScale)
  fun dialogDeleteGradeScale() = _instructionsLD.postValue(OperationState.DeleteGradeScale)
  fun indicateDialogOpened() = _instructionsLD.postValue(OperationState.Opened)

  fun setGradeToDelete(pos: Int) {
    selectGradeInPos.value = pos
    val gradeToDelete = selectedGrade.value
    removeGrade(gradeToDelete)
    _instructionsLD.postValue(OperationState.DeleteGrade)
  }

  fun setGradeScaleInPos(position: Int) {
    gradeScalesNamesLDF.value?.let { gradeScaleList ->
      if (gradeScaleList.size >= position && position >= 0) {
        selectGradeScaleWithName.value = gradeScaleList[position]
      }
    }
  }

  fun setGradeScaleWithName(name: String) {
    gradeScalesNamesLDF.value?.let { gradeScaleList ->
      if (gradeScaleList.contains(name)) {
        selectGradeScaleWithName.value = gradeScaleList.first { it == name }
      }
    }
  }


}
