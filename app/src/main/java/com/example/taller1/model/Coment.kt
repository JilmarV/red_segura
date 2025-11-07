package com.example.taller1.model;

import java.time.LocalDateTime

 class Coment(
    var id: String,
    var content: String,
    var userId: String,
    var reportId: String,
    var likes: Int,
    var fecha: LocalDateTime,
) {
}
