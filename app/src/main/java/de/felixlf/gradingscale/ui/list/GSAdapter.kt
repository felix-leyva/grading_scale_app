package de.felixlf.gradingscale.ui.list

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.ExperimentalCoroutinesApi

const val LIST_HEADER_KEY = 0
const val LIST_GRADE_ITEM_KEY = 1

class GSAdapter @ExperimentalCoroutinesApi constructor(val viewModel: GSViewModel) :
  ListAdapter<GradeListItem, RecyclerView.ViewHolder>(GradeListDiffUtil()) {

  override fun getItemViewType(position: Int): Int {
    return when (getItem(position)) {
      is GradeListItem.GradesHeader -> LIST_HEADER_KEY
      is GradeListItem.GradeItem -> LIST_GRADE_ITEM_KEY
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      LIST_HEADER_KEY -> HeaderViewHolder.from(parent)
      LIST_GRADE_ITEM_KEY -> GradeViewHolder.from(parent)
      else -> throw ClassCastException("Unknown viewType $viewType")
    }
  }

  @ExperimentalCoroutinesApi
  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is HeaderViewHolder -> holder.bind()
      is GradeViewHolder -> {
        val item = getItem(position) as GradeListItem.GradeItem
        holder.apply {
          bind(item)
          binding.root.setOnClickListener { viewModel.dialogEditGrade(position) }  //simple solution but should be sent to the viewholder as a lambda
        }
      }
    }
  }

  fun getListItem(position: Int): GradeListItem {
    return getItem(position)
  }
}

