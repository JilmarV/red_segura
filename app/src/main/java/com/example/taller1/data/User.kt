package com.example.taller1.data

import com.example.taller1.model.Location
import com.example.taller1.model.Role

data class User (
    var id : String = "",
    val name: String = "",
    val address: String = "",
    val email: String = "",
    val password: String = "",
    var location: Location = Location(0.0, 0.0),
    val role: Role = Role.CLIENT
) {
}