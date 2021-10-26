package de.felixlf.gradingscale.ui.weightedcalculator.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.felixlf.gradingscale.R
import de.felixlf.gradingscale.databinding.WgListItemHeaderBinding

class WGHeaderViewHolder private constructor(val binding: WgListItemHeaderBinding) :
  RecyclerView.ViewHolder(binding.root) {
  fun bind() {
    binding.apply {
      listGrade1Tv.text = root.context.getString(R.string.grade_name_col)
      listPercentageTv.text = root.context.getString(R.string.percentage_col)
      listPointsTv.text = root.context.getString(R.string.points_col)
      listTotalPointsTv.text = root.context.getString(R.string.total_points_col)
    }
  }

  companion object {
    fun from(parent: ViewGroup): WGHeaderViewHolder {
      val binding = WgListItemHeaderBinding
        .inflate(LayoutInflater.from(parent.context), parent, false)
      return WGHeaderViewHolder(binding)
    }
  }
}