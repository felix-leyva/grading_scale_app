package de.felixlf.gradingscale.ui.calculator

import de.felixlf.gradingscale.model.GradesInScale
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*


class CalcFragFlows(
  val selectedGradeInScaleFlow: Flow<GradesInScale>,
  val gradeNamePosSF: MutableSharedFlow<Int> = MutableSharedFlow<Int>(),
  val percentageSF: MutableSharedFlow<String> = MutableSharedFlow<String>()
) {

  fun setGradeByName(gradeNamePos: SharedFlow<Int>) =
    selectedGradeInScaleFlow.distinctUntilChanged()
      .combineTransform(gradeNamePos.distinctUntilChanged()) { gIS, pos ->
        val gradeToGIS = try {
          val nameOfGrade = gIS.gradesNamesList()[pos]
          val grade = gIS.gradeByName(nameOfGrade)
          (grade to gIS)
        } catch (ex: Exception) {
          //ex.printStackTrace()
          (gIS.grades[0] to gIS)
        }
        emit(gradeToGIS)
      }


  fun setGradeByName2(grade2NamePos: SharedFlow<Int>) =
    selectedGradeInScaleFlow.distinctUntilChanged()
      .combineTransform(grade2NamePos.distinctUntilChanged()) { gIS, pos ->
        val gradeToGIS = try {
          val nameOfGrade2 = gIS.grades2NamesList()[pos]
          val grade = gIS.grade2ByName(nameOfGrade2)
          (grade to gIS)
        } catch (ex: Exception) {
          //ex.printStackTrace()
          (gIS.grades.last() to gIS)
        }
        emit(gradeToGIS)
      }


  fun setGradeByPercentage(percentage: SharedFlow<String>) =
    selectedGradeInScaleFlow.distinctUntilChanged()
      .combineTransform(percentage.distinctUntilChanged()) { gIS, mPercentage ->
        mPercentage.toDouble().let { percentage ->
          val adjustedPercentage = when {
            percentage > 100.0 -> 1.0
            else -> percentage / 100
          }

          val gradeToGIS = try {
            val grade = gIS.gradeByPercentage(adjustedPercentage).copy(percentage = adjustedPercentage)
            (grade to gIS)
          } catch (ex: Exception) {
            (gIS.grades.first() to gIS)
          }
          emit(gradeToGIS)


        }
      }


  @FlowPreview
  fun setCurrentGradeAndScaleFromFlowOps() = flowOf(
    setGradeByName(gradeNamePosSF),
    setGradeByPercentage(percentageSF)
  ).flattenMerge(2)


}
