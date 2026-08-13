package ec.edu.puce.swipeshare.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ec.edu.puce.swipeshare.ui.components.ItemCard
import ec.edu.puce.swipeshare.viewmodels.FeedViewModel

@Composable
fun FeedScreen(viewModel: FeedViewModel) {
    val currentItem by viewModel.currentItem.observeAsState()
    val myItemsList by viewModel.myItemsList.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    val matchEvent by viewModel.matchEvent.observeAsState(false)
    val showNoItemsDialog by viewModel.showNoItemsDialog.observeAsState(false)
    val showOfferSelectionDialog by viewModel.showOfferSelectionDialog.observeAsState(false)
    val toastMessage by viewModel.toastMessage.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.toastMessage.value = null
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadFeed()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // DIÁLOGO 1: No tiene productos publicados
        if (showNoItemsDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.showNoItemsDialog.value = false },
                title = { Text("Producto Requerido") },
                text = { Text("Para poder enviar un LIKE y proponer un intercambio, primero debes publicar al menos un producto en la sección 'Publicar'.") },
                confirmButton = {
                    Button(onClick = { viewModel.showNoItemsDialog.value = false }) {
                        Text("Entendido")
                    }
                }
            )
        }

        // DIÁLOGO 2: Selección de producto a ofrecer
        if (showOfferSelectionDialog) {
            var selectedItem by remember { mutableStateOf(myItemsList.firstOrNull()) }

            AlertDialog(
                onDismissRequest = { viewModel.showOfferSelectionDialog.value = false },
                title = { Text("¿Qué producto deseas ofrecer?") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Selecciona cuál de tus artículos quieres ofrecer a cambio:", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                        myItemsList.forEach { item ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedItem = item }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = (selectedItem?.id == item.id),
                                    onClick = { selectedItem = item }
                                )
                                Text(
                                    text = item.title,
                                    modifier = Modifier.padding(start = 8.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            selectedItem?.let { viewModel.confirmOfferSelection(it.id) }
                        },
                        enabled = (selectedItem != null)
                    ) {
                        Text("Ofrecer este ítem")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.showOfferSelectionDialog.value = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }

        // DIÁLOGO 3: Notificación de Match (Sin Emojis)
        if (matchEvent) {
            AlertDialog(
                onDismissRequest = { viewModel.matchEvent.value = false },
                title = { Text("¡Es un Match!") },
                text = { Text("¡Genial! Alguien también quiere intercambiar contigo. Revisa la pestaña de Matches.") },
                confirmButton = {
                    Button(onClick = { viewModel.matchEvent.value = false }) {
                        Text("Aceptar")
                    }
                }
            )
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (currentItem != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Contenedor centrado para la tarjeta del producto
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    ItemCard(item = currentItem!!)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botones redondos de acción
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón PASS redondo
                    Button(
                        onClick = { viewModel.swipe("PASS") },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                        modifier = Modifier.size(64.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Pass",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Botón LIKE redondo
                    Button(
                        onClick = { viewModel.swipe("LIKE") },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                        modifier = Modifier.size(64.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Like",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        } else {
            Text(
                text = "¡No hay más artículos disponibles por hoy!",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}