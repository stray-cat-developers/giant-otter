package io.mustelidae.grantotter.domain.crawler

import com.github.kittinunf.fuel.Fuel
import io.mustelidae.grantotter.domain.spec.SwaggerSpec
import io.mustelidae.grantotter.domain.spec.SwaggerSpecFinder
import io.mustelidae.grantotter.utils.ClientSupport
import io.mustelidae.grantotter.utils.Jackson
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@EnableScheduling
@Service
class SwaggerSpecCrawler
@Autowired constructor(
    private val swaggerSpecFinder: SwaggerSpecFinder
) : ClientSupport(
    Jackson.getMapper(),
    true,
    LoggerFactory.getLogger(SwaggerSpecCrawler::class.java)
) {

    fun crawling(swaggerSpec: SwaggerSpec) {
        val headers: MutableMap<String, Any> = mutableMapOf("Content-Type" to "application/json")
        swaggerSpec.headers?.let {
            headers.putAll(it)
        }

        val result = Fuel.get(swaggerSpec.url)
            .header(headers)
            .responseString()
            .orElseThrow()
            .component1()

        if (result.isNullOrBlank().not()) {
            val mapper = Jackson.getMapper()

            val apiDefinition = when (swaggerSpec.type) {
                SwaggerSpec.Type.JSON -> {
                    result!!
                }
                SwaggerSpec.Type.YAML -> {
                    val yaml = Jackson.getYmlMapper().readValue(result, Any::class.java)
                    mapper.writerWithDefaultPrettyPrinter().writeValueAsString(yaml)
                }
            }
            SwaggerDocCacheStore.add(swaggerSpec, apiDefinition)
        }
    }

    @Scheduled(fixedDelay = 300000) // 5분
    fun flushAndCrawlingAll() {
        SwaggerDocCacheStore.flash()
        val specs = swaggerSpecFinder.findAll()

        for (spec in specs) {
            this.crawling(spec)
        }
    }
}
