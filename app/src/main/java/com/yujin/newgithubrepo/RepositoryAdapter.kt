package com.yujin.newgithubrepo

import android.content.Context
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class RepositoryAdapter(private val context: Context, private val onRepositoryItemClickListener: OnRepositoryItemClickListener) : RecyclerView.Adapter<RepositoryAdapter.RepoViewHolder>() {
    private var items: List<GitHubService.RepositoryItem> = emptyList()

    fun setItemsAndRefresh(items: List<GitHubService.RepositoryItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    private fun getItemAt(position: Int) : GitHubService.RepositoryItem {
        return items[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.repo_item, parent, false)
        return RepoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val item: GitHubService.RepositoryItem = getItemAt(position)

        holder.itemView.setOnClickListener {
            onRepositoryItemClickListener.onRepositoryItemClick(item)
        }

        holder.repoName.text = item.name
        holder.repoDetail.text = item.description
        holder.starCount.text = item.stargazers_count

        Glide.with(context)
            .load(item.owner.avatar_url)
            .transform(CenterCrop(), RoundedCorners(10)).into(holder.repoImage)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface OnRepositoryItemClickListener {
        fun onRepositoryItemClick(item: GitHubService.RepositoryItem)
    }

    inner class RepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var repoName: TextView = itemView.findViewById(R.id.repo_name)
        var repoDetail: TextView = itemView.findViewById(R.id.repo_detail)
        var repoImage: ImageView = itemView.findViewById(R.id.repo_image)
        var starCount: TextView = itemView.findViewById(R.id.repo_star)

    }
}