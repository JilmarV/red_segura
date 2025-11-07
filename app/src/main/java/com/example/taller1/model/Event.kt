package com.example.taller1.model

import com.example.taller1.data.User
import java.time.LocalDateTime

class Event(
    var id: String,
    var userId: String,
    var name: String,
    var description: String,
    var fecha: LocalDateTime,) {
}