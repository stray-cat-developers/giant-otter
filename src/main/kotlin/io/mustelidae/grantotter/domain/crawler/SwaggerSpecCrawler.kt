package io.mustelidae.grantotter.domain.crawler

import io.mustelidae.grantotter.domain.client.SpecClientHandler
import io.mustelidae.grantotter.domain.spec.SwaggerSpec
import io.mustelidae.grantotter.domain.spec.SwaggerSpecFinder
import io.mustelidae.grantotter.utils.ClientSupport
import io.mustelidae.grantotter.utils.Jackson
import org.slf4j.LoggerFactory
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
) : ClientSupport(
    Jackson.getMapper(),
    true,
    LoggerFactory.getLogger(SwaggerSpecCrawler::class.java),
) {

    fun crawling(swaggerSpec: SwaggerSpec) {
        val client = specClientHandler.client(swaggerSpec.type)

        val result = client.getSpec(URI(swaggerSpec.url))
        SwaggerDocCacheStore.add(swaggerSpec, result)
    }

    @Scheduled(fixedDelay = 300000) // 5분
    fun flushAndCrawlingAll() {
        SwaggerDocCacheStore.flash()
        val specs = swaggerSpecFinder.findAll()

        for (spec in specs) {
            try {
                this.crawling(spec)
            } catch (e: Exception) {
                log.error("${spec.name} can't crawing")
            }
        }
    }
}
