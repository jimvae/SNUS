package com.orbital.snus.data

import java.util.*

data class UserEvent(
    var eventName: String? = null, var eventDescription: String? = null,
    var startDate: Date? = null, var endDate: Date? = null,
    var location: String? = null, var addToTimeline: Boolean? = null,
    var id: String? = null) {



    fun updateEvent(eventName: String,
                    eventDescription: String,
                    startDate: Date, endDate: Date,
                    location: String, addToTimeline: Boolean) {
        this.eventName = eventName
        this.eventDescription = eventDescription
        this.startDate = startDate
        this.endDate = endDate
        this.location = location
        this.addToTimeline = addToTimeline
    }

//    override fun equals(other: Any?): Boolean {
//        if (other == null) {
//            return false
//        } else {
//            if (other == this) {
//                return true
//            } else if (other is UserEvent) {
//                return this.eventName.equals(other.eventName) &&
//                        this.eventDescription.equals(other.eventDescription) &&
//                        this.startDate.equals(other.startDate) &&
//                        this.endDate.equals(other.endDate) &&
//                        this.location.equals(other.location) &&
//                        this.addToTimeline.equals(other.addToTimeline)
//            } else {
//                return false
//            }
//        }
//    }
//
//    override fun hashCode(): Int {
//        return super.hashCode()
//    }
}