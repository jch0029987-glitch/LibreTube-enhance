package dev.jch0029987.libretibs.helpers

import dev.jch0029987.libretibs.util.NewPipeDownloaderImpl
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.StreamingService

object NewPipeExtractorInstance {
    val extractor: StreamingService by lazy {
        NewPipe.getService(ServiceList.YouTube.serviceId)
    }

    fun init() {
        NewPipe.init(NewPipeDownloaderImpl())
    }
}