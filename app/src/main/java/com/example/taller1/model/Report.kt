package com.example.taller1.model

import com.google.firebase.Timestamp

class Report(
    var id: String = "",
    var title: String = "",
    var description: String = "",
    var state: ReportState = ReportState.PENDING,
    var images: List<String> = listOf(),
    var location: Location = Location(),
    var fecha: String = "",
    var userId: String = "",
    var category: Category = Category.OTROS,
    var rejectionReason: String? = null
)
