package com.funnify.app.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

data class Post(var title: String, var time: String)

class HomeViewModel : ViewModel() {

    private val _posts = MutableLiveData<List<Post>>().apply {
        value = Supplier.posts
    }

    fun getPosts(): LiveData<List<Post>> {
        return _posts
    }

    object Supplier {
        val posts = listOf(
            Post("Post 1", "time 1"),
            Post("Post 2", "time 2"),
            Post("Post 3", "time 3"),
            Post("Post 4", "time 4"),
            Post("Post 5", "time 5"),
            Post("Post 6", "time 6"),
            Post("Post 7", "time 7"),
            Post("Post 8", "time 8"),
            Post("Post 9", "time 9"),
            Post("Post 10", "time 10"),
            Post("Post 11", "time 11"),
            Post("Post 12", "time 12"),
            Post("Post 13", "time 13")

        )
    }


}