package io.mustelidae.grantotter

import io.mustelidae.grantotter.domain.crawler.SwaggerSpecCrawler
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("default")
@Configuration
class ApplicationInitializer(
    private val crawler: SwaggerSpecCrawler,
) : CommandLineRunner {
    override fun run(vararg args: String?) {
        crawler.flushAndCrawlingAll()
    }
}
