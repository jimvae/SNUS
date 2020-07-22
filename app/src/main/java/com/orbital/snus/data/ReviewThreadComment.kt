package com.orbital.snus.data

import java.util.*

data class ReviewThreadComment (var threadID: String? = null,
                                var userID: String? = null,
                                val date: Date? = null,
                                val text: String? = null) {
}