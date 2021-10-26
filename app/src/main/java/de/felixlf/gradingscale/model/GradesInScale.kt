package de.felixlf.gradingscale.model

import androidx.room.Embedded
import androidx.room.Relation

data class GradesInScale(
  @Embedded var gradeScale: GradeScale,
  @Relation(
    parentColumn = "gradeScaleId",
    //entity = Grade::class,
    entityColumn = "nameOfScale"
  )
  var grades: MutableList<Grade>

) {
  private fun sortedGrades() = grades.sortedByDescending { it.percentage }


  fun gradesNamesList() = sortedGrades().map { grade -> grade.namedGrade }

  fun grades2NamesList() =
    sortedGrades().filter { it.namedGrade2 != "" }.map { grade -> grade.namedGrade2 }

  fun getPercentage(gradeName: String) =
    sortedGrades().find { it.namedGrade == gradeName }?.percentage ?: 0.0


  fun getGradeNamePos(percentage: Double): Int {
    var pos = sortedGrades().indexOfFirst { validPercentage(percentage) >= it.percentage }
    if (pos == -1 && sortedGrades().size > 1) pos = sortedGrades().size - 1
    return pos
  }

  fun getGrade2NamePos(percentage: Double): Int {
    val cGrade = sortedGrades().find {
      validPercentage(percentage) >= it.percentage && it.namedGrade2 != ""
    } ?: sortedGrades().last()

    var pos = grades2NamesList().indexOfFirst { cGrade.namedGrade2 == it }
    if (pos == -1 && grades2NamesList().size > 1) pos = grades2NamesList().size - 1
    return pos
  }

  fun getPoints(percentage: Double, totalPoints: Double): Double {
    return totalPoints * validPercentage(percentage)

  }

  fun getPoints(gradeName: String, totalPoints: Double): Double {
    val percentage = getPercentage(gradeName)
    return getPoints(percentage, totalPoints)
  }

  fun getPoints(grade: Grade, totalPoints: Double): Double {
    val percentage = grade.percentage
    return totalPoints * grade.percentage
  }

  fun gradeByName(gradeName: String): Grade {
    val grades = sortedGrades()
    return grades.find { it.namedGrade == gradeName } ?: sortedGrades().last()
  }

  fun grade2ByName(gradeName2: String): Grade {
    return sortedGrades().find { it.namedGrade2 == gradeName2 } ?: sortedGrades().last()
  }

  fun gradeByPercentage(percentage: Double): Grade {
    val cPercentage = validPercentage(percentage)
    val grades = sortedGrades()
    return sortedGrades().find { cPercentage >= it.percentage } ?: sortedGrades().last()
  }

  fun gradeByPoints(points: Double, totalPoints: Double): Grade {
    val cPoints = validPoints(points, totalPoints)
    val percentage = cPoints / totalPoints
    return gradeByPercentage(percentage)
  }


//  fun gradeByTotalPoints(totalPoints: Double, percentage: Double): Grade {
//    val cPoints = totalPoints * percentage
//    val newPercentage = cPoints / totalPoints
//    return gradeByPercentage(newPercentage)
//  }

  private fun validPercentage(percentage: Double) = when {
    percentage > 1.0 -> 1.0
    percentage < 0.0 -> 0.0
    else -> percentage
  }

  private fun validPoints(points: Double, totalPoints: Double) = when {
    points < 0.0 -> 0.0
    points > totalPoints -> totalPoints
    else -> points
  }


}