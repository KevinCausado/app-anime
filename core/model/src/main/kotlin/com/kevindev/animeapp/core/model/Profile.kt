package com.kevindev.animeapp.core.model

data class Profile(
    val id: Long,
    val name: String,
    val avatarPath: String?,
    val createdAt: Long,
    val isDefault: Boolean,
)
