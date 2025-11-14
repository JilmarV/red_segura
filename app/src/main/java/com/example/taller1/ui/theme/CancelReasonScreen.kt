package com.example.taller1.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.taller1.firebase.FirestoreService
import com.example.taller1.model.ReportState

@Composable
fun CancelReasonScreen(
    navController: NavHostController,
    reportId: String,
    userId: String
) {
    var reason by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color.White,
                elevation = 0.dp,
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = Color.Red)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Motivo del rechazo",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2B2F33)
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = reason,
                onValueChange = {
                    reason = it
                    error = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                placeholder = { Text("Escribe el motivo") },
                maxLines = 6,
                isError = error
            )

            if (error) {
                Text("Este campo es obligatorio", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (reason.isBlank()) {
                        error = true
                    } else if (!isSaving) {
                        isSaving = true
                        FirestoreService.rejectReport(
                            reportId = reportId,
                            motivo = reason,
                            onSuccess = {
                                FirestoreService.createReportResultNotification(
                                    userId = userId,
                                    reportId = reportId,
                                    newState = ReportState.REJECTED,
                                    motivo = reason,
                                    onSuccess = {},
                                    onFailure = {}
                                )
                                navController.popBackStack()
                            },
                            onFailure = {
                                isSaving = false
                            }
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFFF3C3C))
            ) {
                Text("Enviar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
