package de.felixlf.gradingscale.data.remote

import de.felixlf.gradingscale.model.firebase.CountriesAndGradeScales
import de.felixlf.gradingscale.model.firebase.GradeScaleRemote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRemoteDatabase: RemoteDatabase {

  val countriesAndGradeScales = FakeRemoteDbData.generateFakeGradesAndCountries()
  val gradeScaleRemote = FakeRemoteDbData.generateFakeGradeScaleList()

  override suspend fun getCountriesAndGrades(): List<CountriesAndGradeScales> {
    return countriesAndGradeScales
  }

  override suspend fun getGradescaleWithName(name: String): GradeScaleRemote {
    val country = name.substringBefore("_")
    val scaleName = name.substringAfter("_")
    return gradeScaleRemote.firstOrNull {
      it.country == country && it.gradeScaleName == scaleName
    } ?: GradeScaleRemote()
  }

  override fun countriesAndGradesFlow(): Flow<List<CountriesAndGradeScales>> {
    return flow { emit(countriesAndGradeScales) }
  }

  override fun gradescaleWithNameFlow(name: String): Flow<GradeScaleRemote> {
    return flow { emit(getGradescaleWithName(name))}
  }
}