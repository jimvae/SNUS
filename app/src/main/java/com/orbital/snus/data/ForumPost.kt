package com.orbital.snus.data

import java.util.*

data class ForumPost (val userid: String? = null, var id: String? = null,
                      var title: String? = null, var date: Date? = null
                      , var status: Boolean? = null, var question: String? = null) {
    // Each ForumPost has a unique ID inside the thread
    // Add in image later
    // Add in suggested answer
}