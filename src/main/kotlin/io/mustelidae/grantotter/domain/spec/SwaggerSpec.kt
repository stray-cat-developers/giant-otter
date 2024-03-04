package io.mustelidae.grantotter.domain.spec

import io.mustelidae.grantotter.utils.fromJson
import io.mustelidae.grantotter.utils.toJson
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.bson.types.ObjectId
import java.time.LocalDateTime

/**
 * Swagger Json specification
 */
@Entity
class SwaggerSpec(
    @Enumerated(EnumType.STRING)
    val type: Type,
    @Column(name = "SpecGroup", length = 200)
    val group: String,
    val name: String,
    val url: String,
    val version: String,
    val description: String? = null,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
        private set

    @Column(unique = true)
    private val specId: String = ObjectId().toString()

    var createdAt = LocalDateTime.now()!!
        private set

    private var headers: String? = null

    enum class Type {
        JSON,
        YAML,
    }

    fun getId(): ObjectId = ObjectId(specId)

    fun setHeaders(headers: Map<String, Any>) {
        this.headers = headers.toJson()
    }

    fun getHeader(): Map<String, Any>? {
        return this.headers?.fromJson<Map<String, Any>>()
    }
}
