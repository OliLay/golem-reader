package com.olilay.golemreader.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.olilay.golemreader.databinding.PostViewBinding
import com.olilay.golemreader.models.comment.Post


class PostAdapter(private val data: List<Post>) :
    RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    class ViewHolder(val postViewBinding: PostViewBinding) :
        RecyclerView.ViewHolder(postViewBinding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val postViewBinding = PostViewBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(postViewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]

        holder.postViewBinding.postAuthor.text = item.author
        holder.postViewBinding.postContent.text = item.content
        holder.postViewBinding.postDate.text = item.getDateTimeString()
        holder.postViewBinding.postHeading.text = item.heading
    }

    override fun getItemCount() = data.size
}