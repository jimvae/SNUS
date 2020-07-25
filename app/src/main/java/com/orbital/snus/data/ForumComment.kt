package com.orbital.snus.data

import java.util.*

data class ForumComment (var forumID: String? = null,
                         var userID: String? = null,
                         var forumName: String? = null,
                         val date: Date? = null,
                         val text: String? = null) {
    // each user should have a unique ID for all their comments in a particular ForumPost
    // need think how to generate for each ForumPost
}