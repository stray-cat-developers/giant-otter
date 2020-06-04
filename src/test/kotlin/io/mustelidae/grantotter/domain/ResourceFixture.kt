package io.mustelidae.grantotter.domain

import io.mustelidae.grantotter.domain.spec.SwaggerSpec
import io.mustelidae.grantotter.domain.spec.controlller.SwaggerSpecResources

object ResourceFixture {
    fun aSwaggerSpecRequest(port: Int): SwaggerSpecResources.Request {
        return SwaggerSpecResources.Request(
            SwaggerSpec.Type.JSON,
            "TEST",
            "Grantotter",
            "http://localhost:$port/v2/api-docs",
            "2.0",
            "Grantotter Swagger Json Specification",
            mapOf("x-authentication" to "test"),
            listOf("Platform")
        )
    }

    fun aSwaggerSpecInvalidRequest(): SwaggerSpecResources.Request {
        return SwaggerSpecResources.Request(
            SwaggerSpec.Type.JSON,
            "TEST",
            "Grantotter",
            "http://localhost/v2/api",
            "2.0",
            "Grantotter Swagger Json Specification",
            mapOf("x-authentication" to "test"),
            listOf("Platform")
        )
    }
}
