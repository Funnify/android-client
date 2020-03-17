package com.funnify.app.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.funnify.app.R
import kotlinx.android.synthetic.main.list_item.view.*

class PostsAdapter(val context: Context?, val posts: List<Post>) :
    RecyclerView.Adapter<PostsAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val post = posts[position]
        holder.setData(post, position)
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var currentPost: Post? = null
        var currentPosition: Int = 0

        init {
            itemView.setOnClickListener {
                Toast.makeText(context, currentPost!!.title, Toast.LENGTH_SHORT).show()
            }
        }

        fun setData(post: Post?, pos: Int) {
            itemView.txvPost.text = post!!.title
            itemView.txvTime.text = post.time

            this.currentPost = post
            this.currentPosition = pos
        }
    }
}