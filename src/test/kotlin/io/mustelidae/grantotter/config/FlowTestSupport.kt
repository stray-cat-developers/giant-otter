package io.mustelidae.grantotter.config

import io.mustelidae.grantotter.GrantOtterApplication
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@ActiveProfiles("embedded")
@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [GrantOtterApplication::class], webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
class FlowTestSupport {
    @Autowired
    final lateinit var mockMvc: MockMvc
}
