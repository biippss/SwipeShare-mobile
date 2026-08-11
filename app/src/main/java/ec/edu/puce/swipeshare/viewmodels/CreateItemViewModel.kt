package ec.edu.puce.swipeshare.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.puce.swipeshare.models.CreateItemRequest
import ec.edu.puce.swipeshare.models.ItemResponse
import ec.edu.puce.swipeshare.services.ApiService
import kotlinx.coroutines.launch

class CreateItemViewModel(private val apiService: ApiService) : ViewModel() {

    val isLoading = MutableLiveData(false)
    val isSuccess = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun createProduct(title: String, description: String, category: String, imageUrl: String?) {
        if (title.isBlank() || description.isBlank() || category.isBlank()) {
            errorMessage.value = "Por favor completa todos los campos requeridos."
            return
        }

        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = apiService.createItem(
                    CreateItemRequest(
                        title = title.trim(),
                        description = description.trim(),
                        category = category.trim(),
                        imageUrl = imageUrl?.ifBlank { null }
                    )
                )
                if (response.isSuccessful) {
                    isSuccess.value = true
                } else {
                    errorMessage.value = "Error al publicar (${response.code()})"
                }
            } catch (e: Exception) {
                errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }
}