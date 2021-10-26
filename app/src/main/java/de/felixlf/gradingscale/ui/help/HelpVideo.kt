package de.felixlf.gradingscale.ui.help

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.felixlf.gradingscale.R
import de.felixlf.gradingscale.databinding.FragmentHelpVideoBinding
import de.felixlf.gradingscale.utils.openHelpVideo

class HelpVideo : Fragment() {
  var _bind: FragmentHelpVideoBinding? = null
  val bind: FragmentHelpVideoBinding get() = _bind!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    _bind = FragmentHelpVideoBinding.inflate(inflater, container, false)
    setupButtonListeners()
    return bind.root
  }
  override fun onDestroy() {
    super.onDestroy()
    _bind = null
  }

  fun setupButtonListeners() {
    bind.calculatorVideoButton.setOnClickListener { openHelpVideo(getString(R.string.link_video_calculator)) }
    bind.editVideoButton.setOnClickListener { openHelpVideo(getString(R.string.link_video_editor)) }
    bind.wGCalcButton.setOnClickListener { openHelpVideo(getString(R.string.link_video_w_calc)) }
    bind.importButton.setOnClickListener { openHelpVideo(getString(R.string.link_video_import)) }
  }




}