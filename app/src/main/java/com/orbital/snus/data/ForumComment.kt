package com.orbital.snus.data

import java.util.*

data class ForumComment (val id: String, val date: Date, val text: String) {
    // each user should have a unique ID for all their comments in a particular ForumPost
    // need think how to generate for each ForumPost
}