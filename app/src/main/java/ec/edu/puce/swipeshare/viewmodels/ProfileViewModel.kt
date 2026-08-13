package ec.edu.puce.swipeshare.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.puce.swipeshare.models.ReviewResponse
import ec.edu.puce.swipeshare.models.UpdateProfileRequest
import ec.edu.puce.swipeshare.models.UserProfileResponse
import ec.edu.puce.swipeshare.services.ApiService
import kotlinx.coroutines.launch

class ProfileViewModel(private val apiService: ApiService) : ViewModel() {

    val userProfile = MutableLiveData<UserProfileResponse?>()
    val activeItemsCount = MutableLiveData(0)
    val completedMatchesCount = MutableLiveData(0)
    val myReviewsList = MutableLiveData<List<ReviewResponse>>(emptyList())

    val updateSuccess = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>()

    fun loadMyProfile() {
        isLoading.value = true
        viewModelScope.launch {
            try {
                // 1. Obtener datos del perfil
                val response = apiService.getMyProfile()
                if (response.isSuccessful && response.body() != null) {
                    val profile = response.body()!!
                    userProfile.value = profile

                    // 2. Contar mis ítems activos
                    val itemsRes = apiService.getMyItems()
                    if (itemsRes.isSuccessful) {
                        activeItemsCount.value = itemsRes.body()?.size ?: 0
                    }

                    // 3. Contar mis matches aprobados
                    val matchesRes = apiService.getMyMatches()
                    if (matchesRes.isSuccessful) {
                        completedMatchesCount.value = matchesRes.body()?.count { it.status == "APPROVED" } ?: 0
                    }

                    // 4. Cargar las reseñas que otros me han dejado
                    val reviewsRes = apiService.getReviewsForUser(profile.cognitoId)
                    if (reviewsRes.isSuccessful) {
                        myReviewsList.value = reviewsRes.body() ?: emptyList()
                    }
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

    fun updateProfile(name: String, email: String, bio: String?, phone: String?) {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val request = UpdateProfileRequest(name = name, email = email, bio = bio, phone = phone)
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