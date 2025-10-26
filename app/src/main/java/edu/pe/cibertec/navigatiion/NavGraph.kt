package edu.pe.cibertec.navigatiion

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.pe.cibertec.ui.screens.LoginScreen
import edu.pe.cibertec.ui.screens.MapScreen

sealed class Screen(val route: String){
    object Login : Screen("login")
    object Map: Screen("map")
}

@Composable
fun NavGraph(){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ){
        composable(Screen.Login.route){
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Map.route){
                        popUpTo(Screen.Login.route){
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(Screen.Map.route){
            MapScreen()
        }
    }
}