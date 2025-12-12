package dev.jch0029987.libretibs.ui.activities

import android.content.Intent
import android.os.Bundle
import dev.jch0029987.libretibs.constants.IntentData
import dev.jch0029987.libretibs.databinding.ActivityNointernetBinding
import dev.jch0029987.libretibs.helpers.NavigationHelper
import dev.jch0029987.libretibs.ui.base.BaseActivity

class NoInternetActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityNointernetBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (intent.getBooleanExtra(IntentData.maximizePlayer, false)) {
            NavigationHelper.openAudioPlayerFragment(this, offlinePlayer = true)
        }
    }
}
