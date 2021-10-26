package de.felixlf.gradingscale.ui.calculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import de.felixlf.gradingscale.R
import de.felixlf.gradingscale.databinding.CalculatorFragmentBinding
import de.felixlf.gradingscale.ui.*
import de.felixlf.gradingscale.utils.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


@ExperimentalCoroutinesApi
@FlowPreview
@AndroidEntryPoint
class CalculatorFragment : Fragment() {

  val model: CalculatorFragmentViewModel by viewModels()
  private var _cF: CalculatorFragmentBinding? = null
  private val cF get() = _cF!!
  private lateinit var touchElement: StateFlow<Int>


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  @SuppressLint("SetTextI18n")
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _cF = CalculatorFragmentBinding.inflate(inflater, container, false)

    setLocaleEditTexts()
    setupGradeScaleSelectionSpinner()
    setupGradeSpinnersWithGradeScaleLiveData()
    setupLiveDataObserversForEditTextAndSpinners()
    declareFlowBindings()
    restoreOnSaveInstanceValues(cF, savedInstanceState)
    setupFAB()

    cF.percentage.setText("100")
    return cF.root
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    _cF?.let { outState.putString("percentage", it.percentage.text.toString().noCommas()) }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _cF = null
  }

  private fun setLocaleEditTexts() {
    cF.percentage.decimalLocale()
    cF.points.decimalLocale()
    cF.totalPoints.decimalLocale()
  }

  private fun setupGradeScaleSelectionSpinner() {
    model.gradeScalesNamesLD.observe(viewLifecycleOwner) { listGSNames ->
      cF.gradeScaleSelectorPar.setListNames(listGSNames)
      if (cF.gradeScaleSelector.text.toString() == "" && listGSNames.isNotEmpty()) {
        cF.gradeScaleSelector.setText(listGSNames[0], false)
      }
    }
  }

  private fun setupGradeSpinnersWithGradeScaleLiveData() {
    model.selectedGradeInScaleLD.observe(viewLifecycleOwner) { gradeInScale ->
      cF.gradeSelectorPar.setListNames(gradeInScale.gradesNamesList())
    }
  }

  private fun setupLiveDataObserversForEditTextAndSpinners() {
    model.totalPointsLD.observe(viewLifecycleOwner) { totalPoints ->
      if (touchElement.value != cF.totalPoints.id)
        cF.totalPoints.setText(totalPoints.round(1).setLocString())
    }

    model.points.observe(viewLifecycleOwner) { points ->
      if (touchElement.value != cF.points.id)
        cF.points.setText(points.round(1).setLocString())
    }

    model.percentageLD.observe(viewLifecycleOwner) { percentage ->
      if (touchElement.value != cF.percentage.id)
        cF.percentage.setText((percentage * 100).round(2).setLocString())
    }

    model.currentGradeAndScale.observe(viewLifecycleOwner) { (grade, gIS) ->
      if (touchElement.value != cF.gradeSelector.id
        || !gIS.grades.map { it.namedGrade }.contains(cF.gradeSelector.text.toString())
      )
        cF.gradeSelector.setText(grade.namedGrade, false)
    }
  }

  @SuppressLint("SetTextI18n")
  private fun declareFlowBindings() {
    viewLifecycleOwner.lifecycleScope.launch {
      val textDebounce = 400L

      touchElement = flowOf(
        cF.gradeSelector.onTouchTI(),
        cF.totalPoints.onTouch(),
        cF.points.onTouch(),
        cF.percentage.onTouch()
      ).flattenMerge()
        .stateIn(this, SharingStarted.WhileSubscribed(), cF.percentage.id)


      cF.gradeScaleSelector.onTyped().onEach {
        val percentageText = cF.percentage.text.toString().noCommas()
        delay(textDebounce)
        model.setGradeScaleInPos(it)
        setGradeByPercentage(percentageText)
      }.launchIn(this)

      cF.gradeSelector.onSelectedTextChanged(touchElement)
        .debounce(textDebounce)
        .onEach { model.setGradeByName(it) }.launchIn(this)

      cF.percentage
        .onSelectedTextChanged(touchElement)
        .debounce(textDebounce).notEmptyNoCommas().onEach {
          setGradeByPercentage(it)
        }.launchIn(this)

      cF.totalPoints
        .onSelectedTextChanged(touchElement)
        .debounce(textDebounce + 400).notEmptyNoCommas().onEach {
          when {
            it.isEmpty() -> cF.totalPoints.setText("100")
            it.nonValidDecimal() -> {}
            else -> model.setGradeByTotalPoints(it)
          }
        }.launchIn(this)

      cF.points
        .onSelectedTextChanged(touchElement)
        .debounce(textDebounce).notEmptyNoCommas().onEach {
          val totalPoints = cF.totalPoints.text.toString().noCommas()
          when {
            it.nonValidDecimal() -> {}
            it.isEmpty() or totalPoints.isEmpty() -> cF.points.setText("")
            it.toDouble()  > totalPoints.toDouble() -> cF.points.setText(totalPoints)
            else -> model.setGradeByPoints(it)
          }
        }.launchIn(this)

    }

  }

  private fun setGradeByPercentage(it: String) {
    when {
      it.nonValidDecimal() -> { }
      it.isEmpty() -> cF.percentage.setText("")
      it.toDouble() > 100.0 -> cF.percentage.setText("100")
      else -> model.setGradeByPercentage(it)
    }
  }

  private fun restoreOnSaveInstanceValues(
    cF: CalculatorFragmentBinding,
    savedInstanceState: Bundle?
  ) {
    if (savedInstanceState != null) {
      cF.percentage.setText(savedInstanceState.getString("percentage", "100"))
    }
  }

  private fun setupFAB() {
    cF.fab.setOnClickListener {
      val directions = CalculatorFragmentDirections.actionCalculatorFragmentToImportRemoteFragment()
      findNavController().navigate(directions)
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
    menuInflater.inflate(R.menu.help_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.help -> openHelpVideo(getString(R.string.link_video_calculator))
      else -> return false
    }
    return true
  }



}








