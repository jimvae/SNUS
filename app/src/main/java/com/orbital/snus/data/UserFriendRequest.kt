package com.orbital.snus.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserFriendRequest(val from: String? = null, val to:String? = null) : Parcelable {
}