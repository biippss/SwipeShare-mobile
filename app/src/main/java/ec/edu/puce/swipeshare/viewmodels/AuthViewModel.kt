package ec.edu.puce.swipeshare.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.puce.swipeshare.models.CognitoAuthRequest
import ec.edu.puce.swipeshare.models.StatsResponse
import ec.edu.puce.swipeshare.models.UserProfileRequest
import ec.edu.puce.swipeshare.services.ApiService
import ec.edu.puce.swipeshare.services.CognitoApiService
import ec.edu.puce.swipeshare.services.TokenManager
import kotlinx.coroutines.launch

class AuthViewModel(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val cognitoClientId = "74ekqsr53ur4qelrvfvqum96vh"
    private val cognitoApiService = CognitoApiService.create()

    val loginResult = MutableLiveData<String?>() // null = éxito, string = mensaje de error
    val isLoading = MutableLiveData<Boolean>()
    val globalStats = MutableLiveData<StatsResponse?>() // Estado para las estadísticas globales

    // Cargar estadísticas públicas de la plataforma
    fun loadGlobalStats() {
        viewModelScope.launch {
            try {
                val response = apiService.getGlobalStats()
                if (response.isSuccessful) {
                    globalStats.value = response.body()
                }
            } catch (_: Exception) {}
        }
    }

    fun login(email: String, pass: String) {
        val cleanEmail = email.trim().lowercase()

        if (cleanEmail.isBlank() || (!cleanEmail.endsWith("@puce.edu.ec") && cleanEmail != "domenica@test.com")) {
            loginResult.value = "Ingresa un correo institucional válido (@puce.edu.ec)"
            return
        }

        isLoading.value = true

        viewModelScope.launch {
            try {
                val request = CognitoAuthRequest(
                    clientId = cognitoClientId,
                    authParameters = mapOf(
                        "USERNAME" to cleanEmail,
                        "PASSWORD" to pass
                    )
                )

                val response = cognitoApiService.initiateAuth(request)

                if (response.isSuccessful && response.body()?.authenticationResult?.idToken != null) {
                    val idToken = response.body()!!.authenticationResult!!.idToken!!
                    tokenManager.saveToken(idToken)

                    try {
                        val profileResponse = apiService.getMyProfile()
                        if (!profileResponse.isSuccessful) {
                            apiService.createProfile(
                                UserProfileRequest(
                                    name = cleanEmail.substringBefore("@"),
                                    email = cleanEmail,
                                    bio = "Estudiante PUCE",
                                    phone = "+593900000000"
                                )
                            )
                        }
                    } catch (e: Exception) {
                        try {
                            apiService.createProfile(
                                UserProfileRequest(
                                    name = cleanEmail.substringBefore("@"),
                                    email = cleanEmail,
                                    bio = "Estudiante PUCE",
                                    phone = "+593900000000"
                                )
                            )
                        } catch (_: Exception) {}
                    }

                    loginResult.value = null
                } else {
                    loginResult.value = "Error: Credenciales inválidas o correo no verificado"
                }
            } catch (e: Exception) {
                loginResult.value = "Error de conexión: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}