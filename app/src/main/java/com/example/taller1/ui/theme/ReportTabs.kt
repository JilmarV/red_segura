package com.example.taller1.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taller1.ui.theme.CreateReportScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReportTabsContent(navController: NavHostController) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Mis reportes", "Crear reporte")

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            backgroundColor = Color.White,
            contentColor = Color.Black
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> MyReportScreen()
            1 -> CreateReportScreen(navController)
        }
    }
}
