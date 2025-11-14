package com.example.taller1.firebase

import com.example.taller1.data.User
import com.example.taller1.model.Report
import com.example.taller1.model.ReportNotification
import com.example.taller1.model.ReportState
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

object FirestoreService {
    private val db = Firebase.firestore
    val usuarios = db.collection("usuarios")
    private val reportes = db.collection("reportes")
    private val notificaciones = db.collection("notificaciones")

    fun crear(usuario: User, onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        usuarios
            .add(usuario)
            .addOnSuccessListener { ref ->
                usuarios.document(ref.id).update("id", ref.id)
                onSuccess(ref.id)
            }
            .addOnFailureListener { onError(it) }
    }


    fun login(
        email: String,
        password: String,
        onSuccess: (User) -> Unit,
        onFailure: () -> Unit
    ) {
        usuarios
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val user = result.documents[0].toObject(User::class.java)
                    if (user != null) {
                        user.id = result.documents[0].id
                        onSuccess(user)
                    } else {
                        onFailure()
                    }
                } else {
                    onFailure()
                }
            }
            .addOnFailureListener {
                onFailure()
            }
    }

    fun getReports(
        onSuccess: (List<Report>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        reportes
            .get()
            .addOnSuccessListener { result ->
                val reports = result.mapNotNull { it.toObject(Report::class.java) }
                onSuccess(reports)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getPendingReports(
        onSuccess: (List<Report>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        reportes
            .whereEqualTo("state", ReportState.PENDING.name) // campo "state" en Firestore
            .get()
            .addOnSuccessListener { result ->
                val reports = result.mapNotNull { it.toObject(Report::class.java) }
                onSuccess(reports)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun verifyReport(
        reportId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        reportes
            .document(reportId)
            .update(
                mapOf(
                    "state" to ReportState.ACCEPTED.name,
                    "rejectionReason" to null // por si tenía un motivo previo
                )
            )
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun rejectReport(
        reportId: String,
        motivo: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        reportes
            .document(reportId)
            .update(
                mapOf(
                    "state" to ReportState.REJECTED.name,
                    "rejectionReason" to motivo
                )
            )
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun createReportResultNotification(
        userId: String,
        reportId: String,
        newState: ReportState,
        motivo: String? = null,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val mensaje = when (newState) {
            ReportState.ACCEPTED ->
                "Tu reporte ha sido verificado. ¡Gracias por tu ayuda!"
            ReportState.REJECTED ->
                "Tu reporte fue rechazado. Motivo: ${motivo ?: "Sin motivo especificado"}"
            ReportState.PENDING ->
                "Tu reporte está en revisión."
        }

        val data = mapOf(
            "userId" to userId,
            "reportId" to reportId,
            "state" to newState.name,
            "message" to mensaje,
            "motivo" to motivo,
            "timestamp" to System.currentTimeMillis()
        )

        notificaciones
            .add(data)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
    fun getUserNotifications(
        userId: String,
        onSuccess: (List<ReportNotification>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        notificaciones
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val list = result.mapNotNull { it.toObject(ReportNotification::class.java) }
                onSuccess(list)
            }
            .addOnFailureListener { onFailure(it) }
    }

}
