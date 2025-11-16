package com.example.taller1.ui.theme

import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.taller1.firebase.FirestoreService
import com.example.taller1.model.Report
import com.example.taller1.model.ReportState
import java.io.File

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController) {
    var reportes by remember { mutableStateOf<List<Report>>(emptyList()) }
    val context = LocalContext.current

    LaunchedEffect(true) {
        FirestoreService.getReports(
            onSuccess = { reportList ->
                reportes = reportList.filter { it.state == ReportState.ACCEPTED }
            },
            onFailure = {
                Toast.makeText(context, "Error al obtener reportes", Toast.LENGTH_SHORT).show()
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color.White,
                elevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Reportes ciudadanos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red),
                        shape = MaterialTheme.shapes.medium,
                        elevation = ButtonDefaults.elevation(4.dp)
                    ) {
                        Text("AYUDA", color = Color.White)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(reportes) { reporte ->
                PostItem(report = reporte, navController = navController)
            }
        }
    }
}

@Composable
fun PostItem(report: Report, navController: NavController) {

    val (statusText, statusColor, statusIcon) = when (report.state) {
        ReportState.PENDING -> Triple("Pendiente", Color(0xFFFFA000), Icons.Default.HourglassEmpty)
        ReportState.ACCEPTED -> Triple("Aprobado", Color(0xFF4CAF50), Icons.Default.CheckCircle)
        ReportState.REJECTED -> Triple("Rechazado", Color(0xFFE53935), Icons.Default.Close)
    }

    val creatorName = if (!report.userName.isNullOrBlank()) {
        report.userName
    } else {
        "Usuario: ${report.userId}"
    }

    Card(
        elevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("detalle_reporte/${report.id}")
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text(
                        text = report.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Usuario",
                            tint = Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = creatorName,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.LocationOn,
                            contentDescription = "Ubicación",
                            tint = Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Lat: ${report.location.latitud}, Lon: ${report.location.longitud}",
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        "Categoría: ${report.category}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            color = statusColor.copy(alpha = 0.12f),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = "Estado",
                        tint = statusColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = statusText,
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                val firstImage = report.images.firstOrNull()

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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = report.description,
                fontSize = 14.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Justify,
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = report.fecha,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = "ID: ${report.id}",
                    fontSize = 10.sp,
                    color = Color.LightGray
                )
            }
        }
    }
}
