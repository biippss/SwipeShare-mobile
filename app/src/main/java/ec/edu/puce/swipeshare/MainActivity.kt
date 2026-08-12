package ec.edu.puce.swipeshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ec.edu.puce.swipeshare.services.RetrofitClient
import ec.edu.puce.swipeshare.services.TokenManager
import ec.edu.puce.swipeshare.ui.screens.*
import ec.edu.puce.swipeshare.viewmodels.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tokenManager = TokenManager(this)
        val apiService = RetrofitClient.getApiService(tokenManager)

        val authViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = AuthViewModel(apiService, tokenManager) as T
        })[AuthViewModel::class.java]

        val profileViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = ProfileViewModel(apiService) as T
        })[ProfileViewModel::class.java]

        val feedViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = FeedViewModel(apiService) as T
        })[FeedViewModel::class.java]

        val createItemViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = CreateItemViewModel(apiService) as T
        })[CreateItemViewModel::class.java]

        val myProductsViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = MyProductsViewModel(apiService) as T
        })[MyProductsViewModel::class.java]

        val matchesViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = MatchesViewModel(apiService) as T
        })[MatchesViewModel::class.java]

        setContent {
            MaterialTheme {
                // Controla la pantalla actual en la app
                var currentScreen by remember { mutableStateOf("LOGIN") }

                Scaffold(
                    bottomBar = {
                        // Solo muestra la barra inferior si no está en la pantalla de Login
                        if (currentScreen != "LOGIN") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                TextButton(onClick = { currentScreen = "PROFILE" }) { Text("Perfil") }
                                TextButton(onClick = { currentScreen = "FEED" }) { Text("Feed") }
                                TextButton(onClick = { currentScreen = "CREATE_ITEM" }) { Text("Publicar") }
                                TextButton(onClick = { currentScreen = "MY_PRODUCTS" }) { Text("Mis Items") }
                                TextButton(onClick = { currentScreen = "MATCHES" }) { Text("Matches") }
                            }
                        }
                    }
                ) { innerPadding ->

                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentScreen) {
                            "LOGIN" -> LoginScreen(
                                viewModel = authViewModel,
                                onLoginSuccess = { currentScreen = "PROFILE" }
                            )
                            "PROFILE" -> ProfileScreen(
                                viewModel = profileViewModel,
                                onNavigateToFeed = { currentScreen = "FEED" },
                                onLogout = {
                                    // 1. Borra el token guardado localmente
                                    tokenManager.clearToken()
                                    // 2. Redirige inmediatamente a la pantalla de Login
                                    currentScreen = "LOGIN"
                                }
                            )
                            "FEED" -> FeedScreen(
                                viewModel = feedViewModel
                            )
                            "CREATE_ITEM" -> CreateItemScreen(
                                viewModel = createItemViewModel,
                                onSuccess = { currentScreen = "MY_PRODUCTS" }
                            )
                            "MY_PRODUCTS" -> MyProductsScreen(
                                viewModel = myProductsViewModel
                            )
                            "MATCHES" -> MatchesScreen(
                                viewModel = matchesViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}