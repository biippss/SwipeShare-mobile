package ec.edu.puce.swipeshare.models

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
    val totalUsers: Long? = 0,
    val activeItems: Long? = 0,
    val totalMatches: Long? = 0
)