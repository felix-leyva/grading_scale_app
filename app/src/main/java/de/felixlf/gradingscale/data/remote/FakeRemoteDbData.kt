package de.felixlf.gradingscale.data.remote

import de.felixlf.gradingscale.model.firebase.CountriesAndGradeScales
import de.felixlf.gradingscale.model.firebase.GradeRemote
import de.felixlf.gradingscale.model.firebase.GradeScaleRemote

object FakeRemoteDbData {
  fun generateFakeGradeScale() = GradeScaleRemote(
    "TestName",
    "Mexico",
    mutableListOf(
      GradeRemote("A2", .9),
      GradeRemote("B", .8),
      GradeRemote("C", .7),
      GradeRemote("D2", .6),
      GradeRemote("E", .5),
      GradeRemote("F2", .4)
    )
  )

  fun generateFakeMiniGradeScale() = GradeScaleRemote(
    "Mini",
    "Germany",
    mutableListOf(
      GradeRemote("E1+", 0.98),
      GradeRemote("E1", .94),
      GradeRemote("E1-", .9),
      GradeRemote("E2+", .87),
      GradeRemote("E2", .84),
      GradeRemote("E2-", .8),
      GradeRemote("E3+", .75),
      GradeRemote("E3", .7),
      GradeRemote("E3-", .65),
      GradeRemote("E4+", .6),
      GradeRemote("E4", .5),
      GradeRemote("E4-", .49),
      GradeRemote("G2+", .48),
      GradeRemote("G2", .46),
      GradeRemote("G2-", .45),
      GradeRemote("G3", .42),
      GradeRemote("G3+", .4),
      GradeRemote("G3-", .37),
      GradeRemote("G4+", .35),
      GradeRemote("G4", .32),
      GradeRemote("G4-", .29),
      GradeRemote("G5+", .23),
      GradeRemote("G5", .17),
      GradeRemote("G5-", .12),
      GradeRemote("G6", .11)
    )

  )

  fun generateFakeGradeScaleList(): List<GradeScaleRemote> {
    val gradeScale1 = generateFakeGradeScale()
    val gradeScale2 = gradeScale1.copy(gradeScaleName = "Test2")
    return listOf(gradeScale1, gradeScale2, generateFakeMiniGradeScale())
  }

  fun generateFakeGradesAndCountries() = listOf(
    CountriesAndGradeScales("Mexico", listOf("TestName", "SEP", "UDLAP")),
    CountriesAndGradeScales("Germany", listOf("Mini", "Abitur", "ETC")),
  )

}