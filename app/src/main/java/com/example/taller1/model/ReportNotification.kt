package com.example.taller1.model

data class ReportNotification(
    var userId: String = "",
    var reportId: String = "",
    var state: String = "",
    var message: String = "",
    var motivo: String? = null,
    var timestamp: Long = 0L
)
