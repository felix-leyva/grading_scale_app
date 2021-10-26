package de.felixlf.gradingscale.data.flowdatasources

import de.felixlf.gradingscale.data.repository.GiSRepository
import de.felixlf.gradingscale.data.repository.RemoteSyncRepository
import de.felixlf.gradingscale.model.GradeScale
import de.felixlf.gradingscale.model.converters.generateGradeInScaleFromRemoteGradeScale
import de.felixlf.gradingscale.model.firebase.GradeScaleRemote
import de.felixlf.gradingscale.ui.importremote.recyclerview.ImportRemoteListItem
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class ImportDBDataSources @Inject constructor(
  val remoteSyncRepository: RemoteSyncRepository,
  private val giSRepository: GiSRepository,
) {

  suspend fun flowOfListOfScales() =
    remoteSyncRepository.countriesAndGradesFlow().mapLatest { countriesGrades ->
      countriesGrades.map { countryAndGrades ->
        val countryName = countryAndGrades.countryName
        return@map countryAndGrades.gradeScalesNames.map { "${countryName}_${it}" }
      }.flatten()
    }


  suspend fun getListOfScales(): List<String> {
    return remoteSyncRepository.getCountriesAndGrades().map { countriesGrades ->
      val countryName = countriesGrades.countryName
      countriesGrades.gradeScalesNames.map { "${countryName}_${it}" }
    }.flatten()
  }

  suspend fun filteredListOfScales(selectedCountry: Flow<String>) = flowOfListOfScales()
    .combine(selectedCountry) { listScales, country ->
      if (country.isNotBlank()) {
        listScales.filter { it.substringBefore("_") == country }
      } else {
        listScales
      }
    }


  suspend fun filteredImportRemoteListItems(selectedCountry: Flow<String>) =
    remoteSyncRepository.countriesAndGradesFlow()
      .combineTransform(selectedCountry) { listScales, country ->
        val filteredListScales = if (country.isNotBlank()) {
          listScales.filter { it.countryName == country }
        } else {
          listScales
        }

        val listOfItemsForRecyclerView: MutableList<ImportRemoteListItem> =
          filteredListScales.map { countriesGrades ->
            val countryName = countriesGrades.countryName
            countriesGrades.gradeScalesNames.map { scaleName ->
              ImportRemoteListItem.ImportRemoteItem(
                country = countryName,
                nameScale = scaleName
              )
            }
          }.flatten().toMutableList()

        listOfItemsForRecyclerView.add(0, ImportRemoteListItem.ImportRemoteHeader)
        emit(listOfItemsForRecyclerView)
      }


  suspend fun getListOfCountries(): Flow<List<String>> {
    return remoteSyncRepository.countriesAndGradesFlow().map { countriesAndGradeScales ->
      countriesAndGradeScales.map { it.countryName }
    }

  }

  suspend fun getRemoteGradeScaleWithName(name: String): GradeScaleRemote {
    return remoteSyncRepository.getGradescaleWithName(name)
  }

  suspend fun remoteGradeScaleWithNameFlow(name: String): Flow<GradeScaleRemote> {
    return remoteSyncRepository.gradescaleWithNameFlow(name)
  }


  suspend fun importIntoDbRemoteGradeScale(gradeScaleRemote: GradeScaleRemote) {
    val nameOfScaleOnly =
      returnValidName("${gradeScaleRemote.country}_${gradeScaleRemote.gradeScaleName}")
    val gradeRemote = gradeScaleRemote.copy(gradeScaleName = nameOfScaleOnly)
    val giS = generateGradeInScaleFromRemoteGradeScale(gradeRemote)
    giSRepository.addGradeScale(giS.gradeScale)
    giS.grades.forEach { giSRepository.addGrade(it) }

  }


  suspend fun returnValidName(name: String): String {
    val gradesInScales = giSRepository.getGradeOfScales()
    val nameOfScaleOnly = if (isNameDuplicated(gradesInScales, name)) {
      findUntilNoRepeated(gradesInScales, name)
    } else {
      name
    }
    return nameOfScaleOnly.substringAfter("_")
  }

  fun isNameDuplicated(gradesInScales: List<GradeScale?>, name: String): Boolean {
    val gradeInScaleLocal =
      gradesInScales.firstOrNull { it?.gradeScaleName == name }
    return gradeInScaleLocal != null
  }

  fun findUntilNoRepeated(gradesInScales: List<GradeScale?>, name: String): String {
    for (i in 1 until 100) {
      val gradeRemote = name.substringBeforeLast("(")
      val newName = "${gradeRemote}($i)"
      if (!isNameDuplicated(gradesInScales, newName)) {
        return newName
      }
    }
    return name
  }


}