package com.orbital.snus.data

import java.util.*

data class UserEvent(
    var eventName: String, var eventDescription: String,
    var startDate: Date, var endDate: Date,
    var location: String, var addToTimeline: Boolean) {

    fun updateEvent(eventName: String, eventDescription: String,
                    startDate: Date, endDate: Date,
                    location: String, addToTimeline: Boolean) {
        this.eventName = eventName
        this.eventDescription = eventDescription
        this.startDate = startDate
        this.endDate = endDate
        this.location = location
        this.addToTimeline = addToTimeline
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        } else {
            if (other == this) {
                return true
            } else if (other is UserEvent) {
                return this.eventName == other.eventName &&
                        this.eventDescription == other.eventDescription &&
                        this.startDate == other.startDate &&
                        this.endDate == other.endDate &&
                        this.location == other.location &&
                        this.addToTimeline == other.addToTimeline
            } else {
                return false
            }
        }
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}