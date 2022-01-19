package com.yujin.newgithubrepo

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GitHubService {
    @GET("search/repositories?sort=star&order=desc")
    fun listRepos(@Query("q") query: String): Observable<Repositories>

    @GET("repos/{repoOwner}/{repoName}")
    fun detailRepo(@Path(value = "repoOwner") owner: String, @Path(value = "repoName") repoName: String): Observable<RepositoryItem>

    class Repositories(val items: List<RepositoryItem>)

    class RepositoryItem(var description: String, var owner: Owner, var language: String, var name: String,
                         var stargazers_count: String, var forks_count: String, var full_name: String, var html_url: String)

    class Owner(var received_events_url: String, var organizations_url: String, var avatar_url: String,
                var gravatar_id: String, var gists_url: String, var starred_url: String, var site_admin: String,
                var type: String, var url: String, var id: String, var html_url: String, var following_url: String,
                var events_url: String, var login: String, var subscription_url: String, var repos_url: String, var followers_url: String)
}