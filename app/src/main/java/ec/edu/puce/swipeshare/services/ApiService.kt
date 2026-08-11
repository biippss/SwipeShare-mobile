package ec.edu.puce.swipeshare.services

import ec.edu.puce.swipeshare.models.AuthResponse
import ec.edu.puce.swipeshare.models.LoginRequest
import ec.edu.puce.swipeshare.models.StatsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    // Endpoint para iniciar sesión contra Cognito
    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    // Endpoint público para obtener estadísticas del sistema (HU-14)
    @GET("api/public/stats")
    suspend fun getGlobalStats(): Response<StatsResponse>
}