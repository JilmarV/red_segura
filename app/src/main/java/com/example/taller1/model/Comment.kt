package com.example.taller1.model;

import java.time.LocalDateTime

data class Comment(
    var id: String = "",
    var content: String = "",
    var userId: String = "",
    var reportId: String = "",
    var fecha: String = "",
    var userName: String = ""
) {
}
