package ec.edu.puce.swipeshare.models

// DTO para los productos del feed
data class ItemResponse(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val category: String,
    val ownerId: String
)

// DTO para registrar la interacción (LIKE / PASS)
data class SwipeRequest(
    val targetItemId: String,
    val action: String // "LIKE" o "PASS"
)

// DTO de respuesta para saber si hubo Match (HU-07)
data class SwipeResponse(
    val isMatch: Boolean,
    val matchId: String?
)