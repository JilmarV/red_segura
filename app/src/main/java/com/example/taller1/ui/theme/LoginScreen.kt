package com.example.taller1.ui.theme

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.taller1.R
import com.example.taller1.data.UserSession
import com.example.taller1.firebase.FirestoreService
import com.example.taller1.model.Role

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 38.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_whitout_background),
            contentDescription = "Logo",
            modifier = Modifier.size(240.dp)
        )

        Text("RED SEGURA", fontSize = 24.sp, color = Color.Red)
        Spacer(modifier = Modifier.height(16.dp))
        Text(stringResource(R.string.login_title), fontSize = 20.sp, color = Color.Black)
        Text(stringResource(R.string.welcome_back), fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            singleLine = true,
            onValueChange = { email = it },
            placeholder = { Text("Ingrese su Correo Electrónico") },
            label = { Text("") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Ingrese su contraseña") },
            label = { Text("") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                val desc = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = icon, contentDescription = desc)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Debe llenar todos los campos", Toast.LENGTH_LONG).show()
                } else {
                    FirestoreService.login(email, password,
                        onSuccess = { foundUser ->
                            UserSession.currentUser = foundUser

                            when (UserSession.currentUser.role) {
                                Role.ADMIN -> navController.navigate("homeadmin")
                                Role.CLIENT -> navController.navigate("home")
                                else -> Toast.makeText(context, "Rol no reconocido", Toast.LENGTH_LONG).show()
                            }
                        },
                        onFailure = {
                            Toast.makeText(context, "Correo o contraseña incorrectos", Toast.LENGTH_LONG).show()
                        }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text(stringResource(R.string.login_button), color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        val annotatedText = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.Black)) {
                append(stringResource(R.string.forgot_password) + " ")
            }
            pushStringAnnotation(tag = "cambiar", annotation = "cambiar")
            withStyle(style = SpanStyle(color = Color.Red, fontWeight = FontWeight.Bold)) {
                append(stringResource(R.string.change))
            }
            pop()
        }

        ClickableText(
            text = annotatedText,
            onClick = { offset ->
                annotatedText.getStringAnnotations("cambiar", offset, offset).firstOrNull()?.let {
                    navController.navigate("forgotPassword")
                }
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = stringResource(R.string.no_account), color = Color.Gray)
        Text(
            text = stringResource(R.string.register),
            color = Color.Red,
            modifier = Modifier.clickable {
                navController.navigate("register")
            }
        )
    }
}
