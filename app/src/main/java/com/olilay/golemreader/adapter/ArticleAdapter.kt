package com.olilay.golemreader.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.olilay.golemreader.activities.ArticleActivity
import com.olilay.golemreader.databinding.ArticleViewBinding
import com.olilay.golemreader.models.ArticleMetadata


class ArticleAdapter(private val dataset: List<ArticleMetadata>) :
    RecyclerView.Adapter<ArticleAdapter.ViewHolder>() {

    class ViewHolder(val articleViewBinding: ArticleViewBinding) :
        RecyclerView.ViewHolder(articleViewBinding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val articleViewBinding = ArticleViewBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(articleViewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = dataset[position]

        holder.articleViewBinding.articleHeading.text = item.heading
        holder.articleViewBinding.articleDescription.text = item.description
        holder.articleViewBinding.articlePhoto.setImageBitmap(item.thumbnail)
        holder.articleViewBinding.articleTextComment.text = item.amountOfComments.toString()
        holder.articleViewBinding.articleDate.text = "${item.getDateString()},"
        holder.articleViewBinding.articleTime.text = item.getTimeString()

        holder.articleViewBinding.root.setOnClickListener { v ->
            val intent = Intent(v.context, ArticleActivity::class.java)
            intent.putExtra("minimalArticle", item)
            v.context.startActivity(intent)
        }
    }

    override fun getItemCount() = dataset.size
}