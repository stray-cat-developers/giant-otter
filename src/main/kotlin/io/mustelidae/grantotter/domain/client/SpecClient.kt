package io.mustelidae.grantotter.domain.client

import java.net.URI

interface SpecClient {

    fun hasSpec(uri: URI): Boolean

    fun getSpec(uri: URI): String
}
