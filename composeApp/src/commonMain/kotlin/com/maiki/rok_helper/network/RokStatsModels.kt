package com.maiki.rok_helper.network

import kotlinx.serialization.Serializable

@Serializable
data class GovernorResponse(
    val success: Boolean,
    val data: GovernorData? = null,
    val meta: Meta? = null
)

@Serializable
data class GovernorData(
    val playerId: Long,
    val playerName: String,
    val kingdom: Int,
    val power: Long,
    val killPoints: Long,
    val guildName: String? = null,
    val guildAbbreviation: String? = null,
    val avatar: AvatarData? = null
) {
    val avatarUrl: String? get() = avatar?.url
    val avatarFrameUrl: String? get() = avatar?.frame
}

@Serializable
data class AvatarData(
    val url: String? = null,
    val frame: String? = null
)

@Serializable
data class Meta(val snapshotTime: String? = null)
