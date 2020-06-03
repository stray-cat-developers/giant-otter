package io.mustelidae.grantotter.domain.crawler.controller

import io.mustelidae.grantotter.common.Reply
import io.mustelidae.grantotter.domain.crawler.SwaggerSpecCrawler
import io.mustelidae.grantotter.utils.toReply
import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Api(tags = ["Crawling"], description = "Crawling swagger api document definition")
@RestController
@RequestMapping("/crawling/manual")
class CrawlingController
@Autowired constructor(
    private val swaggerSpecCrawler: SwaggerSpecCrawler
) {

    @ApiOperation("retry crawling all")
    @PutMapping
    fun crawlingAll(): Reply<Unit> {
        swaggerSpecCrawler.flushAndCrawlingAll()
        return Unit.toReply()
    }
}
