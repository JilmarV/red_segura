package com.example.red_segura.ui.theme

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextRange

@Composable
fun LoginScreen() {
    Column {
        Text("RED SEGURA")
        Text("Iniciar sesión")
        Text("Que gusto verte de nuevo")
        text_field_email()
    }
}

@Composable
fun text_field_email() {
    TextField(
        value = "",
        onValueChange = {},
        label = { Text("Correo electrónico") }
    )
}