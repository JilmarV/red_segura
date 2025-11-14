package com.example.taller1.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Comment
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.taller1.data.UserSession
import com.example.taller1.firebase.FirestoreService
import com.example.taller1.model.ReportNotification

@Composable
fun NotificationScreen(navController: NavController) {
    val userId = UserSession.currentUser?.id ?: ""

    var notifications by remember { mutableStateOf<List<ReportNotification>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (userId.isNotEmpty()) {
            FirestoreService.getUserNotifications(
                userId = userId,
                onSuccess = {
                    notifications = it
                    loading = false
                },
                onFailure = { e ->
                    loading = false
                    errorMessage = e.message ?: "Error al cargar notificaciones"
                }
            )
        } else {
            loading = false
            errorMessage = "No se encontró el usuario"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Notificaciones",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "Error",
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }

            notifications.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No tienes notificaciones")
                }
            }

            else -> {
                LazyColumn {
                    items(notifications) { notif ->
                        NotificationItem(notif)
                        Divider(
                            color = Color.LightGray,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: ReportNotification) {
    Column(modifier = Modifier.fillMaxWidth()) {

        Text(
            text = notification.message,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.LocationOn,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Reporte: ${notification.reportId}", color = Color.Red)
        }

        if (notification.motivo != null) {
            Text(
                text = "Motivo: ${notification.motivo}",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        Text(
            text = notification.state,
            color = Color.Gray,
            fontSize = 12.sp
        )
    }
}
