package io.mustelidae.grantotter.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
class AppEnvironment {
    var spec = Spec()

    class Spec : Connection()

    open class Connection {
        var connectionTimeout: Long = 1000
        var responseTimeout: Long = 1000
        var logging: Boolean = false
        var useDummy: Boolean = false
    }
}
