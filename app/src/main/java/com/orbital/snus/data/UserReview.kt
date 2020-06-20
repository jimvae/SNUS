package com.orbital.snus.data

import java.util.*

data class UserReview (val id: String, val date: Date, val rating: Int,
    val expectedGrade: String, val actualGrade: String, val commitment: String,
    val workload: String, val prof: String, val description: String) {
    // Under each Module, users can only add one review, so the ID is the userID
    // ratings is out of 5
    // grades should be enum? B, B+, A- etc
    // commitment is number of hrs a week?
    // Workload is low, medium, high?
}