package io.mustelidae.grantotter

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("embedded")
@SpringBootTest
class GrantOtterApplicationTests {
    @Test
    fun contextLoads() {}
}
