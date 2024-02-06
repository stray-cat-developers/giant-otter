package io.mustelidae.grantotter.domain.client

import io.mustelidae.grantotter.config.AppEnvironment
import io.mustelidae.grantotter.domain.spec.SwaggerSpec
import io.mustelidae.grantotter.utils.ConnectionConfig
import io.mustelidae.grantotter.utils.Jackson
import io.mustelidae.grantotter.utils.RestClient
import org.springframework.stereotype.Component

@Component
class SpecClientHandler(
    private val appEnvironment: AppEnvironment,
) {
    private val restClient = RestClient.new(ConnectionConfig.from(appEnvironment.spec))

    fun client(type: SwaggerSpec.Type): SpecClient {
        if (appEnvironment.spec.useDummy) {
            return SpecDummyClient()
        }

        return when (type) {
            SwaggerSpec.Type.JSON -> SpecStableClient(restClient, Jackson.getMapper(), appEnvironment.spec.logging)
            SwaggerSpec.Type.YAML -> SpecStableClient(restClient, Jackson.getYmlMapper(), appEnvironment.spec.logging)
        }
    }
}
