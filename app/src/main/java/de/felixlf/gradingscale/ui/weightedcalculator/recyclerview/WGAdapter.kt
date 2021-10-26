package de.felixlf.gradingscale.ui.weightedcalculator.recyclerview

import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.felixlf.gradingscale.ui.weightedcalculator.model.WGListItem

const val LIST_HEADER_KEY = 0
const val LIST_ITEM_KEY = 1

class WGAdapter constructor(private val navController: NavController) :
  ListAdapter<WGListItem, RecyclerView.ViewHolder>(WGListDiffUtil()) {

  override fun getItemViewType(position: Int): Int {
    return when (getItem(position)) {
      is WGListItem.WGHeaderViewHolder -> LIST_HEADER_KEY
      is WGListItem.WGItem -> LIST_ITEM_KEY
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      LIST_HEADER_KEY -> WGHeaderViewHolder.from(parent)
      LIST_ITEM_KEY -> WGViewHolder.from(parent)
      else -> throw ClassCastException("Unknown viewType $viewType")
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is WGHeaderViewHolder -> holder.bind()
      is WGViewHolder -> {
        val item = getItem(position) as WGListItem.WGItem
        holder.bind(item)
        holder.setClickListener(item, navController)
      }
    }
  }



}




//fun getListItem(position: Int): WGListItem = getItem(position)

