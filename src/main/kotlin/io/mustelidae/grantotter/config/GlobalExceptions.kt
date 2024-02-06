package io.mustelidae.grantotter.config

import io.mustelidae.grantotter.common.ErrorCode
import io.mustelidae.grantotter.common.ErrorSource
import io.mustelidae.grantotter.common.NormalError

open class HumanException(val error: ErrorSource) : RuntimeException(error.message)

class DataNotFoundException(cause: String) : HumanException(NormalError(ErrorCode.H000, cause))

class CommunicationException(val error: ErrorSource) : RuntimeException(error.message)
