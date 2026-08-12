package ec.edu.puce.swipeshare.services

import ec.edu.puce.swipeshare.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- Endpoints para Usuarios (Microservicio Users) ---

    // 1. Obtener mi perfil
    @GET("api/users/me")
    suspend fun getMyProfile(): Response<UserProfileResponse>

    // 2. Crear / Registrar mi perfil por primera vez (Usado en el Login con Cognito)
    @POST("api/users/me")
    suspend fun createProfile(@Body request: UserProfileRequest): Response<UserProfileResponse>

    // 3. Actualizar mi perfil
    @PUT("api/users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UserProfileResponse>

    // 4. Eliminar mi perfil
    @DELETE("api/users/me")
    suspend fun deleteAccount(): Response<Void>

    // 5. Consultar perfil público de otro usuario por su Cognito ID
    @GET("api/users/{cognitoId}")
    suspend fun getPublicProfile(@Path("cognitoId") cognitoId: String): Response<UserProfileResponse>


    // --- Endpoints Públicos y Estadísticas ---

    @GET("api/public/stats")
    suspend fun getGlobalStats(): Response<StatsResponse>


    // --- Endpoints para SwipeShare (Productos y Swipes) ---

    // 1. Obtener el Feed principal (todos los ítems menos los míos)
    @GET("api/items")
    suspend fun getFeed(): Response<List<ItemResponse>>

    // 2. Mis ítems publicados
    @GET("api/items/me")
    suspend fun getMyItems(): Response<List<ItemResponse>>

    // 3. Crear un nuevo producto
    @POST("api/items")
    suspend fun createItem(@Body request: CreateItemRequest): Response<ItemResponse>

    // 4. Swipes
    @POST("api/swipes")
    suspend fun sendSwipe(@Body request: SwipeRequest): Response<SwipeResponse>

    // Eliminar un producto propio por ID
    @DELETE("api/items/{id}")
    suspend fun deleteItem(@Path("id") itemId: Long): Response<Void>

    @GET("api/matches/me")
    suspend fun getMyMatches(): Response<List<MatchResponse>>
    @PATCH("api/matches/{id}/status")
    suspend fun updateMatchStatus(
        @Path("id") matchId: Long,
        @Body request: UpdateMatchStatusRequest
    ): Response<MatchResponse>
    // Asegúrate de que el modelo que devuelve tenga la variable 'val phone: String?'
    @GET("api/users/{cognitoId}")
    suspend fun getUserProfile(@Path("cognitoId") cognitoId: String): Response<ProfileResponse>

    @GET("api/items/{id}")
    suspend fun getItemById(@Path("id") itemId: Long): Response<ItemResponse>

    @PUT("api/items/{id}")
    suspend fun updateItem(
        @Path("id") id: Long,
        @Body request: CreateItemRequest
    ): Response<ItemResponse>

}