package de.felixlf.gradingscale.ui.list.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import de.felixlf.gradingscale.R
import de.felixlf.gradingscale.databinding.DialogGradeBinding
import de.felixlf.gradingscale.ui.list.GSViewModel
import de.felixlf.gradingscale.ui.onTyping
import de.felixlf.gradingscale.ui.round
import de.felixlf.gradingscale.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@ExperimentalCoroutinesApi
class GradeModifyDialogFragment : DialogFragment() {

  private var _dG: DialogGradeBinding? = null
  private val dG get() = _dG!!
  private var correctData = true

  private val getPer: Double
    get() = (dG.dialogPercentage.getDoubleOrZero() / 100).round(4)

  private val getName: String
    get() = dG.dialogNamedGrade1.text.toString()

  private var posGradeInList: Int? = null


  private val gsViewModel: GSViewModel by navGraphViewModels(R.id.list_grades) { defaultViewModelProviderFactory }
  val args: GradeModifyDialogFragmentArgs by navArgs()

  @FlowPreview
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

    _dG = DialogGradeBinding.inflate(requireActivity().layoutInflater)
    val builder = activity.let { AlertDialog.Builder(it) }
    val dialog = builder.setView(dG.root)

    setCurrentGradeInEditTextWithLiveData()
    setupButtons(dialog)

    setupRXJavaBindingInTextEdits()
    gsViewModel.indicateDialogOpened()
    dG.dialogPercentage.decimalLocale()

    return dialog.create()
  }
  override fun onDestroy() {
    super.onDestroy()
    _dG = null
  }


  private fun setupButtons(dialog: AlertDialog.Builder) {
    if (args.updateGrade) {
      dialog.setPositiveButton(getString(R.string.save_button)) { _, _ ->
        if (correctData) gsViewModel.updateGrade(getName,  getPer)
      }
      setDeleteButton(dialog)

    } else {
      dG.dialogDescription.text = getString(R.string.new_grade_settings)
    }

    dialog.setNeutralButton(getString(R.string.save_new_grade_button)) { _, _ ->
      if (correctData) gsViewModel.addNewGradeFromCurrent(getName,  getPer)
    }

  }

  private fun setDeleteButton(dialog: AlertDialog.Builder) {
    if (gsViewModel.currentGradeInScale.value.grades.size > 1) {
      dialog.setNegativeButton(getString(R.string.delete_grade_button)) { _, _ ->
        posGradeInList?.let {
          println("TEST: setDeleteButton $it")
          gsViewModel.setGradeToDelete(it+1)
        }
      }
    }
  }

  private fun setCurrentGradeInEditTextWithLiveData() {
    gsViewModel.currentGradeLDF.observe(this) {
      dG.dialogNamedGrade1.setText(it.namedGrade)
      dG.dialogPercentage.setDouble((it.percentage * 100).round())
      posGradeInList = gsViewModel.currentGradeInScale.value.grades.indexOf(it)
    }
  }

  @FlowPreview
  private fun setupRXJavaBindingInTextEdits() {
    setupPercentageEditTextRXBinding()
    setupGradeNameEditTextRXBinding()
  }

  @SuppressLint("ResourceType")
  @FlowPreview
  private fun setupGradeNameEditTextRXBinding() {
    dG.dialogNamedGrade1.onTyping().debounce(100).onEach {
      correctData = if (it.isEmpty()) {
        dG.dialogNamedGrade1.setBackgroundColor(Color.parseColor(getString(R.color.colorAccent)))
        false
      } else {
        dG.dialogNamedGrade1.setBackgroundColor(Color.TRANSPARENT)
        true
      }
    }.launchIn(lifecycleScope)

  }

  @FlowPreview
  private fun setupPercentageEditTextRXBinding() {
    dG.dialogPercentage.onTyping().debounce(100).notEmptyNoCommas().onEach {
      correctData = if (it.nonValidDecimal() || it.toDouble() < 0 || it.toDouble() > 100) {
        dG.dialogPercentage.setBackgroundColor(Color.RED)
        false
      } else {
        dG.dialogPercentage.setBackgroundColor(Color.TRANSPARENT)
        true
      }
    }.launchIn(lifecycleScope)

  }



}
