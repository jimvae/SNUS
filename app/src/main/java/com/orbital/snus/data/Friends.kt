package com.orbital.snus.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Friends(val friendID: String? = null, var friendName: String? = null,
                    var faculty: String? = null, var course: String? = null) : Parcelable {
}