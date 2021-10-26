package de.felixlf.gradingscale.ui.list.dialogs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import de.felixlf.gradingscale.R
import de.felixlf.gradingscale.databinding.DialogScaleBinding
import de.felixlf.gradingscale.ui.list.GSViewModel
import de.felixlf.gradingscale.ui.onTyping
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class GradeScaleModFragment : DialogFragment() {

  private var _dS: DialogScaleBinding? = null
  private val dS get() = _dS!!

  private val getName get() = dS.dialogNameScale.text.toString()
  private val gsViewModel: GSViewModel by navGraphViewModels(R.id.list_grades) { defaultViewModelProviderFactory }
  private var correctName = MutableLiveData<Boolean>(false)
  val args: GradeScaleModFragmentArgs by navArgs()


  @SuppressLint("ResourceType")
  @FlowPreview
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

    _dS = DialogScaleBinding.inflate(requireActivity().layoutInflater)
    val builder = activity.let { AlertDialog.Builder(it) }
    val dialog = builder.setView(dS.root)
    checkAndSaveNewGradeScale()


    if (args.modifyGradeScale) {
      dS.dialogNameScalePar.hint = getString(R.string.modify_grade_scale_name_setting)
      dS.dialogNameScale.setText(gsViewModel.currentGradeInScale.value.gradeScale.gradeScaleName)
      dialog.setPositiveButton(getString(R.string.save_button)) { _, which ->
        if (correctName.value == true) gsViewModel.updateGradeScale(this@GradeScaleModFragment.getName)
      }
    } else {

      dS.dialogNameScalePar.hint = getString(R.string.new_grade_scale_settings)
      dialog.setPositiveButton(getString(R.string.save_new_grade_scale_button)) { _, which ->
        if (correctName.value == true) gsViewModel.addNewGradeScale(this@GradeScaleModFragment.getName)
      }
    }

    correctName.observe(this) { nameIsCorrect ->
      if (nameIsCorrect) {
        dS.dialogNameScale.setBackgroundColor(Color.TRANSPARENT)
        dS.dialogNameScale.setTextColor(Color.DKGRAY)

      } else {
        dS.dialogNameScale.setBackgroundColor(Color.parseColor(getString(R.color.colorAccent)))
        dS.dialogNameScale.setTextColor(Color.WHITE)
      }
    }
    return dialog.create()
  }
  override fun onDestroy() {
    super.onDestroy()
    _dS = null
  }


  @FlowPreview
  fun checkAndSaveNewGradeScale() = lifecycleScope.launch {
    dS.dialogNameScale.onTyping().debounce(50).onEach {
      val list = gsViewModel.gradeScalesNames.value
      correctName.value = it.isNotEmpty() && (!list.contains(it) || args.modifyGradeScale)
    }.stateIn(this)
  }


}


