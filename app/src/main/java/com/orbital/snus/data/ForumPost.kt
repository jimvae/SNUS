package com.orbital.snus.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ForumPost (val userid: String? = null,
                      var id: String? = null,
                      var title: String? = null,
                      var date: Date? = null,
                      var status: Boolean? = null,
                      var question: String? = null,
                      var photoURL: String? = null) : Parcelable {
    // Each ForumPost has a unique ID inside the thread
    // Add in image later
    // Add in suggested answer

    fun updatePost(title: String, question: String, photoURL: String?) {
        this.title = title
        this.question = question
        this.date = Calendar.getInstance().time
        this.photoURL = photoURL
    }

    fun resolvedPost() {
        this.status = true
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        } else if (other is ForumPost) {
            return other.id!!.equals(this.id)
        }
        return false
    }
}