package dev.jch0029987.libretibs.api

class LocalStreamsExtractionPipedMediaServiceRepository: PipedMediaServiceRepository() {
    private val newPipeDelegate = NewPipeMediaServiceRepository()

    override suspend fun getStreams(videoId: String) = newPipeDelegate.getStreams(videoId)
}