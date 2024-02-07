package io.mustelidae.grantotter.config

import io.mustelidae.grantotter.domain.crawler.SwaggerDocCacheStore
import org.springdoc.core.models.GroupedOpenApi
import org.springdoc.core.properties.AbstractSwaggerUiConfigProperties.SwaggerUrl
import org.springdoc.core.properties.SwaggerUiConfigProperties
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.context.annotation.Primary
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Configuration
class SwaggerConfiguration {

    init {
        SpringDocUtils.getConfig().replaceWithSchema(
            LocalDateTime::class.java,
            io.swagger.v3.oas.models.media.Schema<LocalDateTime>().apply {
                example(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME))
            },
        )
        SpringDocUtils.getConfig().replaceWithSchema(
            LocalTime::class.java,
            io.swagger.v3.oas.models.media.Schema<LocalTime>().apply {
                example(LocalTime.now().format(DateTimeFormatter.ISO_TIME))
            },
        )
        SpringDocUtils.getConfig().replaceWithSchema(
            LocalDate::class.java,
            io.swagger.v3.oas.models.media.Schema<LocalDate>().apply {
                example(LocalDate.now().format(DateTimeFormatter.ISO_DATE))
            },
        )
    }

    @Primary
    @Bean
    @Lazy
    fun apis(swaggerUiConfig: SwaggerUiConfigProperties): Set<SwaggerUrl> {
        val swaggerUrls = mutableSetOf<SwaggerUrl>()
        swaggerUrls.addAll(SwaggerDocCacheStore.findAll().map { it.first }.sortedBy { it.name })
        swaggerUiConfig.urls = swaggerUrls
        return swaggerUrls
    }

    @Bean
    fun default(): GroupedOpenApi = GroupedOpenApi.builder()
        .group("Management")
        .addOpenApiCustomizer {
            it.info.version("v1")
        }
        .packagesToScan("io.mustelidae.grantotter.domain")
        .build()
}
