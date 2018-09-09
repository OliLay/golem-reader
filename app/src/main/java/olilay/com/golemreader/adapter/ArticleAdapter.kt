package olilay.com.golemreader.adapter

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.article_view.view.*
import olilay.com.golemreader.R
import olilay.com.golemreader.models.Article

class ArticleAdapter(private val dataset: List<Article>) :
        RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ArticleAdapter.ViewHolder {
        // create a new view
        val cardView = LayoutInflater.from(parent.context)
                .inflate(R.layout.article_view, parent, false) as CardView

        return ViewHolder(cardView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        val item = dataset[position]

        holder.cardView.article_heading.text = item.getFullHeading()
        holder.cardView.article_description.text = item.description
        holder.cardView.article_photo.setImageDrawable(item.thumbnail)
        holder.cardView.article_text_comment.text = item.amountOfComments.toString()
        holder.cardView.article_date.text = item.getDateString()
        holder.cardView.article_time.text = item.getTimeString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataset.size
}