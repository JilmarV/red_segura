package com.example.taller1.ui.theme

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.taller1.data.UserSession
import com.example.taller1.firebase.FirestoreService
import com.example.taller1.model.Comment
import com.example.taller1.model.Report
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ReportDetailScreen(navController: NavController, reportId: String) {

    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var report by remember { mutableStateOf<Report?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Comentarios
    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var commentsLoading by remember { mutableStateOf(true) }
    var newComment by remember { mutableStateOf("") }
    var isPosting by remember { mutableStateOf(false) }

    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }

    fun loadComments() {
        commentsLoading = true
        FirestoreService.getCommentsForReport(
            reportId,
            onSuccess = { list ->
                comments = list
                commentsLoading = false
            },
            onFailure = { e ->
                comments = emptyList()
                commentsLoading = false
                Toast.makeText(context, "Error al cargar comentarios: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    LaunchedEffect(reportId) {
        db.collection("reportes")
            .document(reportId)
            .get()
            .addOnSuccessListener { doc ->
                val r = doc.toObject(Report::class.java)
                report = r
                isLoading = false
            }
            .addOnFailureListener { e ->
                error = e.message ?: "Error al cargar el reporte"
                isLoading = false
            }

        loadComments()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color.White,
                elevation = 0.dp,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.Red
                        )
                    }
                },
                title = {
                    Text(
                        text = "Detalle del Reporte",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            )
        }
    ) { padding ->

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = error ?: "Error", color = Color.Red)
                }
            }

            report == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Reporte no encontrado")
                }
            }

            else -> {
                val r = report!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .padding(padding),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = r.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        val firstImage = r.images.firstOrNull()
                        if (firstImage != null) {
                            val model: Any =
                                if (firstImage.startsWith("content://") || firstImage.startsWith("file://")) {
                                    Uri.parse(firstImage)
                                } else {
                                    File(firstImage)
                                }

                            Image(
                                painter = rememberAsyncImagePainter(model),
                                contentDescription = "Imagen del reporte",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = "Imagen",
                                modifier = Modifier.size(64.dp),
                                tint = Color.Gray
                            )
                        }
                    }

                    Text(text = r.description)

                    // Comentarios - lista
                    Text(text = "Comentarios", fontWeight = FontWeight.SemiBold)
                    if (commentsLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        if (comments.isEmpty()) {
                            Text(text = "No hay comentarios", color = Color.Gray)
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 0.dp, max = 240.dp)
                            ) {
                                items(comments) { c ->
                                    val hora = c.fecha
                                    ComentarioItem(nombre = c.userName, comentario = c.content, hora = hora)
                                    Divider()
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newComment,
                            onValueChange = { newComment = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("Escribe un comentario") },
                            enabled = !isPosting
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (newComment.isBlank()) return@Button
                                isPosting = true
                                val currentUserId = UserSession.currentUser.id
                                FirestoreService.createComment(
                                    content = newComment,
                                    userId = currentUserId,
                                    reportId = reportId,
                                    onSuccess = { _ ->
                                        isPosting = false
                                        newComment = ""
                                        loadComments()
                                        Toast.makeText(context, "Comentario enviado", Toast.LENGTH_SHORT).show()
                                    },
                                    onFailure = { _ ->
                                        isPosting = false
                                        Toast.makeText(context, "Error al enviar comentario", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            enabled = !isPosting,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Red
                            )
                        ) {
                            Text(if (isPosting) "Enviando..." else "Enviar",
                                color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ComentarioItem(nombre: String, comentario: String, hora: String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                if (hora.isNotEmpty()) {
                    Text(text = hora, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = comentario)
    }
}
