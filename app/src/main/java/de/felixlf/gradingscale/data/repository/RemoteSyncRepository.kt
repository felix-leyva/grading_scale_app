package de.felixlf.gradingscale.data.repository

import de.felixlf.gradingscale.model.firebase.CountriesAndGradeScales
import de.felixlf.gradingscale.model.firebase.GradeScaleRemote
import kotlinx.coroutines.flow.Flow

interface RemoteSyncRepository {
  suspend fun getCountriesAndGrades(): List<CountriesAndGradeScales>
  suspend fun getGradescaleWithName(name: String): GradeScaleRemote
  suspend fun importGradeScaleIntoLocalDb(remoteGradeScale: GradeScaleRemote): Boolean

  suspend fun countriesAndGradesFlow(): Flow<List<CountriesAndGradeScales>>
  suspend fun gradescaleWithNameFlow(name: String): Flow<GradeScaleRemote>

}