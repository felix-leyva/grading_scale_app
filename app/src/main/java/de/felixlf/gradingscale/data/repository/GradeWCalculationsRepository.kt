package de.felixlf.gradingscale.data.repository

import de.felixlf.gradingscale.model.GWCalculation
import de.felixlf.gradingscale.model.WCalculationsAndGrades
import de.felixlf.gradingscale.model.WeightedGrade
import kotlinx.coroutines.flow.Flow

interface GradeWCalculationsRepository {
  fun getCalculationsFlow(): Flow<List<WCalculationsAndGrades>>
  suspend fun updateWeightedGrade(weightedGrade: WeightedGrade)
  suspend fun insertWeightedGrade(weightedGrade: WeightedGrade)
  suspend fun deleteWeightedGrade(weightedGrade: WeightedGrade)
  suspend fun updateCalculation(gradeWCalculations: GWCalculation)
  suspend fun insertCalculation(gradeWCalculations: GWCalculation)
  suspend fun deleteCalculation(gradeWCalculations: GWCalculation)

}