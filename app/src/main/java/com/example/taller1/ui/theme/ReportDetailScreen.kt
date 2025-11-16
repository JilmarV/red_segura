package com.example.taller1.ui.theme

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Verified
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.taller1.model.Report
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File

@Composable
fun ReportDetailScreen(navController: NavController, reportId: String) {

    val db = FirebaseFirestore.getInstance()

    var report by remember { mutableStateOf<Report?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

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
                    Text("Reporte no encontrado", color = Color.Gray)
                }
            }

            else -> {
                val r = report!!

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    Divider(color = Color.LightGray)

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(r.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Place,
                            contentDescription = null,
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Lat: ${r.location.latitud}, Lon: ${r.location.longitud}",
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        "Categoría: ${r.category}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        "Fecha: ${r.fecha}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Imagen
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
                                Icons.Default.Image,
                                contentDescription = "Imagen",
                                tint = Color.Gray,
                                modifier = Modifier.size(64.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        r.description,
                        fontSize = 14.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = "Estado",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(r.state.name, color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "Comentarios",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    Divider(color = Color.LightGray, modifier = Modifier.padding(vertical = 8.dp))

                    ComentarioItem(
                        nombre = "Carlos",
                        comentario = "Excelente reporte, gracias por compartir.",
                        hora = "10:23 a. m."
                    )
                    ComentarioItem(
                        nombre = "Laura",
                        comentario = "Esto debería ser atendido pronto.",
                        hora = "10:45 a. m."
                    )

                    Divider(color = Color.LightGray, modifier = Modifier.padding(top = 8.dp))

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Usuario",
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Añadir un comentario...", color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun ComentarioItem(nombre: String, comentario: String, hora: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Usuario",
            modifier = Modifier
                .size(36.dp)
                .padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5), shape = MaterialTheme.shapes.medium)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(hora, fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(comentario, fontSize = 14.sp)
        }
    }
}
