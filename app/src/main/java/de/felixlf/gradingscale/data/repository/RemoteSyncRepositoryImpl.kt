package de.felixlf.gradingscale.data.repository

import de.felixlf.gradingscale.data.remote.RemoteDatabase
import de.felixlf.gradingscale.model.converters.generateGradeInScaleFromRemoteGradeScale
import de.felixlf.gradingscale.model.firebase.CountriesAndGradeScales
import de.felixlf.gradingscale.model.firebase.GradeScaleRemote
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoteSyncRepositoryImpl @Inject constructor(
  val remoteDatabase: RemoteDatabase,
  val giSRepository: GiSRepository,
) : RemoteSyncRepository {
  override suspend fun getCountriesAndGrades(): List<CountriesAndGradeScales> {
    return remoteDatabase.getCountriesAndGrades()
  }

  override suspend fun getGradescaleWithName(name: String): GradeScaleRemote {
    return remoteDatabase.getGradescaleWithName(name)
  }

  override suspend fun importGradeScaleIntoLocalDb(remoteGradeScale: GradeScaleRemote): Boolean {
    val giSToAdd = generateGradeInScaleFromRemoteGradeScale(remoteGradeScale)
    return try {
      giSRepository.addGradeScale(giSToAdd.gradeScale)
      giSToAdd.grades.forEach { giSRepository.addGrade(it) }
      true
    } catch (ex: Exception) {
      ex.printStackTrace()
      false
    }
  }

  override suspend fun countriesAndGradesFlow(): Flow<List<CountriesAndGradeScales>> {
    return remoteDatabase.countriesAndGradesFlow()
  }

  override suspend fun gradescaleWithNameFlow(name: String): Flow<GradeScaleRemote> {
    return remoteDatabase.gradescaleWithNameFlow(name)
  }


}