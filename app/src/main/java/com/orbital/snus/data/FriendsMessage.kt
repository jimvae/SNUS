package com.orbital.snus.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class FriendsMessage (var id: String? = null,
                           val sender: String? = null,
                           var reciever: String? = null,
                           var latestMessage: String? = null,
                           var date: Date? = null) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        } else if (other is FriendsMessage) {
            val test = other as FriendsMessage
            return other.id.equals(id)
        }
        return false
    }
}