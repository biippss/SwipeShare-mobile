package ec.edu.puce.swipeshare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ec.edu.puce.swipeshare.models.ReviewResponse
import ec.edu.puce.swipeshare.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToFeed: () -> Unit,
    onLogout: () -> Unit
) {
    val profile by viewModel.userProfile.observeAsState()
    val activeItems by viewModel.activeItemsCount.observeAsState(0)
    val completedMatches by viewModel.completedMatchesCount.observeAsState(0)
    val myReviews by viewModel.myReviewsList.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    var name by remember(profile) { mutableStateOf(profile?.name ?: "") }
    var phone by remember(profile) { mutableStateOf(profile?.phone ?: "") }
    var bio by remember(profile) { mutableStateOf(profile?.bio ?: "") }

    LaunchedEffect(Unit) {
        viewModel.loadMyProfile()
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("Mi Perfil", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    // SECCIÓN DE ESTADÍSTICAS (Karma, Ítems, Matches)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatCard("Karma", "${profile?.karmaBalance ?: 0}", Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        StatCard("Ítems", "$activeItems", Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        StatCard("Matches", "$completedMatches", Modifier.weight(1f))
                    }
                }

                item {
                    OutlinedTextField(
                        value = profile?.email ?: "",
                        onValueChange = {},
                        enabled = false,
                        label = { Text("Correo (no editable)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Biografía") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Button(
                        onClick = {
                            viewModel.updateProfile(name = name, email = profile?.email ?: "", bio = bio, phone = phone)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar Cambios")
                    }
                }

                // SECCIÓN RESEÑAS RECIBIDAS
                item {
                    Text(
                        text = "Reseñas Recibidas",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                    )
                }

                if (myReviews.isEmpty()) {
                    item {
                        Text(
                            text = "Aún no has recibido reseñas de otros usuarios.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                } else {
                    items(myReviews) { review ->
                        ReviewCardItem(review)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(
                        onClick = onNavigateToFeed,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Ir al Feed de Productos")
                    }

                    Button(
                        onClick = onLogout,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    ) {
                        Text("Cerrar Sesión")
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MaterialTheme.typography.titleLarge)
            Text(text = label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun ReviewCardItem(review: ReviewResponse) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                (1..5).forEach { star ->
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (star <= review.rating) Color(0xFFFFC107) else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            if (!review.comment.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = review.comment, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}