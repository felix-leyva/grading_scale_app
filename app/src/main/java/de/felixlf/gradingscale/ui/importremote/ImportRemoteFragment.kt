package de.felixlf.gradingscale.ui.importremote

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import de.felixlf.gradingscale.R
import de.felixlf.gradingscale.databinding.ImportRemoteFragmentBinding
import de.felixlf.gradingscale.ui.importremote.dialogs.ImportDialog
import de.felixlf.gradingscale.ui.importremote.recyclerview.ImportRemoteAdapter
import de.felixlf.gradingscale.ui.importremote.recyclerview.ImportRemoteListItem
import de.felixlf.gradingscale.ui.onTyping
import de.felixlf.gradingscale.ui.setListNames
import de.felixlf.gradingscale.utils.openHelpVideo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class ImportRemoteFragment : Fragment() {

  private val vM: ImportRemoteViewModel by navGraphViewModels(R.id.importRemote) { defaultViewModelProviderFactory }
  private var _bind: ImportRemoteFragmentBinding? = null
  private val bind: ImportRemoteFragmentBinding get() = _bind!!

  private lateinit var adapter: ImportRemoteAdapter

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    _bind = ImportRemoteFragmentBinding.inflate(inflater, container, false)
    setupRecyclerView()
    declareFlowBindings()
    setupSelector()
    setupShowImportSnackbar()
    return bind.root
  }
  override fun onDestroy() {
    super.onDestroy()
    _bind = null
  }

  private fun setupRecyclerView() {
    bind.scalesRecyclerView.layoutManager = LinearLayoutManager(this.context)
    adapter = ImportRemoteAdapter(listItemClickListener())
    bind.scalesRecyclerView.adapter = adapter
    loadingStatus(true)
    vM.listOfCountriesAndGradeScales.observe(viewLifecycleOwner) { listCountriesAndGrades ->
      if (listCountriesAndGrades.isNotEmpty()) {
        adapter.submitList(listCountriesAndGrades)
        loadingStatus(false)
      }

    }
  }

  private fun declareFlowBindings() = viewLifecycleOwner.lifecycleScope.launch {
    bind.country.onTyping().distinctUntilChanged()
      .onEach { vM.selectCountry(it) }.launchIn(this)
  }

  private fun setupSelector() {
    vM.listOfCountries.observe(viewLifecycleOwner) { countries ->
      bind.countryPar.setListNames(countries)
    }
  }

  //https://oozou.com/blog/a-better-way-to-handle-click-action-in-a-recyclerview-item-60
  private fun listItemClickListener(): (ImportRemoteListItem.ImportRemoteItem) -> Unit {
    return { remoteItem ->
      vM.selectGradeAndScale("${remoteItem.country}_${remoteItem.nameScale}")
      ImportDialog().show(parentFragmentManager, "Import")
    }
  }

  private fun setupShowImportSnackbar() {
    vM.snackBarMsg.observe(viewLifecycleOwner) { message ->
      message?.let {
        if (it != "") {
          Toast.makeText(context, it, Toast.LENGTH_LONG).show()
          vM.hideSnackBar()
        }
      }
    }
  }


  private fun loadingStatus(activated: Boolean) {
    when(activated) {
      true -> {
        bind.progressBarF.visibility = View.VISIBLE
      }
      else -> {
        bind.progressBarF.visibility = View.GONE
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
    menuInflater.inflate(R.menu.help_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    when (item.itemId) {
      R.id.help -> openHelpVideo(getString(R.string.link_video_import))
      else -> return false
    }
    return true
  }




}