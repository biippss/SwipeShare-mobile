package ec.edu.puce.swipeshare.ui.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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

    Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {

        // DIÁLOGO 1: No tiene productos publicados
        if (showNoItemsDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.showNoItemsDialog.value = false },
                title = { Text("¡Producto Requerido!") },
                text = { Text("Para poder enviar un LIKE y proponer un intercambio, primero debes publicar al menos un producto en la sección 'Publicar'.") },
                confirmButton = {
                    Button(onClick = { viewModel.showNoItemsDialog.value = false }) {
                        Text("Entendido")
                    }
                }
            )
        }

        // DIÁLOGO 2: Selección de producto a ofrecer (se activa cuando tiene 2 o más productos)
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

        // DIÁLOGO 3: Notificación de Match
        if (matchEvent) {
            AlertDialog(
                onDismissRequest = { viewModel.matchEvent.value = false },
                title = { Text("¡Es un Match! 🎉") },
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
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                ItemCard(item = currentItem!!)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.swipe("PASS") },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.weight(1f).padding(end = 8.dp)
                    ) {
                        Text("PASS")
                    }

                    Button(
                        onClick = { viewModel.swipe("LIKE") },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.weight(1f).padding(start = 8.dp)
                    ) {
                        Text("LIKE")
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