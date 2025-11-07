package com.example.taller1.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.taller1.data.UserSession
import com.example.taller1.ui.theme.Destinations.*

class SideMenuAdmin {

    // Puedes personalizar los destinos para administradores
    private val adminNavigationItems = listOf(
        Screen7, // por ejemplo: Panel de reportes
        Screen2,
        Screen5,
        Screen4,
        Screen6
    )

    @Composable
    fun Drawer(
        navController: NavController,
        scope: CoroutineScope,
        scaffoldState: ScaffoldState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = "Icono de usuario",
                tint = Color.DarkGray,
                modifier = Modifier.size(150.dp)
            )
            Text("Admin Usuario", color = Color.DarkGray, modifier = Modifier.padding(4.dp))
            Text("admin@correo.com", color = Color.DarkGray, modifier = Modifier.padding(4.dp))
        }

        Divider(color = Color.LightGray)

        Column {
            adminNavigationItems.forEach { item ->
                DrawerItem(item = item, onClick = {
                    if (item.route == "login") {
                        UserSession.logout() // Limpiar usuario
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true } // Borra backstack
                        }
                    } else {
                        navController.navigate(item.route)
                    }
                    scope.launch { scaffoldState.drawerState.close() }
                })
            }

        }
    }

    @Composable
    fun DrawerItem(item: Destinations, onClick: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = Color.DarkGray,
                modifier = Modifier.size(30.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = item.title, color = Color.Black)
        }
    }

    @Composable
    fun TopBar(scope: CoroutineScope, scaffoldState: ScaffoldState, title: String) {
        TopAppBar(
            title = { Text(title) },
            navigationIcon = {
                IconButton(onClick = {
                    scope.launch { scaffoldState.drawerState.open() }
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Menu,
                        contentDescription = "Menú",
                        tint = Color.Red,
                        modifier = Modifier.size(30.dp)
                    )
                }
            },
            backgroundColor = Color.White,
            contentColor = Color.Black,
            elevation = 4.dp
        )
    }
}
