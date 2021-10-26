package de.felixlf.gradingscale.model.firebase

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class GradeRemote(
  var gradeName: String = "",
  var percentage: Double = 0.0
)
