package de.felixlf.gradingscale.model.converters

import de.felixlf.gradingscale.model.Grade
import de.felixlf.gradingscale.model.GradeScale
import de.felixlf.gradingscale.model.GradesInScale
import de.felixlf.gradingscale.model.firebase.GradeRemote
import de.felixlf.gradingscale.model.firebase.GradeScaleRemote

fun generateGradeInScaleFromRemoteGradeScale(remoteGradeScale: GradeScaleRemote): GradesInScale {
  val gradeScaleName = "${remoteGradeScale.country}_${remoteGradeScale.gradeScaleName}"
  return GradesInScale(
    gradeScale = GradeScale(gradeScaleName = gradeScaleName),
    grades = remoteGradeScale.grades.map {
      Grade(
        namedGrade = it.gradeName,
        percentage = it.percentage,
        nameOfScale = gradeScaleName
      )
    }.toMutableList()
  )
}

fun generateRemoteGradesFromGradeInScale(
  gradesInScale: GradesInScale,
  country: String,
): GradeScaleRemote {
  val gradeScaleName = gradesInScale.gradeScale.gradeScaleName.substringAfter("_")
  return GradeScaleRemote(
    gradeScaleName = gradeScaleName,
    country = country,
    grades = gradesInScale.grades.map { GradeRemote(it.namedGrade, it.percentage) }
      .toMutableList()
  )
}