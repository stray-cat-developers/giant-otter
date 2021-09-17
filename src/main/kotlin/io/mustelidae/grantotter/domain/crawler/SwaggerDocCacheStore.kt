package io.mustelidae.grantotter.domain.crawler

import io.mustelidae.grantotter.domain.spec.SwaggerSpec
import org.bson.types.ObjectId
import springfox.documentation.swagger.web.SwaggerResource
import java.util.concurrent.ConcurrentHashMap

object SwaggerDocCacheStore {
    private val cacheStore: ConcurrentHashMap<String, Pair<SwaggerResource, String>> = ConcurrentHashMap()

    fun add(swaggerSpec: SwaggerSpec, apiDefinition: String) {
        val resource = SwaggerResource().apply {
            location = "/swagger/specifications/${swaggerSpec.id}/docket"
            name = swaggerSpec.name
            swaggerVersion = swaggerSpec.version
        }

        cacheStore[swaggerSpec.id.toString()] = Pair(resource, apiDefinition)
    }

    fun findOne(id: ObjectId): Pair<SwaggerResource, String>? {
        return cacheStore[id.toString()]
    }

    fun findAll(): List<Pair<SwaggerResource, String>> {
        return cacheStore.map { it.value }
    }

    fun flash() {
        cacheStore.clear()
    }
}
