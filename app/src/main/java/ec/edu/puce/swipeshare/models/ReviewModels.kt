package ec.edu.puce.swipeshare.models

// DTO enviado al backend para calificar a un usuario
data class ReviewRequest(
    val targetUserId: String,
    val rating: Int,
    val comment: String? = null
)

// DTO recibido del backend tras guardar la reseña
data class ReviewResponse(
    val id: Long,
    val reviewerId: String,
    val targetUserId: String,
    val rating: Int,
    val comment: String?
)