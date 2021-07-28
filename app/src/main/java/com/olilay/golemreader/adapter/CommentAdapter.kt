package com.olilay.golemreader.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.olilay.golemreader.databinding.CommentViewBinding
import com.olilay.golemreader.models.comment.CommentMetadata


class CommentAdapter(private val data: List<CommentMetadata>) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    class ViewHolder(val commentViewBinding: CommentViewBinding) :
        RecyclerView.ViewHolder(commentViewBinding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val commentViewBinding = CommentViewBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(commentViewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.commentViewBinding.commentHeading.text = item.heading
        holder.commentViewBinding.commentDate.text = "${item.getCreatedDateString()},"
        holder.commentViewBinding.commentTime.text = item.getCreatedTimeString()
        holder.commentViewBinding.commentAuthor.text = item.author
        holder.commentViewBinding.commentAnswerCount.text = item.answerCount.toString()

        holder.commentViewBinding.root.setOnClickListener { v ->
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.url.toString()))
            v.context.startActivity(browserIntent)
        }
    }

    override fun getItemCount() = data.size
}