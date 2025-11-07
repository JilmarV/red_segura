package com.example.taller1.ui.theme

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.DatePicker
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventScreen() {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = remember { SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()) }

    var titulo by remember { mutableStateOf("Evento 1") }
    var descripcion by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf(dateFormat.format(calendar.time)) }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }

    // Mostrar el DatePickerDialog
    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                calendar.set(year, month, day)
                fecha = dateFormat.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Eventos",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Titulo del evento", color = Color.Red)
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Descripción", color = Color.Red)
        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Descripción del incidente") },
            maxLines = 3
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text("Fecha del evento", color = Color.Red)
        Box(modifier = Modifier
            .fillMaxWidth()
            .clickable { datePickerDialog.show() }) {
            OutlinedTextField(
                value = fecha,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                },
                readOnly = true,
                enabled = false,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    disabledTextColor = LocalContentColor.current,
                    disabledBorderColor = MaterialTheme.colorScheme.outline,
                    disabledTrailingIconColor = LocalContentColor.current,
                    disabledLabelColor = LocalContentColor.current
                )
            )
        }


        Spacer(modifier = Modifier.height(8.dp))

        Text("Imagen", color = Color.Red)

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()
        ) { uri: Uri? ->
            imagenUri = uri
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clickable { launcher.launch("image/*") }
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            if (imagenUri != null) {
                AsyncImage(
                    model = imagenUri,
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "Seleccionar imagen",
                    modifier = Modifier.size(64.dp)
                )
            }
        }


        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* Acción para crear evento */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF8485E))
        ) {
            Text("Crear", color = Color.White, fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateEventScreenPreview() {
    CreateEventScreen()
}
