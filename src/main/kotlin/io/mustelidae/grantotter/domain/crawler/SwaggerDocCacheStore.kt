package io.mustelidae.grantotter.domain.crawler

import io.mustelidae.grantotter.domain.spec.SwaggerSpec
import org.bson.types.ObjectId
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties.SwaggerUrl
import java.util.concurrent.ConcurrentHashMap

object SwaggerDocCacheStore {
    private val cacheStore: ConcurrentHashMap<String, Pair<SwaggerUrl, String>> = ConcurrentHashMap()

    fun add(swaggerSpec: SwaggerSpec, apiDefinition: String) {
        val displayName = "[${swaggerSpec.group}] ${swaggerSpec.name}"

        // springdoc SwaggerUrl의 equals()/hashCode()는 1번째 생성자 인자(내부 name 필드)만 비교하며,
        // 이 값이 SwaggerSpecCrawler#updateOpenAPIGroup()의 Set<SwaggerUrl> 중복 제거 기준이 된다.
        // group을 넘기면 group이 같은 서로 다른 스펙까지 중복으로 취급되어 사라지므로,
        // 실제 중복 판단 기준인 원본 문서 url(swaggerSpec.url)을 넘긴다.
        // 화면에 보이는 라벨은 3번째 인자(displayName, "[group] name" 형태)로 별도 전달한다.
        val swaggerUrl = SwaggerUrl(
            swaggerSpec.url,
            "/swagger/specifications/${swaggerSpec.getId()}/docket",
            displayName,
        )
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
