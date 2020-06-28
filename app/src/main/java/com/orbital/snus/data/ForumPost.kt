package com.orbital.snus.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ForumPost (val userid: String? = null, var id: String? = null,
                      var title: String? = null, var date: Date? = null,
                      var status: Boolean? = null, var question: String? = null) : Parcelable {
    // Each ForumPost has a unique ID inside the thread
    // Add in image later
    // Add in suggested answer

    fun updatePost(title: String, question: String) {
        this.title = title
        this.question = question
        this.date = Calendar.getInstance().time
    }

    fun resolvedPost() {
        this.status = true
    }
}