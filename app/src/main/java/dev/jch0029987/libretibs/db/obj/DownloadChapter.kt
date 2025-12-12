package dev.jch0029987.libretibs.db.obj

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.jch0029987.libretibs.api.obj.ChapterSegment

@Entity(tableName = "downloadChapters")
data class DownloadChapter(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val videoId: String,
    val name: String,
    val start: Long,
    val thumbnailUrl: String
) {
    fun toChapterSegment(): ChapterSegment {
        return ChapterSegment(name, thumbnailUrl, start)
    }
}