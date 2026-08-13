package ec.edu.puce.swipeshare.services

import ec.edu.puce.swipeshare.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // --- USUARIOS (Users Microservice) ---

    @GET("api/users/me")
    suspend fun getMyProfile(): Response<UserProfileResponse>

    @POST("api/users/me")
    suspend fun createProfile(@Body request: UserProfileRequest): Response<UserProfileResponse>

    @PUT("api/users/me")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UserProfileResponse>

    @DELETE("api/users/me")
    suspend fun deleteAccount(): Response<Void>

    @GET("api/users/{cognitoId}")
    suspend fun getUserProfile(@Path("cognitoId") cognitoId: String): Response<ProfileResponse>

    @GET("api/users/{cognitoId}")
    suspend fun getPublicProfile(@Path("cognitoId") cognitoId: String): Response<UserProfileResponse>


    // --- PRODUCTOS / ITEMS ---

    @GET("api/items")
    suspend fun getFeed(): Response<List<ItemResponse>>

    @GET("api/items/me")
    suspend fun getMyItems(): Response<List<ItemResponse>>

    @GET("api/items/{id}")
    suspend fun getItemById(@Path("id") itemId: Long): Response<ItemResponse>

    @POST("api/items")
    suspend fun createItem(@Body request: CreateItemRequest): Response<ItemResponse>

    @PUT("api/items/{id}")
    suspend fun updateItem(
        @Path("id") id: Long,
        @Body request: CreateItemRequest
    ): Response<ItemResponse>

    @DELETE("api/items/{id}")
    suspend fun deleteItem(@Path("id") itemId: Long): Response<Void>


    // --- SWIPES Y MATCHES ---

    @POST("api/swipes")
    suspend fun sendSwipe(@Body request: SwipeRequest): Response<SwipeResponse>

    @GET("api/matches/me")
    suspend fun getMyMatches(): Response<List<MatchResponse>>

    @PATCH("api/matches/{id}/status")
    suspend fun updateMatchStatus(
        @Path("id") matchId: Long,
        @Body request: UpdateMatchStatusRequest
    ): Response<MatchResponse>


    // --- RESEÑAS / REVIEWS ---

    @POST("api/reviews")
    suspend fun createReview(
        @Body request: ReviewRequest
    ): Response<ReviewResponse>

    @GET("api/reviews/target/{targetUserId}")
    suspend fun getReviewsForUser(
        @Path("targetUserId") targetUserId: String
    ): Response<List<ReviewResponse>>


    @GET("api/public/stats")
    suspend fun getGlobalStats(): Response<StatsResponse>
}