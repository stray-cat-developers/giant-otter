package io.mustelidae.grantotter.domain

import io.mustelidae.grantotter.domain.spec.SwaggerSpec
import io.mustelidae.grantotter.domain.spec.controlller.SwaggerSpecResources

object ResourceFixture {
    fun aSwaggerSpecRequest(port: Int): SwaggerSpecResources.Request {
        return SwaggerSpecResources.Request(
            SwaggerSpec.Type.JSON,
            "TEST",
            "Grantotter",
            "http://localhost:$port/v3/api-docs",
            "3.0.1",
            "Grantotter Swagger Json Specification",
            mapOf("x-authentication" to "test"),
        )
    }

    fun aSwaggerSpecInvalidRequest(): SwaggerSpecResources.Request {
        return SwaggerSpecResources.Request(
            SwaggerSpec.Type.JSON,
            "TEST",
            "Grantotter",
            "htt://localhos/v2/api",
            "2.0",
            "Grantotter Swagger Json Specification",
            mapOf("x-authentication" to "test"),
        )
    }
}
