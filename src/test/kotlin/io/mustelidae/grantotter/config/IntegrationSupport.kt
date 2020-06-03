package io.mustelidae.grantotter.config

import io.mustelidae.grantotter.GrantotterApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("embedded")
@SpringBootTest(classes = [GrantotterApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationSupport {
    @LocalServerPort
    var port: Int = 0
}
