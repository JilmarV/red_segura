package com.example.taller1.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CancelReasonScreen(navController: NavController) {
    var title by remember { mutableStateOf("Reporte") }
    var categoryExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Seleccionar") }
    val categoryOptions = listOf("Robo", "Accidente", "Otro")

    var reason by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color.White,
                elevation = 0.dp,
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.Red)
                    }
                },
                title = {}
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
                text = "Reportes",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2B2F33)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Título del reporte
            Text("Título del reporte", color = Color.Red, fontSize = 14.sp)
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Categoría
            Text("Categoría", color = Color.Red, fontSize = 14.sp)
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                    },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categoryOptions.forEach { option ->
                        DropdownMenuItem(onClick = {
                            selectedCategory = option
                            categoryExpanded = false
                        }) {
                            Text(text = option)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Motivo del rechazo", color = Color.Red, fontSize = 14.sp)
            OutlinedTextField(
                value = reason,
                onValueChange = {
                    reason = it
                    error = false
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = { Text("Descripción") },
                maxLines = 6,
                isError = error,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            if (error) {
                Text("Este campo es obligatorio", color = Color.Red, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            androidx.compose.material3.Button(
                onClick = {
                    if (reason.isBlank()) {
                        error = true
                    } else {
                        navController.popBackStack()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3C3C)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                androidx.compose.material3.Text("Enviar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CancelReasonScreenPreview() {
    val navController = rememberNavController()
    CancelReasonScreen(navController = navController)
}
