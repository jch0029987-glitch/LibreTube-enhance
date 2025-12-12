package dev.jch0029987.libretibs.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import dev.jch0029987.libretibs.R
import dev.jch0029987.libretibs.helpers.NavigationHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class RequireRestartDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.require_restart)
            .setMessage(R.string.require_restart_message)
            .setPositiveButton(R.string.okay) { _, _ ->
                ActivityCompat.recreate(requireActivity())
                NavigationHelper.restartMainActivity(requireContext())
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
}
