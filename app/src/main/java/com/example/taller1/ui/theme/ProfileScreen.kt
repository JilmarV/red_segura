package com.example.taller1.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun ProfileScreen(navController: NavController) {
    val name = "USUARIO"
    val ciudad = "Armenia"
    val direccion = "Terranova"
    val email = "juliana@gmail.com"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Mi Perfil",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = Color(0xFF2B2F33),
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Icon(
            imageVector = Icons.Rounded.AccountCircle,
            contentDescription = "Icono de usuario",
            tint = Color.DarkGray,
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2B2F33)
        )

        Spacer(modifier = Modifier.height(24.dp))

        UserProfileField(label = "Ciudad de Residencia", value = ciudad)
        UserProfileField(label = "Dirección", value = direccion)
        UserProfileField(label = "Correo electrónico", value = email)

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "En la app encontrarás un botón de ayuda el cual alertará a las autoridades, para cuando te sientas inseguro o necesites una comunicación rápida con ellos.",
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            androidx.compose.material3.Button(
                onClick = { navController.navigate("dataModification") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                modifier = Modifier.size(width = 120.dp, height = 50.dp)
            ) {
                androidx.compose.material3.Text("Editar", color = Color.White)
            }

            androidx.compose.material3.Button(
                onClick = { /* Acción de eliminar */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier.size(width = 120.dp, height = 50.dp)
            ) {
                androidx.compose.material3.Text("Eliminar", color = Color.White)
            }
        }
    }
}

@Composable
fun UserProfileField(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = label, color = Color.Red, fontSize = 14.sp)
        Text(text = value, color = Color.Black, fontSize = 16.sp)
        Divider(color = Color.LightGray, thickness = 1.dp)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()
    ProfileScreen(navController = navController)
}
