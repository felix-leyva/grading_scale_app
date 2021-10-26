package de.felixlf.gradingscale.data.repository

import de.felixlf.gradingscale.data.database.GradeCalculationDao
import de.felixlf.gradingscale.model.GWCalculation
import de.felixlf.gradingscale.model.WCalculationsAndGrades
import de.felixlf.gradingscale.model.WeightedGrade
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class GradeWCalculationsRepositoryImpl @Inject constructor(
  private val gradeCalculationDao: GradeCalculationDao,
  private val coroutineContext: CoroutineContext

) : GradeWCalculationsRepository {
  @FlowPreview
  override fun getCalculationsFlow(): Flow<List<WCalculationsAndGrades>> {
    return gradeCalculationDao.getCalculationsFlow().transform { listCalculationAndGrades ->
      val flow = when {
        listCalculationAndGrades.isEmpty() -> {
          val wCalculationsAndGrades = generateAndAddDefaultCalculationAndWGrade()
          (listOf(wCalculationsAndGrades))
        }
        listCalculationAndGrades[0].weightedGrades.isEmpty() -> {
          val wCalculationsAndGrades = generateGradesInEmptyFirstCalculation(listCalculationAndGrades[0])
          (listOf(wCalculationsAndGrades))
        }
        else -> { (listCalculationAndGrades) }
      }
      emit(flow)
    }
  }


  private suspend fun generateAndAddDefaultCalculationAndWGrade(): WCalculationsAndGrades {
    val calculation = GWCalculation("Default")
    val wGrade = WeightedGrade(0.5, 1.0, calculation.id.toString())
    val wCalculationsAndGrades = WCalculationsAndGrades(calculation, mutableListOf(wGrade))
    insertCalculation(calculation)
    insertWeightedGrade(wGrade)
    return wCalculationsAndGrades
  }

  private suspend fun generateGradesInEmptyFirstCalculation(firstWCalculationAndGrades: WCalculationsAndGrades): WCalculationsAndGrades {
    val scaleId = firstWCalculationAndGrades.gradeCalculation.id.toString()
    val wGrade = WeightedGrade(0.5, 1.0, scaleId)
    val wCalculationToEmit = firstWCalculationAndGrades.copy(weightedGrades = mutableListOf(wGrade))
    insertWeightedGrade(wGrade)
    return wCalculationToEmit
  }

  override suspend fun updateWeightedGrade(weightedGrade: WeightedGrade) {
    withContext(coroutineContext) {
      gradeCalculationDao.updateWeightedGrade(WCalculationsAndGrades.checkedGrade(weightedGrade))
    }
  }

  override suspend fun insertWeightedGrade(weightedGrade: WeightedGrade) {
    withContext(coroutineContext) {
      gradeCalculationDao.insertWeightedGrade(WCalculationsAndGrades.checkedGrade(weightedGrade))
    }
  }

  override suspend fun deleteWeightedGrade(weightedGrade: WeightedGrade) {
    withContext(coroutineContext) {
      gradeCalculationDao.deleteWeightedGrade(weightedGrade)
    }
  }

  override suspend fun updateCalculation(gradeWCalculations: GWCalculation) {
    withContext(coroutineContext) {
      gradeCalculationDao.updateCalculation(gradeWCalculations)
    }
  }

  override suspend fun insertCalculation(gradeWCalculations: GWCalculation) {
    withContext(coroutineContext) {
      gradeCalculationDao.insertCalculation(gradeWCalculations)
    }
  }

  override suspend fun deleteCalculation(gradeWCalculations: GWCalculation) {
    withContext(coroutineContext) {
      gradeCalculationDao.deleteCalculation(gradeWCalculations)
      gradeCalculationDao.deleteGradesFromCalculationID(gradeWCalculations.id.toString())
    }
  }
}