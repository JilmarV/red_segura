package com.example.taller1.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Comment
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class Notification(
    val title: String,
    val location: String,
    val category: String,
    val description: String,
    val isComment: Boolean = false
)

@Composable
fun NotificationScreen(navController: NavController) {
    val notifications = listOf(
        Notification("Título", "Ubicación", "categoría", "Lorem ipsum dolor sit amet, consectetur adipiscing elit..."),
        Notification("Título", "Ubicación", "categoría", "Lorem ipsum dolor sit amet, consectetur adipiscing elit..."),
        Notification("Título", "Ubicación", "categoría", "Lorem ipsum dolor sit amet, consectetur adipiscing elit..."),
        Notification("Título", "Ubicación", "categoría", "Lorem ipsum dolor sit amet, consectetur adipiscing elit..."),
        Notification("Comentario", "Título reporte", "BUWN in", "", isComment = true)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Notificaciones",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Lista de notificaciones
        LazyColumn {
            items(notifications) { notification ->
                NotificationItem(notification)
                Divider(
                    color = Color.LightGray,
                    thickness = 1.dp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (!notification.isComment) {
            Text(
                text = notification.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.LocationOn,
                    contentDescription = "Ubicación",
                    tint = Color.Red,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = notification.location, color = Color.Red)
            }

            Text(text = notification.category, color = Color.Gray)
            Text(text = notification.description, color = Color.Black)
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.Comment,
                    contentDescription = "Comentario",
                    tint = Color.Red,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Comentario", color = Color.Red)
            }
            Text(text = notification.title, fontWeight = FontWeight.Bold)
            Text(text = notification.category)
        }
    }
}
