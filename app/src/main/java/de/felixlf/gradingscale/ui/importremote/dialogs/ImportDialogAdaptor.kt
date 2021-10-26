package de.felixlf.gradingscale.ui.importremote.dialogs

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.felixlf.gradingscale.databinding.ListItemBinding
import de.felixlf.gradingscale.model.firebase.GradeRemote
import de.felixlf.gradingscale.ui.round
import de.felixlf.gradingscale.utils.setLocString

class ImportDialogAdaptor :
  ListAdapter<GradeRemote, ImportDialogAdaptor.ViewHolder>(ImportDiffUtil) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    return ViewHolder(ListItemBinding.inflate(
      LayoutInflater.from(parent.context),
      parent,
      false)
    )
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = getItem(position)
    if (item != null) {
      holder.bind(item)
    }
  }

  class ViewHolder(val bind: ListItemBinding) : RecyclerView.ViewHolder(bind.root) {

    @SuppressLint("SetTextI18n")
    fun bind(gradeRemote: GradeRemote) {
      bind.text.text = "${gradeRemote.gradeName}:\t\t\t${(gradeRemote.percentage * 100).round(2).setLocString()}%"
    }
  }

}

object ImportDiffUtil : DiffUtil.ItemCallback<GradeRemote>() {
  override fun areItemsTheSame(oldItem: GradeRemote, newItem: GradeRemote) =
    oldItem == newItem

  override fun areContentsTheSame(oldItem: GradeRemote, newItem: GradeRemote) =
    oldItem == newItem
}