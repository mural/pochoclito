package com.mural.domain

data class Video(
    val videoId: String,
    var ownerId: Long,
    val name: String? = "",
    val site: String? = "",
    val key: String? = ""
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Video -> {
                this.videoId == other.videoId
            }
            else -> false
        }
    }

    override fun hashCode(): Int {
        return videoId.hashCode()
    }
}