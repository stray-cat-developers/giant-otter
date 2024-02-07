package io.mustelidae.grantotter.domain.crawler.controller

import io.mustelidae.grantotter.common.Reply
import io.mustelidae.grantotter.domain.crawler.SwaggerDocCacheStore
import io.mustelidae.grantotter.domain.crawler.SwaggerSpecCrawler
import io.mustelidae.grantotter.utils.toReply
import io.swagger.v3.oas.annotations.Operation
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties
import org.springdoc.core.properties.SwaggerUiConfigProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/crawling/manual")
class CrawlingController
@Autowired constructor(
    private val swaggerSpecCrawler: SwaggerSpecCrawler,
    private val swaggerUrlSet: Set<AbstractSwaggerUiConfigProperties.SwaggerUrl>,
    private val swaggerUiConfig: SwaggerUiConfigProperties,
) {

    @Operation(summary = "Crawling")
    @PutMapping
    fun crawlingAll(): Reply<Unit> {
        swaggerSpecCrawler.flushAndCrawlingAll()
        swaggerUiConfig.urls = swaggerUrlSet.toMutableSet().apply {
            clear()
            val urls = SwaggerDocCacheStore.findAll()
            addAll(urls.map { it.first }.sortedBy { it.name })
        }

        return Unit.toReply()
    }
}
