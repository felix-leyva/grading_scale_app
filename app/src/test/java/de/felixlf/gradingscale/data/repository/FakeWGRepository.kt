package de.felixlf.gradingscale.data.repository

import de.felixlf.gradingscale.model.GWCalculation
import de.felixlf.gradingscale.model.WCalculationsAndGrades
import de.felixlf.gradingscale.model.WeightedGrade
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeWGRepository(val mW: MockWGrades = MockWGrades()) : GradeWCalculationsRepository {



  override fun getCalculationsFlow(): Flow<List<WCalculationsAndGrades>> {
    return flowOf(mW.wCalculationsAndGrades)
  }

  override suspend fun updateWeightedGrade(weightedGrade: WeightedGrade) {
    val (indexCalculation, indexGrade) = findIndex(weightedGrade)
    if (indexGrade != -1)
      mW.wCalculationsAndGrades[indexCalculation].weightedGrades[indexGrade] = weightedGrade
      println(mW.wCalculationsAndGrades[indexCalculation].weightedGrades[indexGrade])
  }


  override suspend fun insertWeightedGrade(weightedGrade: WeightedGrade) {
    val (indexCalculation, indexGrade) = findIndex(weightedGrade)
    mW.wCalculationsAndGrades[indexCalculation].weightedGrades.add(weightedGrade)
  }

  override suspend fun deleteWeightedGrade(weightedGrade: WeightedGrade) {
    val (indexCalculation, indexGrade) = findIndex(weightedGrade)
    if (indexGrade != -1) mW.wCalculationsAndGrades[indexCalculation].weightedGrades.removeAt(
      indexGrade
    )
  }

  override suspend fun updateCalculation(gradeWCalculations: GWCalculation) {
    val indexCalculation = findIndexCalc(gradeWCalculations)
    if (indexCalculation != -1)
      mW.wCalculationsAndGrades[indexCalculation].gradeCalculation = gradeWCalculations
  }

  override suspend fun insertCalculation(gradeWCalculations: GWCalculation) {
    mW.wCalculationsAndGrades.add(
      WCalculationsAndGrades(
        gradeWCalculations,
        mutableListOf(WeightedGrade(0.5, 1.0, gradeWCalculations.id.toString()))
      )
    )
  }

  override suspend fun deleteCalculation(gradeWCalculations: GWCalculation) {
    val indexCalculation = findIndexCalc(gradeWCalculations)
    if (indexCalculation != -1)
      mW.wCalculationsAndGrades.removeAt(indexCalculation)
  }

  fun findIndex(weightedGrade: WeightedGrade): Pair<Int, Int> {
    val indexCalculation = mW.wCalculationsAndGrades
      .indexOfFirst { it.gradeCalculation.id.toString() == weightedGrade.scaleId }
    val indexGrade = if (indexCalculation != -1) {
      mW.weightedGradesList[indexCalculation].indexOfFirst { it.id == weightedGrade.id }
    } else -1
    return (indexCalculation to indexGrade)
  }

  fun findIndexCalc(gradeWCalculations: GWCalculation): Int {
    return mW.wCalculationsAndGrades.indexOfFirst { it.gradeCalculation.id == gradeWCalculations.id }
  }



}