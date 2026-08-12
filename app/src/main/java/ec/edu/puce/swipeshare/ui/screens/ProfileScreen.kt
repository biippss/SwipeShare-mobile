package ec.edu.puce.swipeshare.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ec.edu.puce.swipeshare.viewmodels.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onNavigateToFeed: () -> Unit,
    onLogout: () -> Unit
) {
    val profile by viewModel.userProfile.observeAsState()
    val isLoading by viewModel.isLoading.observeAsState(false)

    var name by remember(profile) { mutableStateOf(profile?.name ?: "") }
    var phone by remember(profile) { mutableStateOf(profile?.phone ?: "") }
    var bio by remember(profile) { mutableStateOf(profile?.bio ?: "") }

    LaunchedEffect(Unit) {
        viewModel.loadMyProfile()
    }

    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Mi Perfil", style = MaterialTheme.typography.headlineMedium)

                Text("Karma: ${profile?.karmaBalance ?: 0}", color = MaterialTheme.colorScheme.primary)

                OutlinedTextField(
                    value = profile?.email ?: "",
                    onValueChange = {},
                    enabled = false,
                    label = { Text("Correo (no editable)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Biografía") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val currentEmail = profile?.email ?: ""
                        viewModel.updateProfile(name = name, email = currentEmail, bio = bio, phone = phone)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar Cambios")
                }

                OutlinedButton(
                    onClick = onNavigateToFeed,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ir al Feed de Productos")
                }

                // BOTÓN CERRAR SESIÓN
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