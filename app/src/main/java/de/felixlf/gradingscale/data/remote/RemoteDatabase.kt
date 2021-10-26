package de.felixlf.gradingscale.data.remote

import de.felixlf.gradingscale.model.firebase.CountriesAndGradeScales
import de.felixlf.gradingscale.model.firebase.GradeScaleRemote
import kotlinx.coroutines.flow.Flow

interface RemoteDatabase {
  suspend fun getCountriesAndGrades(): List<CountriesAndGradeScales>
  suspend fun getGradescaleWithName(name: String): GradeScaleRemote

  fun countriesAndGradesFlow(): Flow<List<CountriesAndGradeScales>>
  fun gradescaleWithNameFlow(name: String): Flow<GradeScaleRemote>
}