package de.felixlf.gradingscale.ui.importremote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.felixlf.gradingscale.data.flowdatasources.ImportDBDataSources
import de.felixlf.gradingscale.di.IoDispatcher
import de.felixlf.gradingscale.di.MainDispatcher
import de.felixlf.gradingscale.model.firebase.GradeScaleRemote
import de.felixlf.gradingscale.ui.importremote.recyclerview.ImportRemoteListItem
import de.felixlf.gradingscale.utils.updateMutableLD
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class ImportRemoteViewModel @Inject constructor(
  val dataSource: ImportDBDataSources,
  @IoDispatcher val ioDispatcher: CoroutineDispatcher,
  @MainDispatcher val mainDispatcher: CoroutineDispatcher,
) : ViewModel() {

  lateinit var selectedGradeAndScale : StateFlow<GradeScaleRemote>
  val selectedGradeAndScaleName = MutableStateFlow("")

  private val selectedCountry = MutableStateFlow("")

  private var _listOfCountries = MutableLiveData<List<String>>()
  val listOfCountries: LiveData<List<String>> get() = _listOfCountries

  private val _listOfCountriesAndGradeScales = MutableLiveData<List<ImportRemoteListItem>>()
  val listOfCountriesAndGradeScales: LiveData<List<ImportRemoteListItem>> get() = _listOfCountriesAndGradeScales

  private val _snackbarMsg = MutableLiveData<String>()
  val snackBarMsg : LiveData<String> get() = _snackbarMsg

  init {
    viewModelScope.launch(ioDispatcher) {
      val listOfCountries = dataSource.getListOfCountries()

      val filteredListSharedFlow = dataSource.filteredImportRemoteListItems(selectedCountry)
        .shareIn(this, SharingStarted.WhileSubscribed(), 1)

      selectedGradeAndScale = selectedGradeAndScaleName.flatMapLatest { name ->
        dataSource.remoteGradeScaleWithNameFlow(name)
      }.stateIn(this)

      withContext(mainDispatcher) {
        listOfCountries.updateMutableLD(_listOfCountries, this)
        filteredListSharedFlow.updateMutableLD(_listOfCountriesAndGradeScales, this)
      }
    }
  }

  fun selectCountry(country: String) {
    selectedCountry.value = country
  }

  //In format "country_scaleName"
  fun selectGradeAndScale(gradeAndScale: String) = viewModelScope.launch {
    selectedGradeAndScaleName.value = gradeAndScale
  }

  fun importSelectedGradeAndScale(): Boolean{
    var success = false
    viewModelScope.launch {
      selectedGradeAndScale.value.let {
        if (it.grades.isNotEmpty()) {
          success = true
          dataSource.importIntoDbRemoteGradeScale(it)
        }
      }
    }
    return success
  }

  fun showSnackBar(message: String) {
    _snackbarMsg.value = message
  }

  fun hideSnackBar() {
    _snackbarMsg.value = ""
  }

}

