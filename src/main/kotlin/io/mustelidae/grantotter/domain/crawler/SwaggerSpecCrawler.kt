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
        val specs = swaggerSpecFinder.findAll()

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
        swaggerUiConfig.urls = swaggerUrlSet.toMutableSet().apply {
            clear()
            val urls = SwaggerDocCacheStore.findAll()
            addAll(urls.map { it.first }.sortedBy { it.name })
        }
    }
}
