package io.mustelidae.grantotter.common

class CError(
    val responseError: String,
    private val cause: String? = null
) : Error {
    override fun getCode(): String = ErrorCode.C000.name
    override fun getMessage(): String? {
        var message = ErrorCode.C000.desc
        cause?.let {
            message += " cause by $it"
        }
        return message
    }
}
