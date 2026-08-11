package ec.edu.puce.swipeshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
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

        setContent {
            MaterialTheme {
                var currentScreen by remember { mutableStateOf("LOGIN") }

                when (currentScreen) {
                    "LOGIN" -> LoginScreen(
                        viewModel = authViewModel,
                        onLoginSuccess = { currentScreen = "PROFILE" }
                    )
                    "PROFILE" -> ProfileScreen(
                        viewModel = profileViewModel,
                        onNavigateToFeed = { currentScreen = "FEED" }
                    )
                    "FEED" -> FeedScreen(
                        viewModel = feedViewModel
                    )
                }
            }
        }
    }
}