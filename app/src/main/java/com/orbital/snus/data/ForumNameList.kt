package com.orbital.snus.data

data class ForumNameList(var map: HashMap<String, String>? = null) {

    fun checkIfForumNameIsTaken(newForumName: String): Boolean {
        return map!!.containsValue(newForumName)
    }

    fun updateForumName(newForumName: String, userID: String) {
        map!![userID] = newForumName
    }
}