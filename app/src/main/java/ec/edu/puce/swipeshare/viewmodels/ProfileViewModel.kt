package ec.edu.puce.swipeshare.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.puce.swipeshare.models.UpdateProfileRequest
import ec.edu.puce.swipeshare.models.UserProfileResponse
import ec.edu.puce.swipeshare.services.ApiService
import kotlinx.coroutines.launch

class ProfileViewModel(private val apiService: ApiService) : ViewModel() {

    val userProfile = MutableLiveData<UserProfileResponse?>()
    val updateSuccess = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>()

    fun loadMyProfile() {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response = apiService.getMyProfile()
                if (response.isSuccessful && response.body() != null) {
                    userProfile.value = response.body()
                } else {
                    errorMessage.value = "Error al obtener perfil (${response.code()})"
                }
            } catch (e: Exception) {
                errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateProfile(name: String, bio: String?, phone: String?) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val request = UpdateProfileRequest(name, bio, phone)
                val response = apiService.updateProfile(request)
                if (response.isSuccessful && response.body() != null) {
                    userProfile.value = response.body()
                    updateSuccess.value = true
                } else {
                    errorMessage.value = "Error al actualizar perfil (${response.code()})"
                }
            } catch (e: Exception) {
                errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}