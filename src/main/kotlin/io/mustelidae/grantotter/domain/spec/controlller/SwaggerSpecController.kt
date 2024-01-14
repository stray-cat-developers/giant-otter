package io.mustelidae.grantotter.domain.spec.controlller

import io.mustelidae.grantotter.common.Replies
import io.mustelidae.grantotter.common.Reply
import io.mustelidae.grantotter.domain.spec.SwaggerSpec
import io.mustelidae.grantotter.domain.spec.SwaggerSpecFinder
import io.mustelidae.grantotter.domain.spec.SwaggerSpecManager
import io.mustelidae.grantotter.utils.toReplies
import io.mustelidae.grantotter.utils.toReply
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Management", description = "API Docket Spec Management")
@RestController
@RequestMapping("/swagger/specifications")
class SwaggerSpecController
@Autowired constructor(
    private val swaggerSpecManager: SwaggerSpecManager,
    private val swaggerSpecFinder: SwaggerSpecFinder,
) {

    @GetMapping
    fun findAll(): Replies<SwaggerSpecResources.Reply> {
        val specs = swaggerSpecFinder.findAll()
        return specs.map {
            SwaggerSpecResources.Reply.from(it)
        }.toReplies()
    }

    @GetMapping("{id}")
    fun findOne(@PathVariable id: String): Reply<SwaggerSpecResources.Reply> {
        val objectId = ObjectId(id)
        val spec = swaggerSpecFinder.findOne(objectId)
        return SwaggerSpecResources.Reply.from(spec)
            .toReply()
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun add(@Valid request: SwaggerSpecResources.Request): Reply<String> {
        val spec = request.run {
            SwaggerSpec(type, "[$category] $name", url, version, description, headers, tags)
        }

        val id = swaggerSpecManager.add(spec)
        return id.toString().toReply()
    }

    @DeleteMapping("{id}")
    fun remove(@PathVariable id: String): Reply<Unit> {
        swaggerSpecManager.remove(ObjectId(id))
        return Unit.toReply()
    }
}
