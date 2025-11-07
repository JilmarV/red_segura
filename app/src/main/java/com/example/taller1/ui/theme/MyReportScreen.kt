package com.example.taller1.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Photo
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material.icons.rounded.Verified
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MyReportScreen() {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Título",
                    color = Color.Black
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.LocationOn,
                        contentDescription = "Icono Ubicación",
                        modifier = Modifier.size(16.dp),
                        tint = Color.Red
                    )
                    Text(
                        text = "Ubicación",
                        color = Color.Red,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Text(
                    text = "Categoría",
                    color = Color.Black
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Verified,
                    contentDescription = "Icono Verificado",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
                Icon(
                    imageVector = Icons.Rounded.MoreVert,
                    contentDescription = "Menú",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Black
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp) // Ajusta el alto para darle más espacio a la imagen
                .background(Color.LightGray)
        ) {
            Icon(
                imageVector = Icons.Rounded.Photo,
                contentDescription = "icono de imagen",
                modifier = Modifier.align(Alignment.Center)
                    .size(100.dp)
            )
        }
        Text(
            text = "Descripción del incidente",
            color = Color.Black
        )
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ){
            Icon(
                imageVector = Icons.Rounded.ThumbUp,
                contentDescription = "Icono Me gusta",
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = "000",
                color = Color.Black
            )
        }
        Divider(
            color = Color.Gray,
            thickness = 1.dp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MyReportScreenPreview() {
    MyReportScreen()
}