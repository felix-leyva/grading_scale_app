package de.felixlf.gradingscale.ui.list

import de.felixlf.gradingscale.model.Grade

sealed class GradeListItem(val viewType: Int) {
  object GradesHeader: GradeListItem(0) //represents header
  data class GradeItem(val grade: Grade, var points: Double): GradeListItem(1)
}
