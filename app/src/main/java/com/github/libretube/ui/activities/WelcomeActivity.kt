package dev.jch0029987.libretibs.ui.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import dev.jch0029987.libretibs.databinding.ActivityWelcomeBinding
import dev.jch0029987.libretibs.ui.adapters.InstancesAdapter
import dev.jch0029987.libretibs.ui.base.BaseActivity
import dev.jch0029987.libretibs.ui.models.WelcomeViewModel
import dev.jch0029987.libretibs.ui.preferences.BackupRestoreSettings

class WelcomeActivity : BaseActivity() {

    private val viewModel by viewModels<WelcomeViewModel> { WelcomeViewModel.Factory }

    private val restoreFilePicker =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri == null) return@registerForActivityResult
            viewModel.restoreAdvancedBackup(this, uri)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = InstancesAdapter(
            viewModel.uiState.value?.selectedInstanceIndex,
            viewModel::setSelectedInstanceIndex,
        )
        binding.instancesRecycler.adapter = adapter

        binding.okay.setOnClickListener {
            viewModel.onConfirmSettings()
        }

        binding.restore.setOnClickListener {
            restoreFilePicker.launch(BackupRestoreSettings.JSON)
        }

        binding.operationModeGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (checkedId == binding.fullLocalModeToggleGroupButton.id) viewModel.setFullLocalModeEnabled(isChecked)
        }

        viewModel.uiState.observe(this) { (fullLocalMode, selectedIndex, instances, error, navigateToMain) ->
            binding.okay.isEnabled = fullLocalMode || selectedIndex != null
            binding.progress.isGone = instances.isNotEmpty()

            binding.instancesContainer.isVisible = !fullLocalMode
            binding.localModeInfoContainer.isVisible = fullLocalMode

            if (!fullLocalMode) adapter.submitList(instances)

            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                viewModel.onErrorShown()
            }

            navigateToMain?.let {
                val mainActivityIntent = Intent(this, MainActivity::class.java)
                startActivity(mainActivityIntent)
                finish()
                viewModel.onNavigated()
            }
        }
    }

    override fun requestOrientationChange() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER
    }
}
