package de.felixlf.gradingscale.model

import androidx.room.Embedded
import androidx.room.Relation


data class WCalculationsAndGrades(
  @Embedded var gradeCalculation: GWCalculation = GWCalculation(),
  @Relation(
    parentColumn = "id",
    entityColumn = "scaleId"
  )
  var weightedGrades: MutableList<WeightedGrade> = mutableListOf()
) {


  val totalWeightedGrade: WeightedGrade
    get() {
      if (weightedGrades.isEmpty()) return WeightedGrade(0.0, 0.0, gradeCalculation.id.toString())
      val percentage = weightedGrades.map { it.percentage * it.weight }.reduce { acc, d -> acc + d }
      val weightTotal = weightedGrades.map { it.weight }.reduce { acc, d -> acc + d }
      return WeightedGrade((percentage/weightTotal), weightTotal, gradeCalculation.id.toString())
    }

  fun weightedGradesWithScale(currentGradeInScale: GradesInScale): List<Grade> {
    if (gradesListAreCorrect(currentGradeInScale)) return mutableListOf()
    return weightedGrades.map { currentGradeInScale.gradeByPercentage(it.percentage) }
  }

  private fun gradesListAreCorrect(currentGradeInScale: GradesInScale) =
    weightedGrades.isEmpty() || currentGradeInScale.grades.isEmpty()

  //Functions only for internal GradeW list modification
  fun addWeightedGrade(percentage: Double, weight: Double) {
    addWeightedGrade(
      WeightedGrade(
        percentage = percentage,
        weight = weight,
        scaleId = gradeCalculation.id.toString()
      )
    )
  }

  fun addWeightedGrade(wGrade: WeightedGrade) {
    weightedGrades.add(checkedGrade(wGrade))
  }

  fun addListOfWGrades(wGrades: List<WeightedGrade>) {
    wGrades.forEach { addWeightedGrade(it) }
  }

  @JvmName("addListOfWGrades1")
  fun addListOfWGrades(percentageAndWeightList: List<Pair<Double, Double>>) {
    percentageAndWeightList.forEach { (percentage, weight) ->
      addWeightedGrade(percentage, weight)
    }
  }

  fun removeGradeInPos(pos: Int) {
    if (pos < 0 || pos > weightedGrades.size - 1) return
    weightedGrades.removeAt(pos)
  }

  companion object {
    //Functions to ensure correct data insertion of external data sources (Room)
    fun checkedGrade(wGrade: WeightedGrade): WeightedGrade {
      when {
        wGrade.weight < 0 -> wGrade.weight = 0.0
        wGrade.percentage > 1.0 -> wGrade.percentage = 1.0
        wGrade.percentage < 0.0 -> wGrade.percentage = 0.0
      }
      return wGrade
    }

    fun generateWeightedCorrectWGrade(percentage: Double, weight: Double, id: String) =
      checkedGrade(WeightedGrade(percentage = percentage, weight = weight, scaleId = id))

    fun generateListOfCorrectWGrades(percentageWeightIdList: List<Triple<Double, Double, String>>) =
      percentageWeightIdList.map { (percentage, weight, id) ->
        generateWeightedCorrectWGrade(percentage, weight, id)
      }

  }


}