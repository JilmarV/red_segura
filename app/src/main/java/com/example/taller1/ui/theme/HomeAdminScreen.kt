package com.example.taller1.ui.theme

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.layout.ContentScale
import com.example.taller1.firebase.FirestoreService
import com.example.taller1.model.Report
import com.example.taller1.model.ReportState
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun HomeAdminScreen(navController: NavHostController) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val sideMenuAdmin = SideMenuAdmin()

    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val reports = remember { mutableStateListOf<Report>() }
    val snackbarHostState = scaffoldState.snackbarHostState

    LaunchedEffect(Unit) {
        FirestoreService.getPendingReports(
            onSuccess = { list ->
                reports.clear()
                reports.addAll(list)
                loading = false
            },
            onFailure = { e ->
                errorMessage = e.message ?: "Error al cargar reportes"
                loading = false
            }
        )
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            sideMenuAdmin.TopBar(
                scope = scope,
                scaffoldState = scaffoldState,
                title = "Reportes pendientes"
            )
        },
        drawerContent = {
            sideMenuAdmin.Drawer(
                navController = navController,
                scope = scope,
                scaffoldState = scaffoldState
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                errorMessage != null -> {
                    Text(
                        text = errorMessage ?: "Error",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                reports.isEmpty() -> {
                    Text(
                        text = "No hay reportes pendientes",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        items(reports) { report ->
                            PostItemAdmin(
                                report = report,
                                navController = navController,
                                onVerify = {
                                    FirestoreService.verifyReport(
                                        reportId = report.id,
                                        onSuccess = {
                                            FirestoreService.createReportResultNotification(
                                                userId = report.userId,
                                                reportId = report.id,
                                                newState = ReportState.ACCEPTED,
                                                motivo = null,
                                                onSuccess = {},
                                                onFailure = {}
                                            )
                                            reports.remove(report)
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Reporte verificado")
                                            }
                                        },
                                        onFailure = { e ->
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    "Error al verificar: ${e.message}"
                                                )
                                            }
                                        }
                                    )
                                },
                                onReject = {
                                    navController.navigate("cancel_reason/${report.id}/${report.userId}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostItemAdmin(
    report: Report,
    navController: NavController,
    onVerify: () -> Unit,
    onReject: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        elevation = 6.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = report.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
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
                    Text(report.category.name, fontSize = 12.sp, color = Color.Gray)
                }

                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Opciones",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            expanded = false
                            onVerify()
                        }) {
                            Text("Verificar")
                        }
                        DropdownMenuItem(onClick = {
                            expanded = false
                            onReject()
                        }) {
                            Text("Rechazar")
                        }
                    }
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
                textAlign = TextAlign.Justify
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Verified,
                    contentDescription = "Estado",
                    tint = when (report.state) {
                        ReportState.PENDING -> Color.Gray
                        ReportState.ACCEPTED -> Color(0xFF4CAF50)
                        ReportState.REJECTED -> Color.Red
                    },
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(report.id, color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeAdminScreenPreview() {
    val navController = rememberNavController()
    HomeAdminScreen(navController = navController)
}
