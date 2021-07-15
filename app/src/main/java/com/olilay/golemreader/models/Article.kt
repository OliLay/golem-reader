package com.olilay.golemreader.models

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
    var content: String
) : ArticleMetadata(heading, url, description, date, imageUrl, amountOfComments) {
}