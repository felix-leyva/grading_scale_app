package de.felixlf.gradingscale.ui.importremote.recyclerview

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.felixlf.gradingscale.databinding.ListItemImportBinding

class ImportRemoteViewHolder private constructor(val bind: ListItemImportBinding) :
  RecyclerView.ViewHolder(bind.root) {
  @SuppressLint("SetTextI18n")
  fun bind(item: ImportRemoteListItem.ImportRemoteItem) {
    bind.text.text = "${item.country} - ${item.nameScale}"
  }

  fun setClickListener(
    item: ImportRemoteListItem.ImportRemoteItem,
    clickListener: (ImportRemoteListItem.ImportRemoteItem) -> Unit,
  ) {
    bind.root.setOnClickListener { clickListener(item) }
  }

  companion object {
    fun from(parent: ViewGroup): ImportRemoteViewHolder {
      val bind =
        ListItemImportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
      return ImportRemoteViewHolder(bind)
    }
  }
}