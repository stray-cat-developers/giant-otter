package io.mustelidae.grantotter.domain.spec.controlller

import io.mustelidae.grantotter.domain.spec.SwaggerSpec
import io.swagger.annotations.ApiModel
import java.time.LocalDateTime
import org.hibernate.validator.constraints.URL

class SwaggerSpecResources {

    @ApiModel("SwaggerSpec.Request")
    class Request(
        val type: SwaggerSpec.Type,
        val category: String,
        val name: String,
        @field:URL
        val url: String,
        val version: String,
        val description: String? = null,
        val headers: Map<String, Any>? = null,
        val tags: List<String>? = null
    )

    @ApiModel("SwaggerSpec.Reply")
    data class Reply(
        val id: String,
        val createdAt: LocalDateTime,
        val name: String,
        val type: SwaggerSpec.Type,
        val url: String,
        val version: String,
        val description: String? = null,
        val headers: Map<String, Any>? = null,
        val tags: List<String>? = null
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
                        tags
                    )
                }
            }
        }
    }
}
