package ec.edu.puce.swipeshare.models

// DTO para la respuesta del perfil (propio o público)
data class UserProfileResponse(
    val id: String,
    val name: String,
    val email: String,
    val bio: String?,
    val phone: String?,
    val karma: Int
)

// DTO para actualizar los datos personales
data class UpdateProfileRequest(
    val name: String,
    val bio: String?,
    val phone: String?
)