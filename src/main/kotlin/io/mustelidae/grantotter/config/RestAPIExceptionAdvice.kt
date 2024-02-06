package io.mustelidae.grantotter.config

import io.mustelidae.grantotter.common.ErrorCode
import io.mustelidae.grantotter.common.ErrorSource
import io.mustelidae.grantotter.common.NormalError
import io.mustelidae.grantotter.utils.Jackson
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes
import org.springframework.core.env.Environment
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.request.ServletWebRequest

@ControllerAdvice(annotations = [RestController::class])
class RestAPIExceptionAdvice(
    private val env: Environment,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(value = [RuntimeException::class, IllegalStateException::class])
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    fun handleGlobalException(e: RuntimeException, request: HttpServletRequest): GlobalErrorFormat {
        log.error("Unexpected error", e)
        return errorForm(request, e, NormalError(ErrorCode.S000, e.message))
    }

    @ExceptionHandler(value = [HumanException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleHumanException(e: HumanException, request: HttpServletRequest): GlobalErrorFormat {
        return errorForm(request, e, e.error)
    }

    @ExceptionHandler(value = [IllegalArgumentException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleIllegalArgumentException(e: IllegalArgumentException, request: HttpServletRequest): GlobalErrorFormat {
        return errorForm(request, e, NormalError(ErrorCode.H001, e.message))
    }

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    fun handleMethodArgumentNotValidException(
        e: MethodArgumentNotValidException,
        request: HttpServletRequest,
    ): GlobalErrorFormat {
        return errorForm(
            request,
            e,
            NormalError(
                ErrorCode.H001,
                e.bindingResult.fieldError?.defaultMessage,
            ),
        )
    }

    /**
     * Default error format
     */
    private fun errorForm(request: HttpServletRequest, e: Exception, error: ErrorSource): GlobalErrorFormat {
        val errorAttributeOptions = if (env.activeProfiles.contains("prod").not()) {
            ErrorAttributeOptions.of(ErrorAttributeOptions.Include.STACK_TRACE)
        } else {
            ErrorAttributeOptions.defaults()
        }

        val errorAttributes =
            DefaultErrorAttributes().getErrorAttributes(ServletWebRequest(request), errorAttributeOptions)

        errorAttributes.apply {
            this["message"] = error.message
            this["code"] = error.code
            this["type"] = e.javaClass.simpleName
        }

        return Jackson.getMapper().convertValue(errorAttributes, GlobalErrorFormat::class.java)
    }
}
