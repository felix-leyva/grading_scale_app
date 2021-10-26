package de.felixlf.gradingscale.ui.importremote.recyclerview

import androidx.recyclerview.widget.DiffUtil

class ImportRemoteListDiffUtil: DiffUtil.ItemCallback<ImportRemoteListItem>() {
  override fun areItemsTheSame(
    oldItem: ImportRemoteListItem,
    newItem: ImportRemoteListItem,
  ): Boolean {
    return oldItem == newItem
  }

  override fun areContentsTheSame(
    oldItem: ImportRemoteListItem,
    newItem: ImportRemoteListItem,
  ): Boolean {
    return oldItem == newItem
  }
}