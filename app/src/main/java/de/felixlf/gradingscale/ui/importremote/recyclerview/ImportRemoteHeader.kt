package de.felixlf.gradingscale.ui.importremote.recyclerview

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.felixlf.gradingscale.R
import de.felixlf.gradingscale.databinding.ListItemImportBinding

class ImportRemoteHeader private constructor(val bind: ListItemImportBinding) :
  RecyclerView.ViewHolder(bind.root) {
  fun bind() {
    bind.text.text = bind.root.context.getString(R.string.import_remote_header)
    bind.text.typeface = Typeface.DEFAULT_BOLD
  }


  companion object {
    fun from(parent: ViewGroup): ImportRemoteHeader {
      val bind =
        ListItemImportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
      return ImportRemoteHeader(bind)
    }
  }
}