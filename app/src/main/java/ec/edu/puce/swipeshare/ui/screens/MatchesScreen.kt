package ec.edu.puce.swipeshare.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ec.edu.puce.swipeshare.viewmodels.MatchUIItem
import ec.edu.puce.swipeshare.viewmodels.MatchesViewModel

@Composable
fun MatchesScreen(
    viewModel: MatchesViewModel,
    onSendReview: (targetUserId: String, rating: Int, comment: String?) -> Unit
) {
    val matchesList by viewModel.matchesList.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    val errorMessage by viewModel.errorMessage.observeAsState()

    var selectedMatchForReview by remember { mutableStateOf<MatchUIItem?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadMyMatches()
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // DIÁLOGO DE RESEÑA / CALIFICACIÓN
        selectedMatchForReview?.let { match ->
            ReviewDialog(
                counterpartName = match.counterpartName,
                onDismiss = { selectedMatchForReview = null },
                onSubmit = { rating, comment ->
                    onSendReview(match.counterpartId, rating, comment)
                    selectedMatchForReview = null
                }
            )
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (matchesList.isEmpty()) {
            Text(
                text = "Aún no tienes matches pendientes o aceptados.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Mis Matches",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(matchesList) { match ->
                        MatchCardItem(
                            match = match,
                            onAccept = { viewModel.updateMatchStatus(match.matchId, "APPROVED") },
                            onReject = { viewModel.updateMatchStatus(match.matchId, "REJECTED") },
                            onReviewClick = { selectedMatchForReview = match }
                        )
                    }
                }
            }
        }

        errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun MatchCardItem(
    match: MatchUIItem,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onReviewClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = if (match.status == "APPROVED") "MATCH ACEPTADO" else "SOLICITUD PENDIENTE",
                style = MaterialTheme.typography.labelMedium,
                color = if (match.status == "APPROVED") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Te ofrecen: ${match.offeredItemTitle}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "A cambio de tu: ${match.requestedItemTitle}",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (match.status == "APPROVED") {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                Text(
                    text = "Contacto: ${match.counterpartName}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Teléfono / WhatsApp: ${match.counterpartPhone ?: "Sin teléfono registrado"}",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Muestra un solo botón según el estado de la reseña
                if (match.hasBeenReviewed) {
                    OutlinedButton(
                        onClick = { },
                        enabled = false,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Reseña Enviada")
                    }
                } else {
                    Button(
                        onClick = onReviewClick,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Calificar Usuario")
                    }
                }
            } else {
                if (match.isIncoming) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = onReject,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Rechazar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = onAccept) {
                            Text("Aceptar Match")
                        }
                    }
                } else {
                    Text(
                        text = "Esperando respuesta de ${match.counterpartName}...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ReviewDialog(
    counterpartName: String,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, comment: String?) -> Unit
) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Calificar a $counterpartName") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("¿Cómo fue tu experiencia de intercambio?")

                Row(horizontalArrangement = Arrangement.Center) {
                    (1..5).forEach { star ->
                        Icon(
                            imageVector = if (star <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Estrella $star",
                            tint = if (star <= rating) Color(0xFFFFC107) else Color.Gray,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { rating = star }
                        )
                    }
                }

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Comentario (Opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(rating, comment.ifBlank { null }) }) {
                Text("Enviar Reseña")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}