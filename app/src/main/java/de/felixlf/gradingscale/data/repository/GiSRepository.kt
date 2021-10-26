package de.felixlf.gradingscale.data.repository

import de.felixlf.gradingscale.model.Grade
import de.felixlf.gradingscale.model.GradeScale
import de.felixlf.gradingscale.model.GradesInScale
import kotlinx.coroutines.flow.Flow

interface GiSRepository {
  suspend fun deleteGradesFromScales(gradeScaleId: String)
  suspend fun addGrade(grade: Grade)
  suspend fun removeGrade(grade: Grade)
  suspend fun updateGrade(grade: Grade)
  suspend fun addGradeScale(gradeScale: GradeScale)
  fun getGradesFromScaleFlow(): Flow<List<GradesInScale>>
  suspend fun updateGradeScale(currentGradeScale: GradeScale)
  suspend fun getGradeOfScales(): List<GradeScale?>
}