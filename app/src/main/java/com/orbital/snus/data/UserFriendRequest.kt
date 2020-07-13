package com.orbital.snus.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserFriendRequest(var id: String? = null, val fromID: String? = null, val toID:String? = null,
                             val fromName: String? = null, val fromCourse: String? = null,
                             var picUri: String? = null) : Parcelable {
}