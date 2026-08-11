package ec.edu.puce.swipeshare.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.puce.swipeshare.models.LoginRequest
import ec.edu.puce.swipeshare.services.ApiService
import ec.edu.puce.swipeshare.services.TokenManager
import kotlinx.coroutines.launch

class AuthViewModel(private val apiService: ApiService, private val tokenManager: TokenManager) : ViewModel() {

    val loginResult = MutableLiveData<String?>() // null = éxito, string = error
    val isLoading = MutableLiveData<Boolean>()

    fun login(email: String, password: String) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response = apiService.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    tokenManager.saveToken(response.body()!!.token)
                    loginResult.value = null // Éxito
                } else {
                    loginResult.value = "Error: Credenciales inválidas"
                }
            } catch (e: Exception) {
                loginResult.value = "Error: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}