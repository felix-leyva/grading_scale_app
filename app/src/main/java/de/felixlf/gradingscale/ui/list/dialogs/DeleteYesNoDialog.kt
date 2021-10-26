package de.felixlf.gradingscale.ui.list.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.navigation.navGraphViewModels
import de.felixlf.gradingscale.R
import de.felixlf.gradingscale.ui.list.GSViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DeleteYesNoDialog : DialogFragment() {

    private val gsViewModel: GSViewModel by navGraphViewModels(R.id.list_grades) { defaultViewModelProviderFactory }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return parentFragment?.context.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(getString(R.string.delete_grade_scale_question))
                .setPositiveButton(getString(R.string.delete)) { _: DialogInterface, _: Int ->
                    gsViewModel.removeCurrentGradeScale()
                }
                .setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int ->
                }
                .create()
        }
    }

}