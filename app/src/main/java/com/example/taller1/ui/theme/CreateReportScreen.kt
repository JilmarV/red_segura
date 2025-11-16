package com.example.taller1.ui.theme

import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.taller1.R
import com.example.taller1.data.UserSession
import com.example.taller1.model.Category
import com.example.taller1.model.Location
import com.example.taller1.model.Report
import com.example.taller1.model.ReportState
import com.google.firebase.firestore.FirebaseFirestore
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material.CircularProgressIndicator

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateReportScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(Category.OTROS) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Imagen
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var capturedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    // Scroll
    val scrollState = rememberScrollState()

    // Mapa
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(-75.6673462, 4.5377869)) // centro aproximado
            zoom(12.0)
        }
    }

    var markerResourceId by remember { mutableStateOf(R.drawable.red_marker) }
    val marker = rememberIconImage(
        key = markerResourceId,
        painter = painterResource(markerResourceId)
    )

    var clickedPoint by remember { mutableStateOf<Point?>(null) }

    // Launchers para galería y cámara
    val pickImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImageUri = it
                capturedImageBitmap = null // limpiamos foto de cámara si se escoge galería
            }
        }

    val takePhotoLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                capturedImageBitmap = it
                selectedImageUri = null // limpiamos uri si se toma foto
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Crear Reporte", style = MaterialTheme.typography.h5)

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Categoría:")
        DropdownMenuBox(
            selected = category,
            onSelected = { category = it }
        )

        MapboxMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp),
            mapViewportState = mapViewportState,
            onMapClickListener = { point ->
                clickedPoint = point
                true
            }
        ) {
            clickedPoint?.let {
                PointAnnotation(point = it) {
                    iconImage = marker
                }
            }
        }

        // Diálogo para escoger origen de imagen
        if (showDialog) {
            ImagePickerDialog(
                onDismiss = { showDialog = false },
                onPickGallery = { pickImageLauncher.launch("image/*") },
                onTakePhoto = { takePhotoLauncher.launch(null) } // TakePicturePreview usa Void? → null
            )
        }

        // Caja de imagen
        ImagePickerBox(
            selectedImageUri = selectedImageUri,
            capturedImageBitmap = capturedImageBitmap,
            onImageClick = { showDialog = true }
        )

        if (showError) {
            Text(
                text = errorMessage ?: "Por favor completa todos los campos.",
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (isSaving) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                showError = false
                errorMessage = null

                if (title.isBlank() || description.isBlank()) {
                    showError = true
                    errorMessage = "Por favor completa título y descripción."
                    return@Button
                }

                if (selectedImageUri == null && capturedImageBitmap == null) {
                    showError = true
                    errorMessage = "Debes adjuntar al menos una imagen."
                    return@Button
                }

                isSaving = true

                val reporteId = db.collection("reportes").document().id
                val fechaActual = LocalDateTime.now()
                val fechaFormato =
                    fechaActual.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))

                // Guardar SOLO LOCAL la imagen (sin Firebase Storage)
                val imagePaths: List<String> =
                    when {
                        selectedImageUri != null -> {
                            // Guardamos el URI de la galería como String
                            listOf(selectedImageUri.toString())
                        }

                        capturedImageBitmap != null -> {
                            // Guardamos el Bitmap en un archivo interno y guardamos la ruta
                            try {
                                val filename =
                                    "report_${reporteId}_${System.currentTimeMillis()}.jpg"
                                val file = File(context.filesDir, filename)
                                FileOutputStream(file).use { out ->
                                    capturedImageBitmap!!.compress(
                                        Bitmap.CompressFormat.JPEG,
                                        90,
                                        out
                                    )
                                }
                                listOf(file.absolutePath)
                            } catch (e: Exception) {
                                isSaving = false
                                showError = true
                                errorMessage =
                                    "No se pudo guardar la imagen localmente: ${e.message}"
                                return@Button
                            }
                        }

                        else -> emptyList()
                    }

                // Ubicación: punto clickeado o fallback Bogotá
                val location = clickedPoint?.let {
                    Location(
                        latitud = it.latitude(),
                        longitud = it.longitude()
                    )
                } ?: Location(4.6097, -74.0818)

                val nuevoReporte = Report(
                    id = reporteId,
                    title = title,
                    description = description,
                    state = ReportState.PENDING,
                    images = imagePaths, // rutas locales / URIs
                    location = location,
                    fecha = fechaFormato,
                    userId = UserSession.currentUser.id,
                    category = category
                )

                val map = hashMapOf(
                    "id" to nuevoReporte.id,
                    "title" to nuevoReporte.title,
                    "description" to nuevoReporte.description,
                    "state" to nuevoReporte.state.name,
                    "images" to nuevoReporte.images,
                    "location" to hashMapOf(
                        "latitud" to nuevoReporte.location.latitud,
                        "longitud" to nuevoReporte.location.longitud
                    ),
                    "fecha" to nuevoReporte.fecha,
                    "userId" to nuevoReporte.userId,
                    "category" to nuevoReporte.category.name
                )

                db.collection("reportes")
                    .document(reporteId)
                    .set(map)
                    .addOnSuccessListener {
                        isSaving = false
                        navController.popBackStack()
                    }
                    .addOnFailureListener { e ->
                        isSaving = false
                        showError = true
                        errorMessage =
                            e.message ?: "Error guardando el reporte en Firestore."
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            )
        ) {
            Text("Guardar Reporte")
        }
    }
}

@Composable
fun DropdownMenuBox(
    selected: Category,
    onSelected: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red,
                contentColor = Color.White
            )
        ) {
            Text(text = selected.name)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Category.entries.forEach {
                DropdownMenuItem(
                    onClick = {
                        onSelected(it)
                        expanded = false
                    }
                ) {
                    Text(text = it.name)
                }
            }
        }
    }
}

@Composable
fun ImagePickerDialog(
    onDismiss: () -> Unit,
    onPickGallery: () -> Unit,
    onTakePhoto: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Seleccionar Imagen") },
        text = { Text(text = "Elige si deseas cargar una imagen desde la galería o tomar una foto.") },
        confirmButton = {
            TextButton(onClick = {
                onPickGallery()
                onDismiss()
            }) {
                Text("Galería")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onTakePhoto()
                onDismiss()
            }) {
                Text("Cámara")
            }
        }
    )
}

@Composable
fun ImagePickerBox(
    selectedImageUri: Uri?,
    capturedImageBitmap: Bitmap?,
    onImageClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(Color.LightGray)
            .clickable { onImageClick() }
    ) {
        when {
            selectedImageUri != null -> {
                Image(
                    painter = rememberAsyncImagePainter(selectedImageUri),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Inside
                )
            }

            capturedImageBitmap != null -> {
                Image(
                    bitmap = capturedImageBitmap.asImageBitmap(),
                    contentDescription = "Foto tomada",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Inside
                )
            }

            else -> {
                Icon(
                    imageVector = Icons.Rounded.AddPhotoAlternate,
                    contentDescription = "Agregar imagen",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(100.dp)
                )
            }
        }
    }
}
