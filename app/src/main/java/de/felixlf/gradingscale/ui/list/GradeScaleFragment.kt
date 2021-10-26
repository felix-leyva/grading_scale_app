package de.felixlf.gradingscale.ui.list

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import de.felixlf.gradingscale.R
import de.felixlf.gradingscale.databinding.GradeScaleFragmentBinding
import de.felixlf.gradingscale.ui.onTyped
import de.felixlf.gradingscale.ui.onTyping
import de.felixlf.gradingscale.ui.setListNames
import de.felixlf.gradingscale.utils.decimalLocale
import de.felixlf.gradingscale.utils.notEmptyNoCommas
import de.felixlf.gradingscale.utils.openHelpVideo
import de.felixlf.gradingscale.utils.setLocString
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class GradeScaleFragment : Fragment() {

  private var _gSF: GradeScaleFragmentBinding? = null
  private val gSF get() = _gSF!!
  private val vM: GSViewModel by navGraphViewModels(R.id.list_grades) { defaultViewModelProviderFactory }
  private lateinit var adapter: GSAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  @SuppressLint("SetTextI18n")
  @FlowPreview
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _gSF = GradeScaleFragmentBinding.inflate(inflater, container, false)
    setupRecyclerViewGradesLists()
    setupNamesInGradeScaleSpinner()
    setupActionsFromOperationStateLD()
    setupClickListenersWithFlowBindings()
    setupFAB()
    gSF.totalPoints.decimalLocale()
    gSF.totalPoints.setText(50.0.setLocString())
    return gSF.root
  }


  private fun setupRecyclerViewGradesLists() {
    gSF.gradeScaleRecyclerView.layoutManager = LinearLayoutManager(this.context)
    adapter = GSAdapter(vM) //this will be used in other parts
    gSF.gradeScaleRecyclerView.adapter = adapter
    setupRecyclerViewSwipeHelpers()
    vM.selectedGradeListItemsLDF.observe(viewLifecycleOwner, adapter::submitList)
  }

  private fun setupRecyclerViewSwipeHelpers() {
    SwipeDeleteHelper(adapter).let {
      ItemTouchHelper(it).attachToRecyclerView(gSF.gradeScaleRecyclerView)
    }
    SwipeUpdateHelper(adapter).let {
      ItemTouchHelper(it).attachToRecyclerView(gSF.gradeScaleRecyclerView)
    }
  }

  private fun setupNamesInGradeScaleSpinner() {
    vM.gradeScalesNamesLDF.observe(viewLifecycleOwner) { listGSNames ->
      gSF.gradePar.setListNames(listGSNames)
      if (gSF.gradeScale.text.isNullOrEmpty()) gSF.gradeScale.setText(listGSNames[0], false)
    }
  }

  @FlowPreview
  private fun setupClickListenersWithFlowBindings() {
    lifecycleScope.launch {
      gSF.gradeScale.onTyped().onEach { vM.setGradeScaleWithName(it) }.launchIn(this)

      gSF.totalPoints.onTyping().debounce(500).distinctUntilChanged().notEmptyNoCommas().onEach {
        if (it.isNotEmpty()) vM.gsSetTotalPoints(it)
      }.launchIn(this)
    }
  }

  @SuppressLint("NotifyDataSetChanged")
  private fun setupActionsFromOperationStateLD() {
    vM.instructionsLD.observe(viewLifecycleOwner) {
      val operationState = it
      vM.resetInstructions()

      when (operationState) {
        is OperationState.EditGrade -> {
          GradeScaleFragmentDirections
            .actionGradeScaleFragmentToGradeModifyDialogFragment(true)
            .let(findNavController()::navigate)
        }
        is OperationState.AddNewGrade -> {
          GradeScaleFragmentDirections
            .actionGradeScaleFragmentToGradeModifyDialogFragment(false)
            .let(findNavController()::navigate)
        }
        is OperationState.Opened -> {
          adapter.notifyDataSetChanged() //avoids having empty spaces
        }
        is OperationState.DeleteGrade -> {
          showUndoSnackBar()
        }
        is OperationState.AddNewGradeScale -> {
          GradeScaleFragmentDirections.actionGradeScaleFragmentToGradeScaleModFragment().apply {
            modifyGradeScale = false
            findNavController().navigate(this)
          }
        }
        is OperationState.DeleteGradeScale -> {
          GradeScaleFragmentDirections.actionGradeScaleFragmentToDeleteYesNoDialog()
            .let(findNavController()::navigate)
        }
        is OperationState.LoadLastGradeScale ->
          delayedRun { gSF.gradeScale.setText(vM.gradeScalesNamesLDF.value?.last(), false) }

        is OperationState.LoadFirstGradeScale ->
          delayedRun { gSF.gradeScale.setText(vM.gradeScalesNamesLDF.value?.first(), false) }

        is OperationState.UpdateGradeScale -> {
          GradeScaleFragmentDirections.actionGradeScaleFragmentToGradeScaleModFragment().apply {
            modifyGradeScale = true
            findNavController().navigate(this)
          }
        }
        is OperationState.UpdateCurrentGradeScaleName ->
          delayedRun { gSF.gradeScale.setText(operationState.currentGradeScaleName, false) }

        is OperationState.OperationFinished -> { }
      }
    }
  }


  override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
    menuInflater.inflate(R.menu.main_nav, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.help -> openHelpVideo(getString(R.string.link_video_editor))
      R.id.action_add_grade -> vM.dialogNewGrade()
      R.id.action_add_grade_scale -> vM.dialogNewGradeScale()
      R.id.action_modify_grade_scale_name -> vM.dialogModifyGradeScale()
      R.id.delete_grade_scale -> checkAndDeleteGradeScale()
      else -> return false
    }
    return true
  }

  override fun onDestroy() {
    super.onDestroy()
    _gSF = null
  }

  private fun checkAndDeleteGradeScale() = vM.gradeScalesNamesLDF.value?.let {
    if (it.size > 1) {
      vM.dialogDeleteGradeScale()
    } else {
      Toast.makeText(context, getString(R.string.last_gradescale_delete), Toast.LENGTH_SHORT).show()
    }
  }


  private fun showUndoSnackBar() {
    view?.let { Snackbar.make(it, R.string.snack_bar_text, Snackbar.LENGTH_LONG) }
      ?.apply {
        setAction(R.string.snack_bar_undo) { vM.undoDelete() }
        show()
      }
  }

  private fun delayedRun(operation: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch(Dispatchers.Default) {
      delay(100)
      withContext(Dispatchers.Main, operation)
    }
  }

  private fun setupFAB() {
    gSF.fab.setOnClickListener {
      vM.dialogNewGrade()
    }
  }


}