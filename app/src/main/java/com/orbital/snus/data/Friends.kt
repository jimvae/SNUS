package com.orbital.snus.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Friends(var friendshipID: String? = null,
                   val friendID: String? = null, var friendName: String? = null,
                     var course: String? = null) : Parcelable {
}