package de.felixlf.gradingscale.ui.list

import androidx.recyclerview.widget.DiffUtil

class GradeListDiffUtil: DiffUtil.ItemCallback<GradeListItem>() {
  override fun areItemsTheSame(oldItem: GradeListItem, newItem: GradeListItem): Boolean {
    return when(oldItem) {
      GradeListItem.GradesHeader -> newItem is GradeListItem.GradesHeader

      is GradeListItem.GradeItem ->  {
        if (newItem !is GradeListItem.GradeItem) return false
        oldItem.grade == newItem.grade
      }

    }
  }

  override fun areContentsTheSame(oldItem: GradeListItem, newItem: GradeListItem): Boolean {
    return oldItem == newItem
  }

}
