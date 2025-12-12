package dev.jch0029987.libretibs.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.jch0029987.libretibs.constants.IntentData
import dev.jch0029987.libretibs.services.DownloadService
import dev.jch0029987.libretibs.ui.activities.MainActivity

class DownloadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val activityIntent = Intent(context, MainActivity::class.java)

        when (intent?.action) {
            DownloadService.ACTION_SERVICE_STARTED -> {
                activityIntent.putExtra(IntentData.downloading, true)
            }

            DownloadService.ACTION_SERVICE_STOPPED -> {
                activityIntent.putExtra(IntentData.downloading, false)
            }
        }
        context?.startActivity(activityIntent)
    }
}
