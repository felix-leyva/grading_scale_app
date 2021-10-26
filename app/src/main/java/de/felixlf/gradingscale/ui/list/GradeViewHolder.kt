package de.felixlf.gradingscale.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.felixlf.gradingscale.databinding.ListItemGradeScaleBinding
import de.felixlf.gradingscale.model.Grade
import de.felixlf.gradingscale.ui.round
import de.felixlf.gradingscale.utils.setLocString
import java.util.*

class GradeViewHolder private constructor(val binding: ListItemGradeScaleBinding) :
  RecyclerView.ViewHolder(binding.root) {

  fun bind(item: GradeListItem.GradeItem) {
    binding.listGrade1Tv.text = item.grade.namedGrade
    binding.listPercentageTv.text = (item.grade.percentage * 100).round(2).setLocString()
    binding.listPointsTv.text = (item.points).round(2).setLocString()
  }

  companion object {
    fun from(parent: ViewGroup): GradeViewHolder {
      val binding =
        ListItemGradeScaleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
      return GradeViewHolder(binding)
    }
  }
}

class GradeHolderListener(val clickListener: (gradeId: UUID?) -> Unit) {
  fun onClick(grade: Grade) = clickListener(grade.id)
}
