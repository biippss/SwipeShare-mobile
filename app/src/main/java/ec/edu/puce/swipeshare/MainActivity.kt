package ec.edu.puce.swipeshare

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

        val reviewViewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T = ReviewViewModel(apiService) as T
        })[ReviewViewModel::class.java]

        setContent {
            MaterialTheme {
                var currentScreen by remember { mutableStateOf("LOGIN") }
                val context = LocalContext.current
                val reviewToast by reviewViewModel.toastMessage.observeAsState()

                LaunchedEffect(reviewToast) {
                    reviewToast?.let {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        reviewViewModel.toastMessage.value = null
                    }
                }

                Scaffold(
                    bottomBar = {
                        if (currentScreen != "LOGIN") {
                            NavigationBar {
                                NavigationBarItem(
                                    selected = (currentScreen == "PROFILE"),
                                    onClick = {
                                        profileViewModel.loadMyProfile()
                                        currentScreen = "PROFILE"
                                    },
                                    icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                                    label = { Text("Perfil") }
                                )
                                NavigationBarItem(
                                    selected = (currentScreen == "FEED"),
                                    onClick = { currentScreen = "FEED" },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Feed") },
                                    label = { Text("Feed") }
                                )
                                NavigationBarItem(
                                    selected = (currentScreen == "CREATE_ITEM"),
                                    onClick = { currentScreen = "CREATE_ITEM" },
                                    icon = { Icon(Icons.Default.AddCircle, contentDescription = "Publicar") },
                                    label = { Text("Publicar") }
                                )
                                NavigationBarItem(
                                    selected = (currentScreen == "MY_PRODUCTS"),
                                    onClick = { currentScreen = "MY_PRODUCTS" },
                                    icon = { Icon(Icons.Default.List, contentDescription = "Mis Items") },
                                    label = { Text("Mis Items") }
                                )
                                NavigationBarItem(
                                    selected = (currentScreen == "MATCHES"),
                                    onClick = {
                                        matchesViewModel.loadMyMatches()
                                        currentScreen = "MATCHES"
                                    },
                                    icon = { Icon(Icons.Default.Favorite, contentDescription = "Matches") },
                                    label = { Text("Matches") }
                                )
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
                                    tokenManager.clearToken()
                                    currentScreen = "LOGIN"
                                }
                            )
                            "FEED" -> FeedScreen(
                                viewModel = feedViewModel
                            )
                            "CREATE_ITEM" -> CreateItemScreen(
                                viewModel = createItemViewModel,
                                onSuccess = {
                                    profileViewModel.loadMyProfile()
                                    currentScreen = "MY_PRODUCTS"
                                }
                            )
                            "MY_PRODUCTS" -> MyProductsScreen(
                                viewModel = myProductsViewModel
                            )
                            "MATCHES" -> MatchesScreen(
                                viewModel = matchesViewModel,
                                onSendReview = { targetUserId, rating, comment ->
                                    reviewViewModel.sendReview(targetUserId, rating, comment)
                                    matchesViewModel.markUserAsReviewed(targetUserId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}