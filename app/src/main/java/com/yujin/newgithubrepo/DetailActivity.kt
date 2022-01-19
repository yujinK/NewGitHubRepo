package com.yujin.newgithubrepo

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.schedulers.Schedulers

class DetailActivity : AppCompatActivity() {
    lateinit var fullNameTextView: TextView
    private lateinit var detailTextView: TextView
    lateinit var repoStarTextView: TextView
    lateinit var repoForkTextView: TextView
    lateinit var ownerImageView: ImageView

    companion object {
        fun start(context: Context, fullRepositoryName: String) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_FULL_REPOSITORY_NAME, fullRepositoryName)
            context.startActivity(intent)
        }

        const val EXTRA_FULL_REPOSITORY_NAME = "EXTRA_FULL_REPOSITORY_NAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        fullNameTextView = findViewById(R.id.fullname)
        //TODO: 빌드 되는 지 확인하고 안되면 원래 걸로 복구
//        fullNameTextView = this@DetailActivity.findViewById(R.id.fullname)
        detailTextView = findViewById(R.id.detail)
        repoStarTextView = findViewById(R.id.repo_star)
        repoForkTextView = findViewById(R.id.repo_fork)
        ownerImageView = findViewById(R.id.owner_image)

        val intent: Intent = intent
        val fullRepoName: String? = intent.getStringExtra(EXTRA_FULL_REPOSITORY_NAME)

        if (fullRepoName != null) {
            loadRepositories(fullRepoName)
        }
    }

    private fun loadRepositories(fullRepoName: String) {
        val repoData: List<String> = fullRepoName.split("/")
        val owner: String = repoData[0]
        val repoName: String = repoData[1]
        val gitHubService: GitHubService =
            (application as NewGitHubReposApplication).getGitHubService()
        gitHubService.detailRepo(owner, repoName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                setupRepositoryInfo(response)
            }, {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "읽을 수 없습니다.",
                    Snackbar.LENGTH_LONG
                )
                    .setAction("Action", null).show()

            })
    }

    private fun setupRepositoryInfo(response: GitHubService.RepositoryItem) {
        fullNameTextView.text = response.full_name
        detailTextView.text = response.description
        repoStarTextView.text = response.stargazers_count
        repoForkTextView.text = response.forks_count

        Glide.with(this)
            .load(response.owner.avatar_url)
            .transform(CenterCrop(), RoundedCorners(10)).into(ownerImageView)

        val listener: View.OnClickListener = View.OnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(response.html_url)))
            } catch (e: Exception) {
                Snackbar.make(findViewById(android.R.id.content), "링크를 열 수 없습니다", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }

        fullNameTextView.setOnClickListener(listener)
        ownerImageView.setOnClickListener(listener)
    }
}
