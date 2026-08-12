package ec.edu.puce.swipeshare.models

// DTO para los productos del feed
data class ItemResponse(
    val id: Long,
    val title: String,
    val description: String,
    val imageUrl: String?,
    val category: String,
    val ownerId: String
)

// DTO para registrar la interacción (coincide 1:1 con Spring Boot)
data class SwipeRequest(
    val targetItemId: Long,
    val type: String,                 // "LIKE" o "DISLIKE"
    val offeredItemId: Long? = null   // Opcional por si ofrecen un producto a cambio
)

// DTO de respuesta del servidor
data class SwipeResponse(
    val isMatch: Boolean,
    val matchId: Long?,
    val message: String
)

data class CreateItemRequest(
    val title: String,
    val description: String,
    val category: String,
    val imageUrl: String? = null
)

data class MatchResponse(
    val id: Long,
    val user1Id: String,
    val user2Id: String,
    val status: String,
    val offeredItemId: Long? = null,
    val requestedItemId: Long = 0L
)

data class UpdateMatchStatusRequest(
    val status: String // "ACCEPTED" o "REJECTED"
)

// --- AGREGAMOS ESTE DTO QUE FALTABA PARA EL TELÉFONO ---
data class ProfileResponse(
    val id: String,
    val name: String,
    val email: String,
    val phone: String? = null,
    val bio: String? = null,
    val karmaBalance: Int = 0
)