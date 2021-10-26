package de.felixlf.gradingscale.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.felixlf.gradingscale.R
import de.felixlf.gradingscale.databinding.ListItemHeaderBinding

class HeaderViewHolder private constructor(val binding: ListItemHeaderBinding): RecyclerView.ViewHolder(binding.root) {

  fun bind(){
    binding.apply {
      listGrade1Tv.text = root.context.getString(R.string.grade_name_col)
      listPercentageTv.text = root.context.getString(R.string.percentage_col)
      listPointsTv.text = root.context.getString(R.string.points_col)
    }

  }

  companion object{
    fun from(parent: ViewGroup): HeaderViewHolder {
      val binding = ListItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
      return HeaderViewHolder(binding)
    }
  }
}