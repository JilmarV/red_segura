package com.example.taller1.firebase

import com.example.taller1.data.User
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.example.taller1.data.UserSession
import com.example.taller1.model.Report
import com.google.firebase.firestore.FirebaseFirestore

object FirestoreService {
    private val db = Firebase.firestore
    val usuarios = db.collection("usuarios")

    fun crear(usuario: User, onSuccess: (String) -> Unit, onError: (Exception) -> Unit) {
        usuarios
            .add(usuario)
            .addOnSuccessListener { onSuccess(it.id) }
            .addOnFailureListener { onError(it) }
    }

    fun     login(email: String, password: String, onSuccess: (User) -> Unit, onFailure: () -> Unit) {
        usuarios
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val user = result.documents[0].toObject(User::class.java)
                    if (user != null) {
                        onSuccess(user)  // ✅ AQUÍ retornas el usuario, pero no instancias nada aquí
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
        FirebaseFirestore.getInstance()
            .collection("reportes")
            .get()
            .addOnSuccessListener { result ->
                val reports = result.mapNotNull { it.toObject(Report::class.java) }
                onSuccess(reports)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

}
