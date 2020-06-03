package io.mustelidae.grantotter.config

import cz.jirutka.spring.embedmongo.EmbeddedMongoFactoryBean
import java.io.IOException
import org.springframework.boot.autoconfigure.mongo.MongoProperties
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.Lazy
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.stereotype.Component

@Lazy(false)
@Import(value = [EmbeddedMongoAutoConfiguration::class])
@Component
@EnableConfigurationProperties(value = [MongoProperties::class])
class DefaultEmbeddedMongo(
    private val mongoProperties: MongoProperties
) {

    @Bean
    @Throws(IOException::class)
    fun mongoTemplate(): MongoTemplate {
        val mongo = EmbeddedMongoFactoryBean()
        mongo.setBindIp(mongoProperties.host)
        mongo.setPort(mongoProperties.port)

        val mongoClient = mongo.`object`!!
        @Suppress("DEPRECATION")
        return MongoTemplate(mongoClient, mongoProperties.database)
    }
}
