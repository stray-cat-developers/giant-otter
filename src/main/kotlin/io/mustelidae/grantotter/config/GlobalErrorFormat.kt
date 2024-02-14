package io.mustelidae.grantotter.config

import io.swagger.v3.oas.annotations.media.Schema

@Schema(name = "GrantOtter.Common.Error", description = "API 기본 오류 포멧")
data class GlobalErrorFormat(
    val timestamp: String,
    val code: String,
    val description: String? = null,
    val message: String,
    val type: String,
    val refCode: String? = null,
    val trace: String? = null,
)
