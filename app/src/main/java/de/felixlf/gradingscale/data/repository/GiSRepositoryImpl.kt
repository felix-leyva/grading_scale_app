package de.felixlf.gradingscale.data.repository

import de.felixlf.gradingscale.data.database.GradesInScaleDao
import de.felixlf.gradingscale.model.Grade
import de.felixlf.gradingscale.model.GradeScale
import de.felixlf.gradingscale.model.GradesInScale
import de.felixlf.gradingscale.utils.MockScale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext


class GiSRepositoryImpl @Inject constructor(
  private val gradesInScaleDao: GradesInScaleDao,
  private val coroutineContext: CoroutineContext,
) : GiSRepository {

  override suspend fun deleteGradesFromScales(gradeScaleId: String) {
    withContext(coroutineContext) {
      gradesInScaleDao.deleteGradesFromScale(gradeScaleId)
      gradesInScaleDao.deleteGradeScalesFromScale(gradeScaleId)
    }
  }

  override suspend fun addGrade(grade: Grade) {
    withContext(coroutineContext) {
      gradesInScaleDao.addGrade(grade)
    }
  }


  override suspend fun removeGrade(grade: Grade) {
    withContext(coroutineContext) {
      gradesInScaleDao.deleteGrade(grade)
    }
  }

  override suspend fun updateGrade(grade: Grade) {
    withContext(coroutineContext) {
      gradesInScaleDao.updateGrade(grade)
    }
  }

  override suspend fun addGradeScale(gradeScale: GradeScale) {
    withContext(coroutineContext) {
      gradesInScaleDao.addScale(gradeScale)
    }
  }

  //Why can I implement with SharedFlow, but in Interface I have Flow?
  override fun getGradesFromScaleFlow(): SharedFlow<List<GradesInScale>> {
    return gradesInScaleDao.getGradesFromScaleFlow().flatMapConcat {
      if (it.isEmpty()) {
        val initialList = generateInitialValues()
        flowOf(initialList)
      } else {
        flowOf(it)
      }
    }.shareIn(CoroutineScope(coroutineContext), SharingStarted.WhileSubscribed(), 1)
  }

  private suspend fun generateInitialValues(): List<GradesInScale> {
    val mockData = MockScale()
    addGradeScale(mockData.gradeScale)
    mockData.gradesList.forEach { addGrade(it) }
    return listOf(mockData.gradesInScale)
  }

  override suspend fun updateGradeScale(currentGradeScale: GradeScale) {
    withContext(coroutineContext) {
      gradesInScaleDao.updateGradeScale(currentGradeScale)
    }
  }

  override suspend fun getGradeOfScales(): List<GradeScale?> {
    val gradeScaleLists: List<GradeScale?>
    withContext(coroutineContext){
      gradeScaleLists = gradesInScaleDao.getGradeScales()
    }
    return gradeScaleLists
  }

}