package io.mustelidae.grantotter.config

import io.mustelidae.grantotter.utils.toJson
import jakarta.servlet.FilterChain
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletRequestWrapper
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.util.StreamUtils
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.UUID

class RequestResponseLogFilter : OncePerRequestFilter() {
    private val defaultCharset = Charset.forName("utf-8")

    private val log = LoggerFactory.getLogger(javaClass)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val isAsync = isAsyncDispatch(request)
        val startTime = System.currentTimeMillis()
        val transactionId = UUID.randomUUID().toString()

        if (isSkipUri(request) || isAsync) {
            filterChain.doFilter(request, response)
        } else {
            val multiReadRequest = request as? MultiReadHttpServletRequest ?: MultiReadHttpServletRequest(request)
            val wrappedResponse = response as? ContentCachingResponseWrapper ?: ContentCachingResponseWrapper(response)

            try {
                val message = StringBuilder()
                run {
                    appendTransactionId(message, transactionId)
                    message.append(", ")
                    appendHttpMethod(message, request)
                    message.append(", ")
                    appendUrl(message, "req", request)
                    message.append(", ")
                    appendReqHeader(message, request)

                    if (log.isDebugEnabled) {
                        message.append(", ")
                        appendRequestBody(message, multiReadRequest)
                    }
                }
                log.info(message.toString())

                filterChain.doFilter(multiReadRequest, wrappedResponse)
            } finally {
                val message = StringBuilder()
                run {
                    appendTransactionId(message, transactionId)
                    message.append(", ")
                    appendHttpMethod(message, request)
                    message.append(", ")
                    appendUrl(message, "res", request)
                    message.append(", ")
                    appendStatus(message, wrappedResponse)
                    message.append(", ")
                    appendLatency(message, startTime)

                    if (log.isDebugEnabled) {
                        message.append(", ")
                        appendResponseBody(message, wrappedResponse)
                    }

                    log.info(message.toString())
                }

                wrappedResponse.copyBodyToResponse()
            }
        }
    }

    private fun appendUrl(loggingMessage: StringBuilder, prefix: String, request: HttpServletRequest) {
        loggingMessage.append("transfer=$prefix, uri=${request.requestURI}")
        request.queryString?.let {
            loggingMessage.append('?').append(it)
        }
    }

    private fun appendReqHeader(loggingMessage: StringBuilder, request: HttpServletRequest) {
        val headers = ServletServerHttpRequest(request).headers

        val headerString = try {
            headers.toJson()
        } catch (e: Exception) {
            null
        }

        if (headerString.isNullOrBlank().not()) {
            loggingMessage.append("headers=$headerString")
        }
    }

    private fun appendHttpMethod(loggingMessage: StringBuilder, request: HttpServletRequest) {
        loggingMessage.append("method=${request.method}, ")
        loggingMessage.append("contentType=${request.contentType}, ")
        loggingMessage.append("encoding=${request.characterEncoding}")
    }

    private fun appendTransactionId(loggingMessage: StringBuilder, transactionId: String) {
        loggingMessage.append("txId=$transactionId")
    }

    private fun appendRequestBody(loggingMessage: StringBuilder, request: MultiReadHttpServletRequest) {
        val requestBody = try {
            StreamUtils.copyToString(request.inputStream, defaultCharset).trimIndent()
        } catch (e: IOException) {
            """{ "error": "Failed to read request body.", "cause": "${e.message}" }""".trimIndent()
        }

        loggingMessage.append("payload-request=${PrivacyLogFilter.masking(requestBody)}")
    }

    private fun appendStatus(loggingMessage: StringBuilder, wrappedResponse: ContentCachingResponseWrapper) {
        loggingMessage.append("status=${wrappedResponse.status}")
    }

    private fun appendLatency(loggingMessage: StringBuilder, startTime: Long) {
        loggingMessage.append("latency=").append(System.currentTimeMillis() - startTime).append("ms")
    }

    private fun appendResponseBody(loggingMessage: StringBuilder, wrappedResponse: ContentCachingResponseWrapper) {
        val responseBody = wrappedResponse.contentAsByteArray.toString(defaultCharset)
        loggingMessage.append("payload-response=$responseBody")
    }

    private fun isSkipUri(request: HttpServletRequest): Boolean {
        val uri = request.requestURI

        return (
            uri.startsWith("/health") ||
                uri.startsWith("/favicon.ico") ||
                uri.startsWith("/h2-console") ||
                uri.startsWith("/actuator") ||
                uri.startsWith("/webjars") ||
                uri.startsWith("/swagger-ui") ||
                uri.startsWith("/v3/api-docs") ||
                uri.startsWith("/v2/api-docs")
            )
    }
}

internal class MultiReadHttpServletRequest(request: HttpServletRequest) : HttpServletRequestWrapper(request) {
    private var cachedBytes: ByteArray? = null

    @Throws(IOException::class)
    override fun getInputStream(): ServletInputStream {
        if (cachedBytes == null) {
            val out = ByteArrayOutputStream()

            StreamUtils.copy(super.getInputStream(), out)
            cachedBytes = out.toByteArray()
        }

        return CachedServletInputStream()
    }

    @Throws(IOException::class)
    override fun getReader(): BufferedReader {
        return BufferedReader(InputStreamReader(inputStream))
    }

    inner class CachedServletInputStream : ServletInputStream() {

        override fun isReady(): Boolean = true
        override fun isFinished(): Boolean = true
        override fun setReadListener(listener: ReadListener) {
            // NOSONAR
        }

        private val input: ByteArrayInputStream = ByteArrayInputStream(cachedBytes)

        @Throws(IOException::class)
        override fun read(): Int {
            return input.read()
        }
    }
}

private object PrivacyLogFilter {
    private val logger = LoggerFactory.getLogger(PrivacyLogFilter::class.java)

    private const val STRING_PATTERN = "\"%s\"\\s*:\\s*\"([^\"]+)\",?"
    private const val NUMBER_PATTERN = "\"%s\"\\s*:\\s*([0-9]+)"
    private val privacyTargetPatterns = listOf(
        STRING_PATTERN.format("latitude").toRegex(),
        STRING_PATTERN.format("address").toRegex(),
        STRING_PATTERN.format("phone").toRegex(),
        STRING_PATTERN.format("plateNumber").toRegex(),
        NUMBER_PATTERN.format("userId").toRegex(),
        NUMBER_PATTERN.format("latitude").toRegex(),
        NUMBER_PATTERN.format("longitude").toRegex(),
    )

    fun masking(input: String): String {
        return try {
            var replace = input
            for (pattern in privacyTargetPatterns) {
                val matchResults = pattern.findAll(replace)
                for (matchResult in matchResults) {
                    val range = matchResult.groups.last()!!.range
                    val size = (range.last - range.first) + 1
                    replace = replace.replaceRange(range, "*".repeat(size))
                }
            }
            replace
        } catch (e: Exception) {
            logger.error("", e)
            "Privacy Masking Error"
        }
    }
}
