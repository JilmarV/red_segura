package com.example.taller1.ui.theme

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun ReportDetailScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color.White,
                elevation = 0.dp,
                navigationIcon = {
                    IconButton(onClick = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.Red)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Divider(color = Color.LightGray)

            Spacer(modifier = Modifier.height(16.dp))

            Text("Título del reporte", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ubicación", color = Color.Red, fontSize = 14.sp)
            }

            Text("Categoría", color = Color.Gray, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Color(0xFFE0E0E0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Image, contentDescription = "Imagen", tint = Color.Gray, modifier = Modifier.size(64.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "Este es un reporte generado por un ciudadano. Describe una situación ocurrida en la zona y debe ser atendida por las autoridades correspondientes.",
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
                    Icon(Icons.Default.Verified, contentDescription = "Verificado", tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("000", color = Color.Black)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = true,
                        onCheckedChange = {},
                        colors = CheckboxDefaults.colors(checkedColor = Color.Red)
                    )
                    Text("Es importante", color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Comentarios", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Divider(color = Color.LightGray, modifier = Modifier.padding(vertical = 8.dp))

            ComentarioItem(nombre = "Carlos", comentario = "Excelente reporte, gracias por compartir.", hora = "10:23 a. m.")
            ComentarioItem(nombre = "Laura", comentario = "Esto debería ser atendido pronto.", hora = "10:45 a. m.")
            ComentarioItem(nombre = "Mónica", comentario = "¿Hay alguna novedad sobre este caso?", hora = "11:10 a. m.")
            ComentarioItem(nombre = "Daniel", comentario = "Buena información, muy útil.", hora = "12:00 p. m.")

            Divider(color = Color.LightGray, modifier = Modifier.padding(top = 8.dp))

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = "Usuario", modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Añadir un comentario...", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))
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

@Preview(showBackground = true)
@Composable
fun VistaPreviaDetalleReporte() {
    val navController = rememberNavController()
    ReportDetailScreen(navController = navController)
}
