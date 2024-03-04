package io.mustelidae.grantotter.config

import org.springdoc.core.properties.SwaggerUiConfigParameters
import org.springdoc.core.properties.SwaggerUiConfigProperties
import org.springdoc.core.properties.SwaggerUiOAuthProperties
import org.springdoc.core.providers.ObjectMapperProvider
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon
import org.springframework.stereotype.Component
import java.io.InputStream

@Component
class SwaggerProxyIndexPageTransformer(
    swaggerUiConfig: SwaggerUiConfigProperties?,
    swaggerUiOAuthProperties: SwaggerUiOAuthProperties?,
    swaggerUiConfigParameters: SwaggerUiConfigParameters?,
    swaggerWelcomeCommon: SwaggerWelcomeCommon?,
    objectMapperProvider: ObjectMapperProvider?,
) : SwaggerIndexPageTransformer(
    swaggerUiConfig,
    swaggerUiOAuthProperties,
    swaggerUiConfigParameters,
    swaggerWelcomeCommon,
    objectMapperProvider,
) {

    override fun defaultTransformations(inputStream: InputStream?): String {
        val default = super.defaultTransformations(inputStream)
        if (default.contains("requestInterceptor")) {
            throw IllegalStateException("requestInterceptor is already defined, proxying request won't work as expected")
        }
        val proxied = default.replace(
            "});",
            """
                });
            const setProxy = function() {
                    if(!window.ui) return // swagger-ui has not been initialized yet
                    const uiConfigs = window.ui.getConfigs()
                    uiConfigs.showMutatedRequest = false
                    uiConfigs.requestInterceptor = (r)=>{
                        if(!r.url.includes(window.location.host))
                        r.url = window.location.protocol + "//" + window.location.host + "/swagger-proxy?url=" + encodeURIComponent(r.url) + "&method=" + r.method + "&headers=" + new URLSearchParams(r.headers).toString();
                        return r
                    }
                    clearInterval(timer)
                }
            const timer = setInterval(setProxy, 500);
            """.trimIndent(),
        )
        return proxied
    }
}
