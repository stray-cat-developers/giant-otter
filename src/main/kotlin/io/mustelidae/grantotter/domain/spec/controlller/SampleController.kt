package io.mustelidae.grantotter.domain.spec.controlller

import io.mustelidae.grantotter.common.Reply
import io.mustelidae.grantotter.utils.toReply
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("sample")
class SampleController {

    @PostMapping
    fun postTest (request: Sample): Reply<String> {
        return request.name.toReply()
    }

    data class Sample(
        val name: String
    )
}