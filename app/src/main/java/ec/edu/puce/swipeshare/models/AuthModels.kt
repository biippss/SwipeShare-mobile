package ec.edu.puce.swipeshare.models

import com.google.gson.annotations.SerializedName

// DTO para la solicitud de inicio de sesión
data class LoginRequest(
    val email: String,
    val password: String
)

// DTO para la respuesta con el token JWT de Cognito
data class AuthResponse(
    val token: String
)

// DTO para las estadísticas globales (totalItems, totalMatches)
data class StatsResponse(
    @SerializedName("totalItems")
    val totalItems: Long? = 0,

    @SerializedName("totalMatches")
    val totalMatches: Long? = 0
)