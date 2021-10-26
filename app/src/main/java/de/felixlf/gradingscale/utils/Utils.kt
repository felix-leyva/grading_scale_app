package de.felixlf.gradingscale.utils

import de.felixlf.gradingscale.model.Grade

fun List<Grade>.nameandScale(): String {
  return this.map { "${it.namedGrade}: ${it.nameOfScale}" }.toString()
}

fun checkTotalPoints(totalPoints: String): Double {
  val totalPointsDouble = totalPoints.toDouble()
  return when {
    totalPointsDouble < 0.0 -> 0.0
    else -> totalPointsDouble
  }
}

fun checkPoints(points: String, totalPoints: Double): Double {
  val pointsDouble = points.toDouble()
  return when {
    pointsDouble < 0.0 -> 0.0
    pointsDouble > totalPoints -> totalPoints
    else -> pointsDouble
  }
}

//https://stackoverflow.com/questions/13154463/how-can-i-check-for-generic-type-in-kotlin
inline fun <reified T> Any?.tryCast(block: T.() -> Unit) {
  if (this is T) {
    block()
  }
}
