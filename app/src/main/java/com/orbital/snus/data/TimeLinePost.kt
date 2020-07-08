package com.orbital.snus.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class TimeLinePost (var id: String? = null, var title: String? = null, var date: Date? = null,
                      var isMilestone: Boolean? = null, var details: String? = null, var friends: ArrayList<UserData>? = null) : Parcelable {
    // Each ForumPost has a unique ID inside the thread
    // Add in image later
    // Add in suggested answer

}