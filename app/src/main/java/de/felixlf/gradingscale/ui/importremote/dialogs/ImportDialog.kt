package de.felixlf.gradingscale.ui.importremote.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import de.felixlf.gradingscale.R
import de.felixlf.gradingscale.data.remote.LoadingStatus
import de.felixlf.gradingscale.databinding.DialogImportRemoteBinding
import de.felixlf.gradingscale.ui.importremote.ImportRemoteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class ImportDialog : DialogFragment() {
  private val vM: ImportRemoteViewModel by navGraphViewModels(R.id.importRemote) { defaultViewModelProviderFactory }
  private var _dG: DialogImportRemoteBinding? = null
  private val dG get() = _dG!!


  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    _dG = DialogImportRemoteBinding.inflate(requireParentFragment().layoutInflater)
    val builder = AlertDialog.Builder(requireParentFragment().context)
    val dialog = builder.setView(dG.root)

    setupRecyclerView(dialog)
    setupButtons(dialog)
    setupSize()
    return dialog.create()
  }

  override fun onDestroy() {
    super.onDestroy()
    _dG = null
  }

  @SuppressLint("SetTextI18n")
  private fun setupRecyclerView(dialog: AlertDialog.Builder) {
    dG.recyclerView.layoutManager = LinearLayoutManager(dialog.context)
    val adapter = ImportDialogAdaptor()
    dG.recyclerView.adapter = adapter


    lifecycleScope.launch() {
      vM.selectedGradeAndScale.collectLatest {
        loadingStatus(false)
        errorStatus(false)

        when (it.gradeScaleName) {
          LoadingStatus.LOADING.name -> loadingStatus(true)
          LoadingStatus.ERROR_CONNECTION.name -> errorStatus(true)
          else -> {
            dG.nameOfScaleTV.text = "${it.country}: ${it.gradeScaleName}"
            adapter.submitList(it.grades)
          }
        }
      }
    }
  }

  private fun loadingStatus(activated: Boolean) {
    when(activated) {
      true -> {
        dG.progressBar.visibility = VISIBLE
        dG.nameOfScaleTV.text = getString(R.string.loading)
      }
      else -> {
        dG.progressBar.visibility = INVISIBLE
      }
    }
  }

  private fun errorStatus(activated: Boolean) {
    when(activated) {
      true -> {
        dG.errorIcon.visibility = VISIBLE
        dG.nameOfScaleTV.text = getString(R.string.error_loading)
      }
      else -> {
        dG.errorIcon.visibility = INVISIBLE
      }
    }

  }

  private fun setupButtons(dialog: AlertDialog.Builder) {
    dialog.setMessage(R.string.dialog_import)
      .setPositiveButton(R.string.yes) { _, _ ->
        val importAndCheckSuccess = vM.importSelectedGradeAndScale()
        if (importAndCheckSuccess) {
          vM.showSnackBar(getString(R.string.dialog_import_success))
        } else {
          vM.showSnackBar(getString(R.string.dialog_import_fail))
        }
      }
      .setNegativeButton(R.string.no) { _, _ ->
        vM.showSnackBar(getString(R.string.dialog_import_fail))
      }

  }
  private fun setupSize(){

    lifecycleScope.launch(Dispatchers.Main) {
      delay(500)
      parentFragment?.view?.height?.let { rootHeight ->
        val params = dG.recyclerView.layoutParams
        params.height = (rootHeight * .5).roundToInt()
        dG.recyclerView.layoutParams = params
      }
    }

  }
}