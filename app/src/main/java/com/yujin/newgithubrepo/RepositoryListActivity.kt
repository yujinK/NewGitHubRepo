package com.yujin.newgithubrepo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class RepositoryListActivity : AppCompatActivity(), RepositoryAdapter.OnRepositoryItemClickListener {
    private lateinit var languageSpinner: Spinner
    private lateinit var progressBar: ProgressBar
    private lateinit var coordinatorLayout: CoordinatorLayout

    private lateinit var repositoryAdapter: RepositoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository_list)

        setupViews()
    }

    private fun setupViews() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_repos)
        recyclerView.layoutManager = LinearLayoutManager(this)
        repositoryAdapter = RepositoryAdapter(this, this)
        recyclerView.adapter = repositoryAdapter

        progressBar = findViewById(R.id.progress_bar)

        coordinatorLayout = findViewById(R.id.coordinator_layout)

        languageSpinner = findViewById(R.id.language_spinner)
        val adapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_item)
        adapter.addAll("java", "object-c", "swift", "groovy", "python", "ruby", "c")
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        languageSpinner.adapter = adapter
        languageSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val language = languageSpinner.getItemAtPosition(position).toString()
                loadRepositories(language)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }
    }

    private fun loadRepositories(language: String) {
        progressBar.visibility = View.VISIBLE

        val calendar: Calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, -7)
        val text: String = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(calendar.time).toString()

        val application: NewGitHubReposApplication = application as NewGitHubReposApplication
        val observable: Observable<GitHubService.Repositories> = application.getGitHubService().listRepos(
            "language:$language created:>$text"
        )
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe({ repositories ->
            progressBar.visibility = View.GONE
            repositoryAdapter.setItemsAndRefresh(repositories.items)
        }, { t ->
            Snackbar.make(coordinatorLayout, "읽어올 수 없습니다.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
            Log.e("error", t.toString())
        })
    }

    override fun onRepositoryItemClick(item: GitHubService.RepositoryItem) {
        DetailActivity.start(this, item.full_name)
    }
}