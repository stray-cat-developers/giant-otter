package io.mustelidae.grantotter.common

class NormalError : ErrorSource {

    constructor(errorCode: ErrorCode, message: String? = null, causeBy: Map<String, Any?>? = null) {
        this.message = message ?: errorCode.desc
        this.causeBy = causeBy
        this.code = errorCode.name
    }

    override val message: String?
    override var causeBy: Map<String, Any?>?
    override val code: String
    override var refCode: String? = null
}
