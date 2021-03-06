package com.orbital.snus.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class Module(val acadYear: String? = null,
                  val preclusion: String? = null,
                  val description: String? = null,
                  val title: String? = null,
                  val department: String? = null,
                  val faculty: String? = null,
                  val workload: List<Double>? = null,
                  val prerequisite: String? = null,
                  val moduleCredit: String? = null,
                  val moduleCode: String? = null,
//                  val semesterData: Any? = null,
                  val lessons: List<String>? = null,
                  val prereqTree: Any? = null,
                  val fulfillRequirements: List<String>? = null) {

    override fun toString(): String {
        return "acadYear " + acadYear + "\n" +
                "preclusion " + preclusion + "\n" +
                "title " + title + "\n" +
                "department " + department + "\n" +
                "faculty " + faculty + "\n" +
                "workload " + workload + "\n" +
                "prerequisite " + prerequisite + "\n" +
                "moduleCredit " + moduleCredit + "\n" +
                "moduleCode " + moduleCode + "\n" +
//                "semesterData " + semesterData + "\n" +
                "lessons: " + lessons + "\n" +
                "prereqTree " + prereqTree + "\n" +
                "fulfillRequirements " + fulfillRequirements + "\n"
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        } else if (other is Module) {
            return this.moduleCode.equals((other as Module).moduleCode)
        }
        return false
    }

}