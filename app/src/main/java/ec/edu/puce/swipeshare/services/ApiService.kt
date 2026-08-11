package ec.edu.puce.swipeshare.services

import ec.edu.puce.swipeshare.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth & Stats (de la HU-02)
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/public/stats")
    suspend fun getGlobalStats(): Response<StatsResponse>

    // --- Endpoints para HU-04 (Perfil de Usuario) ---

    @GET("api/users/me")
    suspend fun getMyProfile(): Response<UserProfileResponse>

    @PUT("api/users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UserProfileResponse>

    @DELETE("api/users/me")
    suspend fun deleteAccount(): Response<Void>

    @GET("api/users/{id}/public")
    suspend fun getPublicProfile(@Path("id") userId: String): Response<UserProfileResponse>
    // --- Endpoints para HU-05 (Feed y Swiping) ---

    @GET("api/items/feed")
    suspend fun getFeed(): Response<List<ItemResponse>>

    @POST("api/swipes")
    suspend fun sendSwipe(@Body request: SwipeRequest): Response<SwipeResponse>

    @POST("api/items")
    suspend fun createItem(@Body request: CreateItemRequest): Response<ItemResponse>
}
