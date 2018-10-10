package olilay.com.golemreader.adapter

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.article_view.view.*
import olilay.com.golemreader.R
import android.content.Intent
import olilay.com.golemreader.activities.ArticleActivity
import olilay.com.golemreader.models.MinimalArticle


class ArticleAdapter(private val dataset: List<MinimalArticle>) :
        RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

    class ViewHolder(val cardView: CardView) : RecyclerView.ViewHolder(cardView)

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ArticleAdapter.ViewHolder {
        val cardView = LayoutInflater.from(parent.context)
                .inflate(R.layout.article_view, parent, false) as CardView

        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]

        holder.cardView.article_heading.text = item.heading
        holder.cardView.article_description.text = item.description
        holder.cardView.article_photo.setImageBitmap(item.thumbnail)
        holder.cardView.article_text_comment.text = item.amountOfComments.toString()
        holder.cardView.article_date.text = item.getDateString()
        holder.cardView.article_time.text = item.getTimeString()

        holder.cardView.setOnClickListener { v ->
            val intent = Intent(v.context, ArticleActivity::class.java)
            intent.putExtra("minimalArticle", item)
            v.context.startActivity(intent)
        }
    }

    override fun getItemCount() = dataset.size
}