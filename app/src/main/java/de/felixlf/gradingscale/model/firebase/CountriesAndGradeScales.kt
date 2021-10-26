package de.felixlf.gradingscale.model.firebase

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class CountriesAndGradeScales(
  val countryName: String = "",
  val gradeScalesNames: List<String> = listOf()
)
