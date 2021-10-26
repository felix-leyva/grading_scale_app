package de.felixlf.gradingscale.ui.weightedcalculator.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import de.felixlf.gradingscale.databinding.WgListItemGradeScaleBinding
import de.felixlf.gradingscale.ui.round
import de.felixlf.gradingscale.ui.weightedcalculator.WeightedGradeCalcFragmentDirections
import de.felixlf.gradingscale.ui.weightedcalculator.model.WGListItem
import de.felixlf.gradingscale.utils.setLocString

class WGViewHolder(val binding: WgListItemGradeScaleBinding) :
  RecyclerView.ViewHolder(binding.root) {

  fun bind(item: WGListItem.WGItem) {
    binding.listGrade1Tv.text = item.gradeName
    binding.listPercentageTv.text = item.percentage.round(2).setLocString()
    binding.listTotalPointsTv.text = item.totalPoints.round(2).setLocString()
    binding.listPointsTv.text = item.points.round(2).setLocString()
  }

  fun setClickListener(item: WGListItem.WGItem, navController: NavController) {
    binding.root.setOnClickListener {
      WeightedGradeCalcFragmentDirections
        .actionWeightedGradeCalcFragmentToWGradeAddModifyDialog().apply {
          edit = true
          id = item.id
          navController.navigate(this)
        }
    }

  }


  companion object {
    fun from(parent: ViewGroup): WGViewHolder {
      val binding =
        WgListItemGradeScaleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
      return WGViewHolder(binding)
    }
  }

}