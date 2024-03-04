package io.mustelidae.grantotter.domain.client

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import io.mustelidae.grantotter.common.ErrorCode
import io.mustelidae.grantotter.common.NormalError
import io.mustelidae.grantotter.config.CommunicationException
import io.mustelidae.grantotter.utils.RestClientSupport
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.slf4j.LoggerFactory
import java.net.URI

class SpecStableClient(
    private val restClient: CloseableHttpClient,
    objectMapper: ObjectMapper,
    logging: Boolean,
) : RestClientSupport(
    objectMapper,
    logging,
    LoggerFactory.getLogger(SpecStableClient::class.java),
),
    SpecClient {
    override fun hasSpec(uri: URI): Boolean {
        return try {
            this.getSpec(uri, null)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun getSpec(uri: URI, headers: Map<String, Any>?): String {
        val requestHeaders = mutableListOf<Pair<String, Any>>().apply {
            Pair("Content-Type", "application/json")
        }
        headers?.let {
            requestHeaders.addAll(it.toList().toMutableList())
        }

        val result = restClient.get(uri.toString(), requestHeaders)
            .orElseThrow()

        try {
            objectMapper.readTree(result)
        } catch (e: JsonProcessingException) {
            throw CommunicationException(NormalError(ErrorCode.C001, "The Open API Spec format(JSON) is incorrect."))
        }

        return result
    }
}
