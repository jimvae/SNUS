package com.orbital.snus.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class UserEvent(
    var eventName: String? = null, var eventDescription: String? = null,
    var startDate: Date? = null, var endDate: Date? = null,
    var location: String? = null, var addToTimeline: Boolean? = null,
    var id: String? = null) : Parcelable {

    fun updateEvent(name: String, description: String, startDate: Date, endDate: Date, location: String) {
        this.eventName = name
        this.eventDescription = description
        this.startDate = startDate
        this.endDate = endDate
        this.location = location
    }
}
