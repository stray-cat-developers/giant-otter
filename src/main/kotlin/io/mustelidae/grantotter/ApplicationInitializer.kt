package io.mustelidae.grantotter

import io.mustelidae.grantotter.domain.crawler.SwaggerSpecCrawler
import io.mustelidae.grantotter.domain.spec.SwaggerSpecManager
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("default")
@Configuration
class ApplicationInitializer(
    private val swaggerSpecManager: SwaggerSpecManager,
    private val crawler: SwaggerSpecCrawler,
) : CommandLineRunner {

    @Value("\${server.port}")
    private var port: Int = 0

    override fun run(vararg args: String?) {
        /*val spec = SwaggerSpec(
            SwaggerSpec.Type.JSON,
            "[CONSOLE] GrantOtter",
            "http://localhost:$port/v3/api-docs",
            "3.0",
            "Grant otter manage api",
        )
        swaggerSpecManager.add(spec)
        crawler.flushAndCrawlingAll()*/
    }
}
