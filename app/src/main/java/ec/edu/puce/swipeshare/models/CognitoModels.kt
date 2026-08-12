package ec.edu.puce.swipeshare.models

import com.google.gson.annotations.SerializedName

data class CognitoAuthRequest(
    @SerializedName("AuthFlow") val authFlow: String = "USER_PASSWORD_AUTH",
    @SerializedName("ClientId") val clientId: String,
    @SerializedName("AuthParameters") val authParameters: Map<String, String>
)

data class CognitoAuthResponse(
    @SerializedName("AuthenticationResult") val authenticationResult: AuthenticationResult?
)

data class AuthenticationResult(
    @SerializedName("IdToken") val idToken: String?,
    @SerializedName("AccessToken") val accessToken: String?,
    @SerializedName("RefreshToken") val refreshToken: String?
)