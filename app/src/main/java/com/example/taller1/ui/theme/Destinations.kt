package com.example.taller1.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destinations(
    val icon: ImageVector,
    val title: String,
    val route: String,
    val isLogout: Boolean = false
){
    object Screen1 : Destinations(
        icon = Icons.Rounded.Home,
        title = "Inicio",
        route = "home"
    )
    object Screen2 : Destinations(
        icon = Icons.Rounded.AccountCircle,
        title = "Perfil",
        route = "profile"
    )
    object Screen3 : Destinations(
        icon = Icons.Rounded.Info,
        title = "Reportes",
        route = "reports"
    )
    object Screen4 : Destinations(
        icon = Icons.Rounded.DateRange,
        title = "Eventos",
        route = "event"
    )
    object Screen5 : Destinations(
        icon = Icons.Rounded.Notifications,
        title = "Notificación",
        route = "notification"
    )
    object Screen6 : Destinations(
        icon = Icons.Rounded.Logout,
        title = "Cerrar Sesión",
        route = "login",
        isLogout = true
    )
    object Screen7 : Destinations(
        icon = Icons.Rounded.Home,
        title = "Inicio",
        route = "homeadmin"
    )


}