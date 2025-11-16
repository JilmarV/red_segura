package com.example.taller1.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taller1.data.UserSession
import com.example.taller1.firebase.FirestoreService
import com.example.taller1.model.Report

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReportTabsContent(navController: NavHostController) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Mis reportes", "Crear reporte")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
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

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTabIndex) {
            0 -> MyReportScreen(navController = navController)
            1 -> CreateReportScreen(navController = navController)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyReportScreen(navController: NavHostController) {
    val currentUserId = UserSession.currentUser.id

    var myReports by remember { mutableStateOf<List<Report>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentUserId) {
        FirestoreService.getReports(
            onSuccess = { reportList ->
                myReports = reportList.filter { it.userId == currentUserId }
                isLoading = false
            },
            onFailure = { e ->
                errorMessage = e.message ?: "Error al obtener tus reportes"
                isLoading = false
            }
        )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "Error",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            myReports.isEmpty() -> {
                Text(
                    text = "Aún no has creado reportes.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(myReports) { report ->
                        PostItem(report = report, navController = navController)
                    }
                }
            }
        }
    }
}
