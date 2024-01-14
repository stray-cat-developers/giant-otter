package io.mustelidae.grantotter.domain.spec.controlller

import io.mustelidae.grantotter.domain.spec.SwaggerSpec
import io.swagger.v3.oas.annotations.media.Schema
import org.hibernate.validator.constraints.URL
import java.time.LocalDateTime

class SwaggerSpecResources {

    @Schema(name = "GrantOtter.SwaggerSpec.Request")
    class Request(
        val type: SwaggerSpec.Type,
        val group: String,
        val name: String,
        @field:URL
        val url: String,
        val version: String,
        val description: String? = null,
        val headers: Map<String, Any>? = null,
        val tags: List<String>? = null,
    )

    @Schema(name = "GrantOtter.SwaggerSpec.Reply")
    data class Reply(
        val id: String,
        val createdAt: LocalDateTime,
        val name: String,
        val type: SwaggerSpec.Type,
        val url: String,
        val version: String,
        val description: String? = null,
        val headers: Map<String, Any>? = null,
        val tags: List<String>? = null,
    ) {
        companion object {
            fun from(swaggerSpec: SwaggerSpec): Reply {
                return swaggerSpec.run {
                    Reply(
                        id.toString(),
                        createdAt,
                        name,
                        type,
                        url,
                        version,
                        description,
                        headers,
                        tags,
                    )
                }
            }
        }
    }
}
