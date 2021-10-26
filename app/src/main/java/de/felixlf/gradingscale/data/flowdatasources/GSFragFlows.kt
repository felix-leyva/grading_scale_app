package de.felixlf.gradingscale.data.flowdatasources

import de.felixlf.gradingscale.model.Grade
import de.felixlf.gradingscale.model.GradesInScale
import de.felixlf.gradingscale.ui.list.GradeListItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.distinctUntilChanged

class GSFragFlows(
  val dataSource: FlowDataSource,
  val selectedGradeScale: MutableStateFlow<String>,
  val gsTotalPoints: Flow<Double>,
  val selectedGradePos: Flow<Int>
) {

  val selectedGradeInScaleFlow: Flow<GradesInScale> =
    dataSource.flowGradeInScaleSelection(selectedGradeScale)

  @ExperimentalCoroutinesApi
  fun setGradeListItemStream() =
    selectedGradeInScaleFlow.distinctUntilChanged()
      .combineTransform(gsTotalPoints) { gIS, totalPoints ->
        val sortedGradeList = gIS.grades.sortedByDescending { it.percentage }
        val gradeScaleListItems = sortedGradeList.map { grade ->
          GradeListItem.GradeItem(grade, (grade.percentage * totalPoints))
        }.toMutableList()
        emit(listOf(GradeListItem.GradesHeader, *gradeScaleListItems.toTypedArray()))
      }

  @ExperimentalCoroutinesApi
  fun getSelectedGrade() =
    setGradeListItemStream().distinctUntilChanged()
      .combineTransform(selectedGradePos) { gradeListItems, position ->

        val grade = when {
          position == -1 || position == 0 -> Grade()
          position >= gradeListItems.size -> Grade()
          gradeListItems[position] is GradeListItem.GradesHeader -> Grade()
          else -> (gradeListItems[position] as GradeListItem.GradeItem).grade
        }
        emit(grade)
      }


}