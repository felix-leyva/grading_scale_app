package de.felixlf.gradingscale.ui.weightedcalculator

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import de.felixlf.gradingscale.R
import de.felixlf.gradingscale.databinding.WeightedGradeCalcFragmentBinding
import de.felixlf.gradingscale.ui.onSelectedTextChanged
import de.felixlf.gradingscale.ui.onTouchTI
import de.felixlf.gradingscale.ui.round
import de.felixlf.gradingscale.ui.setListNames
import de.felixlf.gradingscale.ui.weightedcalculator.recyclerview.WGAdapter
import de.felixlf.gradingscale.utils.openHelpVideo
import de.felixlf.gradingscale.utils.setLocString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class WeightedGradeCalcFragment : Fragment() {

  val vM: WeightedGradeCalcViewModel by navGraphViewModels(R.id.weighted_grade) { defaultViewModelProviderFactory }
  private var _bind: WeightedGradeCalcFragmentBinding? = null
  private val bind get() = _bind!!
  private lateinit var touchElement: StateFlow<Int>

  private lateinit var adapter: WGAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _bind = WeightedGradeCalcFragmentBinding.inflate(inflater, container, false)
    setupRecyclerViewAdapter()
    declareFlowBindings()
    setupTotalWeightGradeColumn()
    setupFAB()
    return bind.root
  }

  private fun setupTotalWeightGradeColumn() {
    vM.resultWeightedGradeLD.observe(viewLifecycleOwner) {
      bind.totalWeightedGrade.setText(it.gradeName)
      bind.percentWeightedGrade.setText(it.percentage.round(1).setLocString())
      bind.points.setText(it.points.round(1).setLocString())
      bind.totalPoints.setText(it.totalPoints.round(1).setLocString())
    }
  }


  private fun setupRecyclerViewAdapter() {
    bind.weightedGradeRecyclerView.layoutManager = LinearLayoutManager(this.context)

    adapter = WGAdapter(findNavController())
    bind.weightedGradeRecyclerView.adapter = adapter
    setupSelectors()
    vM.wGListItemLD.observe(viewLifecycleOwner, adapter::submitList)
  }

  private fun setupSelectors() {
    vM.gradeScalesNamesLD.observe(viewLifecycleOwner) { listGSNames ->
      bind.gradescalePar.setListNames(listGSNames)
      if (bind.gradescale.text.toString() == "" && listGSNames.isNotEmpty())
        bind.gradescale.setText(listGSNames[0], false)
    }
  }

  private fun declareFlowBindings() = viewLifecycleOwner.lifecycleScope.launch {
    touchElement = flowOf(
      bind.gradescale.onTouchTI()
    ).flattenMerge()
      .stateIn(this, SharingStarted.WhileSubscribed(), bind.gradescale.id)

    bind.gradescale.onSelectedTextChanged(touchElement)
      .onEach { vM.selectGradeScaleWithName(it) }.launchIn(this)

  }

  private fun setupFAB() {
    bind.fab.setOnClickListener {
      val directions = WeightedGradeCalcFragmentDirections.actionWeightedGradeCalcFragmentToWGradeAddModifyDialog()
      directions.edit = false
      findNavController().navigate(directions)
    }
  }


  override fun onDestroy() {
    super.onDestroy()
    _bind = null
  }
  override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
    menuInflater.inflate(R.menu.help_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.help -> openHelpVideo(getString(R.string.link_video_w_calc))
      else -> return false
    }
    return true
  }


}