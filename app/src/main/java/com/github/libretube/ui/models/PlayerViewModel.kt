package dev.jch0029987.libretibs.ui.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.media3.common.util.UnstableApi
import dev.jch0029987.libretibs.api.obj.Segment
import dev.jch0029987.libretibs.api.obj.Subtitle
import dev.jch0029987.libretibs.helpers.PlayerHelper

@UnstableApi
class PlayerViewModel : ViewModel() {

    var segments = MutableLiveData<List<Segment>>()
    // this is only used to restore the subtitle after leaving PiP, the actual caption state
    // should always be read from the player's selected tracks!
    var currentSubtitle = Subtitle(code = PlayerHelper.defaultSubtitleCode)
    var sponsorBlockConfig = PlayerHelper.getSponsorBlockCategories()

    /**
     * Whether an orientation change is in progress, so that the current player should be continued to use
     *
     * Set to true if the activity will be recreated due to an orientation change
     */
    var isOrientationChangeInProgress = false
}