package ec.edu.puce.swipeshare.models

// DTO para la respuesta del perfil (propio o público)
data class UserProfileResponse(
    val id: Long,
    val cognitoId: String = "",
    val name: String,
    val email: String,
    val bio: String?,
    val phone: String?,
    val karmaBalance: Int
)

// DTO para actualizar los datos personales
data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val bio: String?,
    val phone: String?
)

// AGREGA ESTE MODELO PARA REGISTRAR/CREAR EL PERFIL:
data class UserProfileRequest(
    val name: String,
    val email: String,
    val bio: String?,
    val phone: String?
)