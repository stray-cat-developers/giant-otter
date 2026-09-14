package io.mustelidae.grantotter.domain.crawler

import io.mustelidae.grantotter.domain.spec.SwaggerSpec
import org.bson.types.ObjectId
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties.SwaggerUrl
import java.util.concurrent.ConcurrentHashMap

object SwaggerDocCacheStore {
    private val cacheStore: ConcurrentHashMap<String, Pair<SwaggerUrl, String>> = ConcurrentHashMap()

    fun add(swaggerSpec: SwaggerSpec, apiDefinition: String) {
        val group = swaggerSpec.group
        val swaggerUrl = SwaggerUrl(group, "/swagger/specifications/${swaggerSpec.getId()}/docket", swaggerSpec.name)
        cacheStore[swaggerSpec.getId().toString()] = Pair(swaggerUrl, apiDefinition)
    }

    fun findOne(id: ObjectId): Pair<SwaggerUrl, String>? {
        return cacheStore[id.toString()]
    }

    fun findAll(): List<Pair<SwaggerUrl, String>> {
        return cacheStore.map { it.value }
    }

    fun flash() {
        cacheStore.clear()
    }
}
