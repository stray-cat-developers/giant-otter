package io.mustelidae.grantotter.domain.spec

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * Swagger Json specification
 */
@Document
class SwaggerSpec(
    val type: Type,
    val group: String,
    val name: String,
    val url: String,
    val version: String,
    val description: String? = null,
    val headers: Map<String, Any>? = null,
    val tags: List<String>? = null,
) {
    @Id
    var id: ObjectId = ObjectId()
        private set
    var createdAt = LocalDateTime.now()!!
        private set

    enum class Type {
        JSON,
        YAML,
    }
}
