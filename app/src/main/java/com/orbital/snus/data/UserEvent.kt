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
}