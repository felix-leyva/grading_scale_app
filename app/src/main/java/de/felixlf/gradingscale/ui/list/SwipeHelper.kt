package de.felixlf.gradingscale.ui.list

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.ExperimentalCoroutinesApi

class SwipeDeleteHelper(private val adapter: GSAdapter) :
  ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {


  override fun onMove(
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder,
    target: RecyclerView.ViewHolder
  ): Boolean {
    return false
  }

  @ExperimentalCoroutinesApi
  override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    val position = viewHolder.adapterPosition
    adapter.viewModel.setGradeToDelete(position)
  }

  override fun getMovementFlags(
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder
  ): Int {
    val position = viewHolder.adapterPosition
    val item = adapter.getListItem(position)
    return if (item is GradeListItem.GradeItem) {
      makeMovementFlags(0, ItemTouchHelper.LEFT)
    } else {
      makeMovementFlags(0, 0)
    }
  }
}

class SwipeUpdateHelper(private val adapter: GSAdapter) :
  ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {


  override fun onMove(
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder,
    target: RecyclerView.ViewHolder
  ): Boolean {
    return false
  }

  @ExperimentalCoroutinesApi
  override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
    val position = viewHolder.adapterPosition
    adapter.viewModel.dialogEditGrade(position)
  }

  override fun getMovementFlags(
    recyclerView: RecyclerView,
    viewHolder: RecyclerView.ViewHolder
  ): Int {
    val position = viewHolder.adapterPosition
    val item = adapter.getListItem(position)
    return if (item is GradeListItem.GradeItem) {
      makeMovementFlags(0, ItemTouchHelper.RIGHT)
    } else {
      makeMovementFlags(0, 0)
    }
  }
}
