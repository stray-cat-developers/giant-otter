package io.mustelidae.grantotter.common

interface Error {
    fun getCode(): String
    fun getMessage(): String?
}
