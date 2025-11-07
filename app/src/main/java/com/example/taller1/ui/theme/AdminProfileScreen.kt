package com.example.taller1.ui.theme



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdminProfileScreen() {
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
            imageVector = Icons.Default.Person,
            contentDescription = "Avatar",
            tint = Color.DarkGray,
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "ADMIN",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color(0xFF2B2F33)
        )

        Spacer(modifier = Modifier.height(24.dp))

        ProfileField(label = "Ciudad de Residencia", value = "Armenia")
        ProfileField(label = "Dirección", value = "Terranova")
        ProfileField(label = "Email", value = "juliana@gmail.com")

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "En la app encontraras un botón de ayuda el cual alertará a las autoridades, para cuando te sientas inseguro o necesites una comunicación rápida con ellos.",
            fontSize = 14.sp,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { /* TODO: Editar */ },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF7043))
            ) {
                Text("Editar", color = Color.White)
            }

            Button(
                onClick = { /* TODO: Eliminar */ },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
            ) {
                Text("Eliminar", color = Color.White)
            }
        }
    }
}

@Composable
fun ProfileField(label: String, value: String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {
        Text(text = label, color = Color.Red, fontSize = 14.sp)
        Text(text = value, color = Color.Black, fontSize = 16.sp)
        Divider(color = Color.LightGray, thickness = 1.dp)
    }
}

@Preview(showBackground = true)
@Composable
fun AdminProfileScreenPreview() {
    AdminProfileScreen()
}
