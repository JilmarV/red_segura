package com.example.taller1.data

object UserSession {
    var currentUser: User = User()

    fun isLoggedIn(): Boolean {
        return currentUser.email.isNotBlank()
    }

    fun logout() {
        currentUser = User()
    }
}