package io.mustelidae.grantotter.utils

import io.mustelidae.grantotter.common.Replies
import io.mustelidae.grantotter.common.Reply

fun <T> List<T>.toReplies(): Replies<T> = Replies(this)
fun <T> T.toReply(): Reply<T> = Reply(this)
