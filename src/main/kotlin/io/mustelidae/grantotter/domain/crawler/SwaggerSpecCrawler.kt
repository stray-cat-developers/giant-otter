package io.mustelidae.grantotter.domain.crawler

import io.mustelidae.grantotter.domain.client.SpecClientHandler
import io.mustelidae.grantotter.domain.spec.SpecConvertor
import io.mustelidae.grantotter.domain.spec.SwaggerSpec
import io.mustelidae.grantotter.domain.spec.SwaggerSpecFinder
import io.mustelidae.grantotter.utils.ClientSupport
import io.mustelidae.grantotter.utils.Jackson
import org.slf4j.LoggerFactory
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties
import org.springdoc.core.properties.SpringDocConfigProperties
import org.springdoc.core.properties.SwaggerUiConfigParameters
import org.springdoc.core.properties.SwaggerUiConfigProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.net.URI

@EnableScheduling
@Service
class SwaggerSpecCrawler
@Autowired constructor(
    private val swaggerSpecFinder: SwaggerSpecFinder,
    private val specClientHandler: SpecClientHandler,
    private val swaggerDocConfigProperties: SpringDocConfigProperties,
    private val swaggerUrlSet: Set<AbstractSwaggerUiConfigProperties.SwaggerUrl>,
    private val swaggerUiConfig: SwaggerUiConfigProperties,
    private val swaggerUiConfigParameters: SwaggerUiConfigParameters,
) : ClientSupport(
    Jackson.getMapper(),
    true,
    LoggerFactory.getLogger(SwaggerSpecCrawler::class.java),
) {
    fun crawling(swaggerSpec: SwaggerSpec) {
        val type = swaggerSpec.type
        val client = specClientHandler.client(type)

        val result = client.getSpec(URI(swaggerSpec.url), swaggerSpec.getHeader())

        val convertor = SpecConvertor(type, result)

        val apiDefinition = if (convertor.isV2()) {
            convertor.convertToV3(swaggerDocConfigProperties)
        } else {
            result
        }

        SwaggerDocCacheStore.add(swaggerSpec, apiDefinition)
    }

    @Scheduled(fixedDelay = 300000) // 5분
    fun flushAndCrawlingAll() {
        SwaggerDocCacheStore.flash()

        // 같은 url로 등록된 스펙이 여러 개면 가장 최근(createdAt 기준)에 등록된 것만 채택한다.
        val specs = swaggerSpecFinder.findAll()
            .groupBy { it.url }
            .map { (_, duplicated) -> duplicated.maxByOrNull { it.createdAt }!! }

        for (spec in specs) {
            try {
                this.crawling(spec)
                this.updateOpenAPIGroup()
            } catch (e: Exception) {
                log.error("${spec.name} can't crawing. cause by ${e.message}")
            }
        }
    }

    fun updateOpenAPIGroup() {
        val urls = swaggerUrlSet.toMutableSet().apply {
            clear()
            addAll(SwaggerDocCacheStore.findAll().map { it.first }.sortedBy { it.displayName })
        }
        swaggerUiConfig.urls = urls
        // swaggerUiConfig.urls를 갱신하는 것만으로는 삭제된 항목이 반영되지 않는다.
        // springdoc이 매 요청마다 swaggerUiConfig.urls를 swaggerUiConfigParameters.urls에 병합만 하고 제거하지는 않기 때문에,
        // 실제로 Swagger UI가 읽는 swaggerUiConfigParameters.urls도 여기서 직접 최신 상태로 교체해야 한다.
        swaggerUiConfigParameters.urls = urls.toMutableSet()
    }
}
