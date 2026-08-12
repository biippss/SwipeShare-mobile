package ec.edu.puce.swipeshare.services

import ec.edu.puce.swipeshare.models.CognitoAuthRequest
import ec.edu.puce.swipeshare.models.CognitoAuthResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface CognitoApiService {

    @Headers(
        "Content-Type: application/x-amz-json-1.1",
        "X-Amz-Target: AWSCognitoIdentityProviderService.InitiateAuth"
    )
    @POST("/")
    suspend fun initiateAuth(@Body request: CognitoAuthRequest): Response<CognitoAuthResponse>

    companion object {
        // Asegúrate de usar tu región de AWS (us-east-1)
        private const val COGNITO_BASE_URL = "https://cognito-idp.us-east-1.amazonaws.com/"

        fun create(): CognitoApiService {
            return Retrofit.Builder()
                .baseUrl(COGNITO_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CognitoApiService::class.java)
        }
    }
}