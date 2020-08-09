package com.orbital.snus.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class UserReview (var id: String? = null,
                       val userID: String? = null,
                       var title: String? = null,
                       var date: Date? = null,
                       var rating: Int? = null,
                       var expectedGrade: String? = null,
                       var actualGrade: String? = null,
                       var commitment: String? = null,
                       var workload: String? = null,
                       var prof: String? = null,
                       var description: String? = null) : Parcelable {
    // Under each Module, users can only add one review, so the ID is the userID
    // ratings is out of 5
    // grades should be enum? B, B+, A- etc
    // commitment is number of hrs a week?
    // Workload is low, medium, high?

    fun updateReview(title: String, date: Date, rating: Int,
                     expectedGrade: String, actualGrade: String,
                     commitment: String, workload: String,
                     prof: String, description: String) {
        this.title = title
        this.date = date
        this.rating = rating
        this.expectedGrade = expectedGrade
        this.actualGrade = actualGrade
        this.commitment = commitment
        this.workload = workload
        this.prof = prof
        this.description = description
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        } else if (other is UserReview) {
            return other.id!!.equals(this.id!!)
        }
        return false
    }
}