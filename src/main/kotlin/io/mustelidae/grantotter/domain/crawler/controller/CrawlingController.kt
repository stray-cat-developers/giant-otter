package io.mustelidae.grantotter.domain.crawler.controller

import io.mustelidae.grantotter.common.Reply
import io.mustelidae.grantotter.domain.crawler.SwaggerSpecCrawler
import io.mustelidae.grantotter.utils.toReply
import io.swagger.v3.oas.annotations.Operation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/crawling/manual")
class CrawlingController
@Autowired constructor(
    private val swaggerSpecCrawler: SwaggerSpecCrawler,
) {

    @Operation(summary = "Crawling")
    @PutMapping
    fun crawlingAll(): Reply<Unit> {
        swaggerSpecCrawler.flushAndCrawlingAll()

        return Unit.toReply()
    }
}
