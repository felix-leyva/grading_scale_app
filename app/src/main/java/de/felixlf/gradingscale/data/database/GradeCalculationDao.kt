package de.felixlf.gradingscale.data.database

import androidx.room.*
import de.felixlf.gradingscale.model.GWCalculation
import de.felixlf.gradingscale.model.WCalculationsAndGrades
import de.felixlf.gradingscale.model.WeightedGrade
import kotlinx.coroutines.flow.Flow

@Dao
interface GradeCalculationDao {
  @Transaction
  @Query("SELECT * FROM gwcalculation")
  fun getCalculationsFlow(): Flow<List<WCalculationsAndGrades>>

  @Update
  suspend fun updateWeightedGrade(weightedGrade: WeightedGrade)

  @Insert
  suspend fun insertWeightedGrade(weightedGrade: WeightedGrade)

  @Delete
  suspend fun deleteWeightedGrade(weightedGrade: WeightedGrade)

  @Update
  suspend fun updateCalculation(gradeWCalculations: GWCalculation)

  @Insert
  suspend fun insertCalculation(gradeWCalculations: GWCalculation)

  @Delete
  suspend fun deleteCalculation(gradeWCalculations: GWCalculation)

  @Query("DELETE FROM WeightedGrade WHERE scaleId=(:calculationId) ")
  suspend fun deleteGradesFromCalculationID(calculationId: String)

}