package de.felixlf.gradingscale.utils

import de.felixlf.gradingscale.model.Grade
import de.felixlf.gradingscale.model.GradeScale
import de.felixlf.gradingscale.model.GradesInScale

class MockScale(gradeScaleName : String = "Mini") {

  val gradeScale = GradeScale(gradeScaleName = gradeScaleName)
  val gradeScale2 = GradeScale(gradeScaleName = "2 $gradeScaleName")
  val gradeScaleList : MutableList<GradeScale>
  val gradesList: MutableList<Grade>
  val gradesList2 =  mutableListOf<Grade>()
  val gradesInScale : GradesInScale
  val gradesInScale2 : GradesInScale
  val gradesInScalesList : MutableList<GradesInScale>

  init {
    gradesList = minGrades

    gradesList.forEach {
      it.nameOfScale = gradeScaleName
      Grade(it.namedGrade, it.percentage, it.pointedGrade, it.namedGrade2, gradeScale2.gradeScaleName)
        .let(gradesList2::add)
    }

    gradeScaleList = mutableListOf(gradeScale, gradeScale2)
    gradesInScale = GradesInScale(gradeScale, gradesList)
    gradesInScale2 = GradesInScale(gradeScale2, gradesList2)
    gradesInScalesList = mutableListOf(gradesInScale, gradesInScale2)
  }


  companion object {

    val minGrades = mutableListOf(
      Grade("E1+", 0.98, 15.0, "1+"),
      Grade("E1", .94, 14.0, "1"),
      Grade("E1-", .9, 13.0, "1-"),
      Grade("E2+", .87, 12.0, "2+"),
      Grade("E2", .84, 11.0, "2"),
      Grade("E2-", .8, 10.0, "2-"),
      Grade("E3+", .75, 9.0, "3+"),
      Grade("E3", .7, 8.0, "3"),
      Grade("E3-", .65, 7.0, "3-"),
      Grade("E4+", .6, 6.0, "4+"),
      Grade("E4", .5, 5.0, "4"),
      Grade("E4-", .49, 4.0, "4-"),
      Grade("G2+", .48, 3.0, "5"),
      Grade("G2", .46, 2.0),
      Grade("G2-", .45, 1.0),
      Grade("G3", .42),
      Grade("G3+", .4),
      Grade("G3-", .37),
      Grade("G4+", .35),
      Grade("G4", .32),
      Grade("G4-", .29),
      Grade("G5+", .23, namedGrade2 = "6"),
      Grade("G5", .17),
      Grade("G5-", .12),
      Grade("G6", .11)
    )


  }
}