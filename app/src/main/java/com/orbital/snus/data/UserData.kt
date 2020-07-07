package com.orbital.snus.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

enum class FACULTY {SoC}
enum class COURSE {BA, CS, CEG, InfoSys, InfoSec}

@Parcelize
data class UserData(val userID: String?, var faculty: FACULTY?, var course: COURSE?, var year: Int?,
                    var bio: String?, var firstTime: Boolean?) : Parcelable {
    // Need to add: Profile Photo, links for Github, Instagram and LinkedIn
}