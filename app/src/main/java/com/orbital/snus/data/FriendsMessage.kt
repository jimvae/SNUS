package com.orbital.snus.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class FriendsMessage (var id: String? = null,
                           val sender: String? = null,
                           var reciever: String? = null,
                           var latestMessage: String? = null,
                           var date: Date? = null,
                           var rating: Int? = null) : Parcelable {
}