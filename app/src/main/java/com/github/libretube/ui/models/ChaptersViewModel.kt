package dev.jch0029987.libretibs.ui.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.jch0029987.libretibs.api.obj.ChapterSegment

class ChaptersViewModel: ViewModel() {
    val chaptersLiveData = MutableLiveData<List<ChapterSegment>>()
    val chapters get() = chaptersLiveData.value.orEmpty()
    val currentChapterIndex = MutableLiveData<Int>()
    var maxSheetHeightPx = 0
}