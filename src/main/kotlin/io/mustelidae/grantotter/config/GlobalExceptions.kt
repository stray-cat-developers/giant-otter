package io.mustelidae.grantotter.config

import io.mustelidae.grantotter.common.Error
import io.mustelidae.grantotter.common.ErrorCode
import io.mustelidae.grantotter.common.GError

open class HumanException(val error: Error) : RuntimeException(error.getMessage())

class DataNotFoundException(cause: String) : HumanException(GError(ErrorCode.H000, cause))

class CommunicationException(val error: Error) : RuntimeException(error.getMessage())
