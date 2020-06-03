package io.mustelidae.grantotter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GrantotterApplication

fun main(args: Array<String>) {
    runApplication<GrantotterApplication>(*args)
}
