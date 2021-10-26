package de.felixlf.gradingscale.ui.importremote.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

const val LIST_HEADER_KEY = 0
const val LIST_ITEM_KEY = 1

class ImportRemoteAdapter constructor(val clickListener: (ImportRemoteListItem.ImportRemoteItem) -> Unit) :
  ListAdapter<ImportRemoteListItem, RecyclerView.ViewHolder>(ImportRemoteListDiffUtil()) {

  override fun getItemViewType(position: Int): Int {
    return when (getItem(position)) {
      is ImportRemoteListItem.ImportRemoteHeader -> LIST_HEADER_KEY
      is ImportRemoteListItem.ImportRemoteItem -> LIST_ITEM_KEY
    }
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    return when (viewType) {
      LIST_HEADER_KEY -> ImportRemoteHeader.from(parent)
      LIST_ITEM_KEY -> ImportRemoteViewHolder.from(parent)
      else -> throw ClassCastException("Unknown ViewType: $viewType")
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is ImportRemoteViewHolder -> {
        val item = getItem(position) as ImportRemoteListItem.ImportRemoteItem
        holder.bind(item)
        holder.setClickListener(item, clickListener)
      }
      is ImportRemoteHeader -> holder.bind()
    }
  }

}