package com.olilay.golemreader.models.article

import kotlinx.parcelize.Parcelize
import java.net.URL
import java.util.*

@Parcelize
class Article(
    override var heading: String,
    override var url: URL,
    override var description: String,
    override var date: Date,
    override var amountOfComments: Int,
    override var imageUrl: URL?,
    var content: String,
    var commentsUrl: URL?,
) : ArticleMetadata(heading, url, description, date, imageUrl, amountOfComments)