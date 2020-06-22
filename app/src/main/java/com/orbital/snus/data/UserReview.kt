package com.orbital.snus.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class UserReview (var id: String? = null, val userID: String? = null,  val title: String? = null, val date: Date? = null, val rating: Int? = null,
    val expectedGrade: String? = null, val actualGrade: String? = null, val commitment: String? = null,
    val workload: String? = null, val prof: String? = null, val description: String? = null) : Parcelable {
    // Under each Module, users can only add one review, so the ID is the userID
    // ratings is out of 5
    // grades should be enum? B, B+, A- etc
    // commitment is number of hrs a week?
    // Workload is low, medium, high?
}