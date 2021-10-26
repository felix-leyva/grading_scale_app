package de.felixlf.gradingscale.ui.weightedcalculator.dialogWGrade

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import dagger.hilt.android.AndroidEntryPoint
import de.felixlf.gradingscale.R
import de.felixlf.gradingscale.databinding.WgradeDialogBinding
import de.felixlf.gradingscale.model.GradeScale
import de.felixlf.gradingscale.model.GradesInScale
import de.felixlf.gradingscale.model.WeightedGrade
import de.felixlf.gradingscale.ui.*
import de.felixlf.gradingscale.ui.weightedcalculator.WeightedGradeCalcViewModel
import de.felixlf.gradingscale.utils.decimalLocale
import de.felixlf.gradingscale.utils.notEmptyNoCommas
import de.felixlf.gradingscale.utils.setDouble
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class WGradeAddModifyDialog : DialogFragment() {
  private var _bind: WgradeDialogBinding? = null
  private val bind get() = _bind!!

  private var percent: Double = 0.0
  private var totalPoints: Double = 0.0

  private var correctData = false
  private lateinit var touchElement: StateFlow<Int>

  private val vM: WeightedGradeCalcViewModel by navGraphViewModels(R.id.weighted_grade) { defaultViewModelProviderFactory }
  private val args: WGradeAddModifyDialogArgs by navArgs()
  lateinit var weightedGrade: WeightedGrade
  private var currentGiS: GradesInScale =
    GradesInScale(GradeScale(gradeScaleName = ""), mutableListOf())

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    _bind = WgradeDialogBinding.inflate(requireParentFragment().layoutInflater)
    val builder = parentFragment?.context.let { AlertDialog.Builder(it) }
    val dialog = builder.setView(bind.root)

    declareFlowBindings()

    if (args.edit) {
      setCurrentGrade()
      setSaveButton(dialog)
      setDeleteButton(dialog)
    } else {
      setNewGrade()
    }

    setLocalizedEditTexts()
    setSaveNewButton(dialog)
    fillGradeSelector()
    updatePercentage()
    updatePoints()
    updateGrade()
    updateTotalPoints()

    return dialog.create()
  }
  override fun onDestroy() {
    super.onDestroy()
    _bind = null
  }

  private fun setLocalizedEditTexts() {
    bind.points.decimalLocale()
    bind.totalPoints.decimalLocale()
    bind.percent.decimalLocale()
  }

  private fun setCurrentGrade() {
    vM.setWeightedGradeWithId(args.id)
    vM.currentWeightedGradeLD.value?.let {
      weightedGrade = it
      percent = it.percentage
      totalPoints = it.weight
    }
  }

  private fun setNewGrade() {
    val scaleId = vM.currentWCalculationGrade.value.gradeCalculation.id.toString()
    percent = 1.0
    totalPoints = 1.0
    weightedGrade = WeightedGrade(percentage = percent, weight = totalPoints, scaleId = scaleId)
  }

  private fun setSaveButton(dialog: AlertDialog.Builder) {
    dialog.setPositiveButton(getString(R.string.save_button)) { _, _ ->
      if (correctData) {
        weightedGrade.percentage = percent
        weightedGrade.weight = totalPoints
        vM.updateWeightedGrade(weightedGrade)
      }
    }
  }

  private fun setDeleteButton(dialog: AlertDialog.Builder) {
    if (vM.currentWCalculationGrade.value.weightedGrades.size > 1) {
      dialog.setNegativeButton(getString(R.string.delete_grade_button)) { _, _ ->
        vM.deleteWeightedGrade(weightedGrade)
      }
    }
  }

  private fun setSaveNewButton(dialog: AlertDialog.Builder) {
    dialog.setNeutralButton(getString(R.string.save_new_grade_button)) { _, _ ->
      if (correctData) {
        vM.insertWeightedGradeInCurrentCalculation(percent, totalPoints)
      }
    }
  }

  private fun fillGradeSelector() {
    vM.currentGradeInScaleLD.value?.let {
      bind.gradenamePar.setListNames(it.gradesNamesList())
      currentGiS = it
    }
  }

  @FlowPreview
  private fun declareFlowBindings() = lifecycleScope.launch {
    setupIdViewTouchObserverFlow(this)
    setupGradeNameListenerFlow(this)
    setupPercentListenerFlow(this)
    setupTotalPointsListenerFlow(this)
    setupPointsListenerFlow(this)
  }

  private fun setupIdViewTouchObserverFlow(coroutineScope: CoroutineScope) {
    touchElement = flowOf(
      bind.gradeName.onTouchTI(),
      bind.percent.onTouch(),
      bind.totalPoints.onTouch(),
      bind.points.onTouch()
    ).flattenMerge().stateIn(coroutineScope, SharingStarted.WhileSubscribed(), bind.percent.id)
  }

  private fun setupGradeNameListenerFlow(coroutineScope: CoroutineScope) {
    bind.gradeName.onSelectedTextChanged(touchElement).onEach { gradeName ->
      currentGiS.grades.firstOrNull { it.namedGrade == gradeName }?.let { grade ->
        percent = grade.percentage
        updatePercentage()
        updatePoints()
      }
      checkCorrectData()
    }.launchIn(coroutineScope)
  }

  @SuppressLint("SetTextI18n")
  private fun setupPercentListenerFlow(coroutineScope: CoroutineScope) {
    bind.percent.onSelectedTextChanged(touchElement).debounce(400).notEmptyNoCommas().onEach {
      when {
        it.notValid() -> {
          bind.percent.setText(".0")
        }
        it.toDouble() > 100.0 -> {
          bind.percent.setText("100.0")
        }
        else -> {
          percent = it.toDouble() / 100
          updateGrade()
          updatePoints()
        }
      }
      checkCorrectData()
    }.launchIn(coroutineScope)
  }

  private fun setupPointsListenerFlow(coroutineScope: CoroutineScope) {
    bind.points.onSelectedTextChanged(touchElement).debounce(400).notEmptyNoCommas().onEach {
      when {
        it.notValid() -> {
          bind.points.setText(".0")
        }
        it.toDouble() > totalPoints -> {
          bind.points.setText(totalPoints.round(2).toString())
        }
        else -> {
          percent = it.toDouble() / totalPoints
          updateGrade()
          updatePercentage()
        }
      }
      checkCorrectData()
    }.launchIn(coroutineScope)
  }

  private fun setupTotalPointsListenerFlow(coroutineScope: CoroutineScope) {
    bind.totalPoints.onSelectedTextChanged(touchElement).debounce(400).notEmptyNoCommas().onEach {
      if (it.notValid()) {
        bind.totalPoints.setText(".0")
      } else {
        totalPoints = it.toDouble()
        updatePoints()
        checkCorrectData()
      }
    }.launchIn(coroutineScope)
  }

  private fun updatePercentage() {
    bind.percent.setDouble((percent * 100).round(2))
  }

  private fun updatePoints() {
    bind.points.setDouble((percent * totalPoints).round(2))
  }

  private fun updateGrade() {
    bind.gradeName.setText(currentGiS.gradeByPercentage(percent).namedGrade, false)
  }

  private fun updateTotalPoints() {
    bind.totalPoints.setDouble(totalPoints.round(2))
  }

  private fun checkCorrectData() {
    correctData = (percent > 0.0 && percent <= 100.0 && totalPoints > 0.0)
  }

  fun String.notValid(): Boolean {
    return (isEmpty() || this == ".")
  }

}