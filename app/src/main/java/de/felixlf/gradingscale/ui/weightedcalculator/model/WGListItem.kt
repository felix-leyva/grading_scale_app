package de.felixlf.gradingscale.ui.weightedcalculator.model


sealed class WGListItem(val viewType: Int){
  object WGHeaderViewHolder: WGListItem(0)
  data class WGItem(var gradeName: String, var percentage: Double, var points: Double, var totalPoints: Double, val id: String): WGListItem(1)
}

