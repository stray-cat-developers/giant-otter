package io.mustelidae.grantotter

import io.mustelidae.grantotter.config.AppEnvironment
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(AppEnvironment::class)
class GrantOtterApplication

fun main(args: Array<String>) {
    runApplication<GrantOtterApplication>(*args)
}
