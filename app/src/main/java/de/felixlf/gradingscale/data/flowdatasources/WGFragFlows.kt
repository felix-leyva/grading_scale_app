package de.felixlf.gradingscale.data.flowdatasources

import de.felixlf.gradingscale.data.repository.GradeWCalculationsRepository
import de.felixlf.gradingscale.model.GradesInScale
import de.felixlf.gradingscale.model.WCalculationsAndGrades
import de.felixlf.gradingscale.model.WeightedGrade
import de.felixlf.gradingscale.ui.round
import de.felixlf.gradingscale.ui.weightedcalculator.model.WGListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class WGFragFlows @Inject constructor(
  private val dataSource: FlowDataSource,
  gradeWRepository: GradeWCalculationsRepository
) {

  private val calculationsFlow: Flow<List<WCalculationsAndGrades>> =
    gradeWRepository.getCalculationsFlow()

  fun selectedGradeInScaleFlow(selectedGradeScale: MutableStateFlow<String>): Flow<GradesInScale> =
    dataSource.flowGradeInScaleSelection(selectedGradeScale)

  fun selectedCalculation(selectedWGradeCalculation: MutableStateFlow<String>): Flow<WCalculationsAndGrades> =
    calculationsFlow.combineTransform(selectedWGradeCalculation) { wCalcAndGrades, mFilter ->
      when {
        mFilter == "" -> emit(WCalculationsAndGrades())
        wCalcAndGrades.isNotEmpty() -> emit((wCalcAndGrades.firstOrNull { it.gradeCalculation.name == mFilter }
          ?: wCalcAndGrades[0]))
      }

    }

  fun calculationsNamesFlow(): Flow<List<String>> =
    calculationsFlow.map { it.map { it.gradeCalculation.name } }

  fun gradeInScaleNamesFlow(): Flow<List<String>> =
    dataSource.flowGradeScaleNames()

  fun getWGListItemFlow(
    wCalculationGrade: Flow<WCalculationsAndGrades>,
    gradeInScale: Flow<GradesInScale>
  ): Flow<List<WGListItem>> =
    wCalculationGrade.combineTransform(gradeInScale) { wGCalc, gIS ->
      val list: MutableList<WGListItem> = wGCalc.weightedGrades.map {
        WGListItem.WGItem(
          gradeName = gIS.gradeByPercentage(it.percentage).namedGrade,
          percentage = it.percentage * 100,
          points = it.percentage * it.weight,
          totalPoints = it.weight,
          id = it.id.toString()

        )
      }.toMutableList()
      list.add(0, WGListItem.WGHeaderViewHolder)
      emit(list)
    }

  fun getWGradeFromID(
    calculationFlow: Flow<WCalculationsAndGrades>,
    idWGrade: Flow<String>
  ): Flow<WeightedGrade> =
    calculationFlow.combineTransform(idWGrade) { wGCalc, id ->
      val wGrade =
        wGCalc.weightedGrades.firstOrNull { it.id.toString() == id } ?: wGCalc.weightedGrades[0]
      emit(wGrade)
    }

  fun getTotalWeightedGrade(
    wCalculationGrade: Flow<WCalculationsAndGrades>,
    gradeInScale: Flow<GradesInScale>
  ): Flow<WGListItem.WGItem> =
    wCalculationGrade.combineTransform(gradeInScale) { wCalc, giS ->
      wCalc.totalWeightedGrade.apply {
        emit(
          WGListItem.WGItem(
            gradeName = giS.gradeByPercentage(percentage).namedGrade,
            percentage = (percentage * 100).round(2),
            totalPoints = weight,
            points = percentage* weight,
            id = scaleId
          )
        )
      }
    }

}