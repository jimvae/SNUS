package com.orbital.snus.data

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserData(val userID: String? = null,
                    var fullname: String? = null,
                    var faculty: String? = null,
                    var course: String? = null,
                    var year: Int? = null,
                    var bio: String? = null,
                    var linkedIn: String? = null,
                    var insta: String? = null,
                    var git: String? = null,
                    var firstTime: Boolean? = null,
                    var picUri: String? = null,
                    var moduleList: ArrayList<String>? = null,
                    var forumName: String? = null) : Parcelable {

    // Need to add: Profile Photo, links for Github, Instagram and LinkedIn

    fun updateUserData(fullname: String,
                       faculty: String,
                       course: String,
                       year: Int,
                       bio: String,
                       linkedIn: String?,
                       insta: String?,
                       git: String?,
                       firstTime: Boolean,
                       picUri: String?,
                       moduleList: ArrayList<String>?,
                       forumName: String?) {
        this.fullname = fullname
        this.faculty = faculty
        this.course = course
        this.year = year
        this.bio = bio
        this.linkedIn = linkedIn
        this.insta = insta
        this.git = git
        this.firstTime = firstTime
        this.picUri = picUri
        this.moduleList = moduleList
        this.forumName = forumName
    }

    fun updateModules(moduleList: ArrayList<String>?) {
        this.moduleList = moduleList
    }
}