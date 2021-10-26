package de.felixlf.gradingscale.data.repository

import de.felixlf.gradingscale.model.Grade
import de.felixlf.gradingscale.model.GradeScale
import de.felixlf.gradingscale.model.GradesInScale
import de.felixlf.gradingscale.utils.MockScale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeRepository(val mockScale: MockScale = MockScale("Mini")) : GiSRepository {

  val gradesList = mockScale.gradesList
  val gradeScale = mockScale.gradeScale
  val gradeScaleList = mockScale.gradeScaleList
  val gradesInScale = mockScale.gradesInScale
  val gradesInScaleList = mockScale.gradesInScalesList


  override suspend fun deleteGradesFromScales(gradeScaleId: String) {
    TODO("Not yet implemented")
  }

  override suspend fun addGrade(grade: Grade) {
    getGIS(grade).grades.add(grade)
  }


  override suspend fun removeGrade(grade: Grade) {
    getGIS(grade).grades.remove(grade)
  }

  override suspend fun updateGrade(grade: Grade) {
    getGIS(grade).grades.first { it.id == grade.id }.let {
      it.namedGrade = grade.namedGrade
      it.percentage = grade.percentage
      it.namedGrade2 = grade.namedGrade2
      it.pointedGrade = grade.pointedGrade
    }
  }

  override suspend fun addGradeScale(gradeScale: GradeScale) {
    gradesInScaleList.add(
      GradesInScale(
        gradeScale,
        mutableListOf() //Grade(nameOfScale = gradeScale.gradeScaleName)
      )
    )
  }

  override fun getGradesFromScaleFlow(): Flow<List<GradesInScale>> {
    return flowOf(gradesInScaleList)
  //flow{ emit(gradesInScaleList) }
  }

  override suspend fun updateGradeScale(currentGradeScale: GradeScale) {
    gradesInScaleList
      .find { it.gradeScale.gradeScaleName == currentGradeScale.gradeScaleName }?.gradeScale
      .let { it?.gradeScaleName = currentGradeScale.gradeScaleName }
  }

  override suspend fun getGradeOfScales(): List<GradeScale?> {
    return gradeScaleList
  }


  private fun getGIS(grade: Grade): GradesInScale {
    return gradesInScaleList.find { it.gradeScale.gradeScaleName == grade.nameOfScale }
      ?: GradesInScale(
        GradeScale(gradeScaleName = grade.nameOfScale),
        mutableListOf(grade)
      )
  }

}