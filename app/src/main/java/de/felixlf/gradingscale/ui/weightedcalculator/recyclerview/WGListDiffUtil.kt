package de.felixlf.gradingscale.ui.weightedcalculator.recyclerview

import androidx.recyclerview.widget.DiffUtil
import de.felixlf.gradingscale.ui.weightedcalculator.model.WGListItem

class WGListDiffUtil: DiffUtil.ItemCallback<WGListItem>() {
  override fun areItemsTheSame(oldItem: WGListItem, newItem: WGListItem): Boolean {
    return when(oldItem){
      WGListItem.WGHeaderViewHolder -> newItem is WGListItem.WGHeaderViewHolder
      is WGListItem.WGItem -> {
        if (newItem !is WGListItem.WGItem) return false
        oldItem == newItem
      }
    }
  }

  override fun areContentsTheSame(oldItem: WGListItem, newItem: WGListItem): Boolean {
    return oldItem == newItem
  }
}