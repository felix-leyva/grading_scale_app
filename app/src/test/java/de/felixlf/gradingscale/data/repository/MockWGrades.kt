package de.felixlf.gradingscale.data.repository

import de.felixlf.gradingscale.model.GWCalculation
import de.felixlf.gradingscale.model.WCalculationsAndGrades
import de.felixlf.gradingscale.model.WeightedGrade

class MockWGrades {

  val gWcalculations = mutableListOf(
    GWCalculation("Test1"),
    GWCalculation("Test2"),
    GWCalculation("Test3")
  )
  val ids = listOf(
    gWcalculations[0].id.toString(),
    gWcalculations[1].id.toString(),
    gWcalculations[2].id.toString()
  )

  val weightedGrades1 = mutableListOf(
    WeightedGrade(0.9, 1.0, ids[0]),
    WeightedGrade(0.8, 1.0, ids[0]),
    WeightedGrade(0.7, 1.0, ids[0]),
    WeightedGrade(0.9, 2.0, ids[0]),
    WeightedGrade(0.9, 2.0, ids[0])
  )

  val weightedGrades2 = mutableListOf(
    WeightedGrade(0.19, 11.0, ids[1]),
    WeightedGrade(0.18, 11.0, ids[1]),
    WeightedGrade(0.17, 11.0, ids[1]),
    WeightedGrade(0.29, 22.0, ids[1]),
    WeightedGrade(0.39, 22.0, ids[1])
  )
  val weightedGrades3 = mutableListOf(
    WeightedGrade(0.59, 11.0, ids[2]),
    WeightedGrade(0.58, 12.0, ids[2]),
    WeightedGrade(0.57, 13.0, ids[2]),
    WeightedGrade(0.39, 24.0, ids[2]),
    WeightedGrade(0.39, 25.0, ids[2])
  )

  val weightedGradesList = mutableListOf(weightedGrades1, weightedGrades2, weightedGrades3)

  val wCalculationsAndGrades = mutableListOf(
    WCalculationsAndGrades(gWcalculations[0], weightedGradesList[0]),
    WCalculationsAndGrades(gWcalculations[1], weightedGradesList[1]),
    WCalculationsAndGrades(gWcalculations[2], weightedGradesList[2])
  )

}