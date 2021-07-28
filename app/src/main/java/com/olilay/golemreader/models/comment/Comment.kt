package com.olilay.golemreader.models.comment

import java.net.URL
import java.util.*

class Comment(
    var posts: List<Post>,
    metadata: CommentMetadata
) : CommentMetadata(
    metadata.heading,
    metadata.url,
    metadata.author,
    metadata.dateCreated,
    metadata.answerCount
)