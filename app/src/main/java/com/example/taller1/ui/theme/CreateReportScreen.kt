package com.example.taller1.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.taller1.data.UserSession
import com.example.taller1.model.*
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.ui.res.painterResource
import coil.compose.rememberAsyncImagePainter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.example.taller1.R
import com.mapbox.maps.extension.compose.annotation.rememberIconImage

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateReportScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(Category.OTROS) }
    var showError by remember { mutableStateOf(false) }
    // Estado para almacenar una única imagen
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var capturedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    // Lanzadores de actividad para abrir la galería y la cámara
    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it // Guarda la URI seleccionada
        }
    }

    val takePhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            capturedImageBitmap = it // Guarda el bitmap capturado
        }
    }

    var mapViewportState = rememberMapViewportState(){
        setCameraOptions {
            center(Point.fromLngLat(-75.6673462, 4.5377869))
            zoom(12.0)
        }
    }

    var markerResourceId by remember {
        mutableStateOf(R.drawable.red_marker)
    }

    var marker = rememberIconImage( key = markerResourceId, painter = painterResource(markerResourceId))

    val scrollState = rememberScrollState()

    var clickedPoint by remember { mutableStateOf<Point?>(null) }

    Column(
        modifier = Modifier.
        fillMaxSize()
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
            onMapClickListener = {
                point ->
                clickedPoint = point
                true
            }
        ){
            clickedPoint?.let {
                PointAnnotation(
                    point = clickedPoint!!
                ){
                    iconImage = marker
                }
            }
        }

        // Imagen incidente (espacio para imagen)
        if (showDialog) {
            ImagePickerDialog(
                onDismiss = { showDialog = false },
                onPickGallery = { pickImageLauncher.launch("image/*") }, // Abre la galería
                onTakePhoto = { takePhotoLauncher.launch() } // Toma una foto con la cámara
            )
        }

        // Imagen seleccionada o capturada con la opción de cambiar
        ImagePickerBox(
            selectedImageUri = selectedImageUri,
            capturedImageBitmap = capturedImageBitmap,
            onImageClick = { showDialog = true } // Abrir el diálogo para cambiar la imagen
        )

        if (showError) {
            Text(
                text = "Por favor completa todos los campos.",
                color = MaterialTheme.colors.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Button(
            onClick = {
                if (title.isBlank() || description.isBlank()) {
                    showError = true
                    return@Button
                }

                val reporteId = db.collection("reportes").document().id
                val fechaActual = LocalDateTime.now()
                val fechaFormato = fechaActual.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))

                val nuevoReporte = Report(
                    id = reporteId,
                    title = title,
                    description = description,
                    state = ReportState.PENDING,
                    images = emptyList(),
                    location = Location(4.6097, -74.0818),
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
                    "fecha" to nuevoReporte.fecha.toString(),
                    "userId" to nuevoReporte.userId,
                    "category" to nuevoReporte.category.name
                )

                db.collection("reportes")
                    .document(reporteId)
                    .set(map)
                    .addOnSuccessListener {
                        navController.popBackStack()
                    }
                    .addOnFailureListener {
                        showError = true
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Red, // Color primario
                contentColor = Color.White // Color del texto
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
                containerColor = Color.Red, // Color primario
                contentColor = Color.White // Color del texto
            )
        ) {
            Text(text = selected.name)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            Category.entries.forEach {
                DropdownMenuItem(onClick = {
                    onSelected(it)
                    expanded = false
                }) {
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
            .height(200.dp) // Ajusta el alto para darle más espacio a la imagen
            .background(Color.LightGray)
            .clickable {
                onImageClick()
            }
    ) {
        if (selectedImageUri != null) {
            // Mostrar la imagen seleccionada desde la galería
            Image(
                painter = rememberAsyncImagePainter(selectedImageUri),
                contentDescription = "Imagen seleccionada",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Inside // La imagen se ajusta sin sobrepasar los límites del contenedor
            )
        } else if (capturedImageBitmap != null) {
            // Mostrar la imagen capturada por la cámara
            Image(
                painter = rememberAsyncImagePainter(selectedImageUri),
                contentDescription = "Imagen seleccionada",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Inside // La imagen se ajusta sin sobrepasar los límites del contenedor
            )

        } else {
            // Mostrar el ícono para agregar una imagen
            Icon(
                imageVector = Icons.Rounded.AddPhotoAlternate,
                contentDescription = "Agregar imagen",
                modifier = Modifier.align(Alignment.Center)
                    .size(100.dp)
            )
        }
    }
}
