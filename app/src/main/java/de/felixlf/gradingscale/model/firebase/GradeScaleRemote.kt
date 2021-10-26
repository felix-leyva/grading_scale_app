package de.felixlf.gradingscale.model.firebase

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class GradeScaleRemote(
  val gradeScaleName: String = "",
  val country: String = "",
  val grades: MutableList<GradeRemote> = mutableListOf()
)
