package olilay.com.golemreader.parser

import android.os.AsyncTask
import org.jsoup.nodes.Element

//TODO: feat: only load first X articles, load others when needed
//TODO: bug: parsing amount of comments sometimes fails
//TODO: feat: support 2 page articles
//TODO: feat: support slide shows (means not showing them :D )
/**
 * Performs async network tasks to get and parse articles from Golem.de.
 * @author Oliver Layer
 */
class TickerParser(private val parseManager: ParseManager)  : AsyncTask<Void, Void, List<Element>>() {

    override fun doInBackground(vararg void : Void) : List<Element> {
        return parse()
    }

    override fun onPostExecute(result: List<Element>?) {
        super.onPostExecute(result)

        parseManager.onTickerParsed(result!!)
    }

    /**
     * Parses the Golem Ticker and extracts all elements belonging to articles.
     * @return Elements (representing articles) on the ticker.
     */
    private fun parse() : List<Element> {
        val doc = ParserUtils.getDocument(GOLEM_URL)

        return doc.getElementsByClass(GOLEM_ARTICLE_CLASS)

        //TODO: add parsing of main article
       // articles.add(getMainArticle(doc))

       // articles.sortByDescending { article -> article.date }
    }

/*    private fun getMainArticle(doc: Document) : Article {
        val elements = doc.getElementsByClass(GOLEM_MAIN_CLASS)

        val preHeading = elements.select("h2[class=$GOLEM_MAIN_PRE_HEADING_CLASS]")?.first()?.text()
                ?: throw ParseException("Could not parse pre-heading for main article")
        val heading = elements.select("h1[class=$GOLEM_MAIN_HEADING_CLASS]")?.first()?.text()
                ?: throw ParseException("Could not parse heading for main article")
        val url = elements.select("a")?.first()?.attr("href")
                ?: throw ParseException("Could not parse url for main article")
        val description = elements.select("p")?.first()?.text()
                ?: throw ParseException("Could not parse description for main article")
        val image = urlToDrawable(elements.select("img")?.attr("src")
                ?: throw ParseException("Could not parse image url for main article"))
        val authorAndDateAndContent = getAuthorAndDateAndContent(url)
        val author = authorAndDateAndContent.first
        val date = authorAndDateAndContent.second
        val content = authorAndDateAndContent.third
        val amountOfComments = parseCommentString(elements.select("p")[1]?.text()
                ?: throw ParseException("Could not parse amount of comments for main article"))

        return Article(preHeading, heading, URL(url), description, image, date, author, amountOfComments, content)
    }*/
}