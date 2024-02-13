package io.mustelidae.grantotter.domain.spec.controlller

import io.mustelidae.grantotter.common.Replies
import io.mustelidae.grantotter.common.Reply
import io.mustelidae.grantotter.utils.fromJson
import io.mustelidae.grantotter.utils.toJson
import org.springframework.hateoas.server.mvc.linkTo
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

class SwaggerSpecControllerFlow(
    private val mockMvc: MockMvc,
) {

    fun findOne(id: String): SwaggerSpecResources.Reply {
        val uri = linkTo<SwaggerSpecController> { findOne(id) }.toUri()

        return mockMvc.get(uri) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn()
            .response
            .contentAsString
            .fromJson<SwaggerSpecResources.Reply>()
    }

    fun add(request: SwaggerSpecResources.Request): String {
        val uri = linkTo<SwaggerSpecController> { add(request) }.toUri()

        return mockMvc.post(uri) {
            contentType = MediaType.APPLICATION_JSON
            content = request.toJson()
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn()
            .response
            .contentAsString
            .fromJson<Reply<String>>()
            .content!!
    }

    fun findAll(): List<SwaggerSpecResources.Reply> {
        val uri = linkTo<SwaggerSpecController> { findAll() }.toUri()
        return mockMvc.get(uri) {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { is2xxSuccessful() }
        }.andReturn()
            .response
            .contentAsString
            .fromJson<Replies<SwaggerSpecResources.Reply>>()
            .getContent()
            .toList()
    }
}
