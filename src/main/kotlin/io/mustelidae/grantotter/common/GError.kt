package io.mustelidae.grantotter.common

class GError(
    private val code: ErrorCode,
    private val cause: String? = null
) : Error {
    override fun getCode(): String = code.name
    override fun getMessage(): String? {
        var message = code.desc
        cause?.let {
            message += " cause by $it"
        }
        return message
    }
}
