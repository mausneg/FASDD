package com.example.fasdd_android.newsdata.response

import com.google.gson.annotations.SerializedName

data class ResponseNews(

	@field:SerializedName("totalResults")
	val totalResults: Int,

	@field:SerializedName("articles")
	val articles: List<ArticlesItem>,

	@field:SerializedName("status")
	val status: String
)

data class ArticlesItem(

	@field:SerializedName("publishedAt")
	val publishedAt: String,

	@field:SerializedName("author")
	val author: String,

	@field:SerializedName("urlToImage")
	val urlToImage: Any,

	@field:SerializedName("description")
	val description: Any,

	@field:SerializedName("source")
	val source: Source,

	@field:SerializedName("title")
	val title: String,

	@field:SerializedName("url")
	val url: String,

	@field:SerializedName("content")
	val content: Any
)

data class Source(

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("id")
	val id: String
)
