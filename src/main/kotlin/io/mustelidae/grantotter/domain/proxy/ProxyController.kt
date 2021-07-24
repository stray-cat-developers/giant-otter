package io.mustelidae.grantotter.domain.proxy

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Method
import io.mustelidae.grantotter.utils.Jackson
import java.lang.Exception
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.util.MultiValueMap
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import springfox.documentation.annotations.ApiIgnore

@ApiIgnore
@RestController
class ProxyController {

    @RequestMapping("swagger-proxy")
    fun proxy(
        @RequestParam method: String,
        @RequestParam url: String,
        @RequestHeader headers: MultiValueMap<String, String>,
        @RequestBody(required = false) body: Map<*, *>?
    ): ResponseEntity<Any?> {
        val request = Fuel.request(Method.valueOf(method), url)
            .apply {
                // set request body
                if (body != null)
                    this.body(Jackson.getMapper().writeValueAsString(body))
                // set request header
                headers.remove(HttpHeaders.ORIGIN)
                header(headers)
            }

        // execute request and get response
        val (_, res) = request.responseString()

        // parse response
        // endpoint is not available if statusCode is not parsable.
        val responseStatus = try { HttpStatus.valueOf(res.statusCode) } catch (e: Exception) { HttpStatus.SERVICE_UNAVAILABLE }
        val responseHeaders =
            HttpHeaders().apply { setAll(res.headers.map { it.key to it.value.joinToString(",") }.toMap()) }
        val responseBody = if (res.body().isEmpty().not()) {
            try {
                Jackson.getMapper().readValue(res.body().toByteArray(), Any::class.java)
            } catch (e: Exception) {
                String(res.body().toByteArray())
            }
        } else
            null

        // return response
        return ResponseEntity(responseBody, responseHeaders, responseStatus)
    }
}
