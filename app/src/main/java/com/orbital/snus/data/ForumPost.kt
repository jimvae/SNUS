package com.orbital.snus.data

import java.util.*

data class ForumPost (val userid: String, val id: String, val date: Date, val status: Boolean,
    val question: String, val answers: MutableList<ForumComment>) {
    // Each ForumPost has a unique ID inside the thread
    // Add in image later
    // Add in suggested answer
}