package olilay.com.golemreader.models

import java.net.URL
import java.util.*

data class MinimalArticle (var heading : String,
                    var url : URL,
                    var description : String,
                    var date : Date,
                    var imageUrl : URL,
                    var amountOfComments : Int)