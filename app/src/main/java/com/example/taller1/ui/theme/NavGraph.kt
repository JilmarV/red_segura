package com.example.taller1.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taller1.screens.NotificationScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    val sideMenu = SideMenu()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("dataModification") { DataModificationScreen(navController) }
        composable("forgotPassword") { ForgotPassword(navController) }


        composable("home") {
            SideMenuScreen(navController = navController, title = "Inicio") {
                HomeScreen(navController)
            }
        }

        composable("profile") {
            SideMenuScreen(navController = navController, title = "Perfil") {
                ProfileScreen(navController)
            }
        }

        composable("reports") {
            SideMenuScreen(navController = navController, title = "Reportes") {
                ReportTabsContent(navController)
            }
        }

        composable("event") {
            SideMenuScreen(navController = navController, title = "Eventos") {
                EventScreen(navController)
            }
        }

        composable("notification") {
            SideMenuScreen(navController = navController, title = "Notificaciones") {
                NotificationScreen(navController)
            }
        }
        composable("detalle_reporte") {
            SideMenuScreen(navController = navController, title = "Detalle del Reporte") {
                ReportDetailScreen(navController)
            }
        }
        composable("admin_profile") {
            SideMenuScreen(navController = navController, title = "Perfil del Administrador") {
                AdminProfileScreen()
            }
        }
        composable("homeadmin") {
            HomeAdminScreen(navController)
        }
        composable("cancel_reason") {
            CancelReasonScreen(navController = navController)
        }
        composable("login") {
            LoginScreen(navController = navController)
        }
    }
}