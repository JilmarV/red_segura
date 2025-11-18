// kotlin
package com.example.taller1.firebase

import com.example.taller1.data.User
import com.example.taller1.model.Report
import com.example.taller1.model.ReportNotification
import com.example.taller1.model.ReportState
import com.example.taller1.model.Comment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger
import kotlin.text.get

object FirestoreService {
    private val db = Firebase.firestore
    val usuarios = db.collection("usuarios")
    private val reportes = db.collection("reportes")
    private val notificaciones = db.collection("notificaciones")
    private val comentarios = db.collection("comentarios")

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

    fun getUserById(
        userId: String,
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        usuarios
            .document(userId)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val user = doc.toObject(User::class.java)
                    if (user != null) {
                        user.id = doc.id
                        onSuccess(user)
                    } else {
                        onFailure(Exception("Error al convertir documento a User"))
                    }
                } else {
                    onFailure(Exception("Usuario no encontrado"))
                }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun getReports(
        onSuccess: (List<Report>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        reportes
            .get()
            .addOnSuccessListener { result ->
                val reports = result.mapNotNull { it.toObject(Report::class.java) }

                if (reports.isEmpty()) {
                    onSuccess(reports)
                    return@addOnSuccessListener
                }

                val remaining = AtomicInteger(reports.size)

                for (r in reports) {
                    val uid = r.userId
                    if (uid.isBlank()) {
                        r.userName = "Usuario: ${r.userId}"
                        if (remaining.decrementAndGet() == 0) onSuccess(reports)
                        continue
                    }

                    getUserById(uid, { user ->
                        r.userName = user.name
                        if (remaining.decrementAndGet() == 0) onSuccess(reports)
                    }, {
                        // fallback si no se puede obtener el usuario
                        r.userName = "Usuario: ${r.userId}"
                        if (remaining.decrementAndGet() == 0) onSuccess(reports)
                    })
                }
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

                if (reports.isEmpty()) {
                    onSuccess(reports)
                    return@addOnSuccessListener
                }

                val remaining = AtomicInteger(reports.size)

                for (r in reports) {
                    val uid = r.userId
                    if (uid.isBlank()) {
                        r.userName = "Usuario: ${r.userId}"
                        if (remaining.decrementAndGet() == 0) onSuccess(reports)
                        continue
                    }

                    getUserById(uid, { user ->
                        r.userName = user.name
                        if (remaining.decrementAndGet() == 0) onSuccess(reports)
                    }, {
                        // fallback si no se puede obtener el usuario
                        r.userName = "Usuario: ${r.userId}"
                        if (remaining.decrementAndGet() == 0) onSuccess(reports)
                    })
                }
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

    fun createComment(
        content: String,
        userId: String,
        reportId: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fechaString = dateFormat.format(Date(System.currentTimeMillis()))

        val data = mapOf(
            "content" to content,
            "userId" to userId,
            "reportId" to reportId,
            "fecha" to fechaString
        )

        comentarios
            .add(data)
            .addOnSuccessListener { ref ->
                comentarios.document(ref.id).update("id", ref.id)
                onSuccess(ref.id)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun getCommentsForReport(
        reportId: String,
        onSuccess: (List<Comment>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        comentarios
            .whereEqualTo("reportId", reportId)
            .get()
            .addOnSuccessListener { result ->
                val list = result.mapNotNull { it.toObject(Comment::class.java) }.toMutableList()
                if (list.isEmpty()) {
                    onSuccess(emptyList())
                    return@addOnSuccessListener
                }

                val remaining = AtomicInteger(list.size)

                list.forEach { comment ->
                    val uid = comment.userId
                    if (uid.isBlank()) {
                        comment.userName = "Usuario: ${comment.userId}"
                        if (remaining.decrementAndGet() == 0) onSuccess(list)
                        return@forEach
                    }

                    getUserById(
                        userId = uid,
                        onSuccess = { user ->
                            comment.userName = user.name
                            if (remaining.decrementAndGet() == 0) onSuccess(list)
                        },
                        onFailure = {
                            comment.userName = "Usuario: ${comment.userId}"
                            if (remaining.decrementAndGet() == 0) onSuccess(list)
                        }
                    )
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}
