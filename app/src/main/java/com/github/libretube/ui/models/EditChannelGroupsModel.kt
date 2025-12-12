package dev.jch0029987.libretibs.ui.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.jch0029987.libretibs.db.obj.SubscriptionGroup

class EditChannelGroupsModel : ViewModel() {
    val groups = MutableLiveData<List<SubscriptionGroup>>()
    var groupToEdit: SubscriptionGroup? = null
}
