package olilay.com.golemreader.models

import java.net.URL
import java.util.*

class Article (heading : String,
               url : URL,
               description : String,
               date : Date,
               amountOfComments : Int,
               imageUrl : URL,
               var content : String) : MinimalArticle(heading, url, description, date, imageUrl, amountOfComments) {
}