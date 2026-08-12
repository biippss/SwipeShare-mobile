package ec.edu.puce.swipeshare.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.puce.swipeshare.models.CreateItemRequest
import ec.edu.puce.swipeshare.models.ItemResponse
import ec.edu.puce.swipeshare.services.ApiService
import kotlinx.coroutines.launch

class MyProductsViewModel(private val apiService: ApiService) : ViewModel() {

    val myItemsList = MutableLiveData<List<ItemResponse>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun loadMyItems() {
        isLoading.value = true
        errorMessage.value = null
        viewModelScope.launch {
            try {
                val response = apiService.getMyItems()
                if (response.isSuccessful && response.body() != null) {
                    myItemsList.value = response.body()!!
                } else {
                    errorMessage.value = "Error al obtener productos (${response.code()})"
                }
            } catch (e: Exception) {
                errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateItem(id: Long, title: String, description: String, category: String, imageUrl: String) {
        viewModelScope.launch {
            try {
                val request = CreateItemRequest(title, description, category, imageUrl)
                val response = apiService.updateItem(id, request)
                if (response.isSuccessful) {
                    loadMyItems()
                } else {
                    errorMessage.value = "Error al actualizar producto (${response.code()})"
                }
            } catch (e: Exception) {
                errorMessage.value = "Error de conexión: ${e.message}"
            }
        }
    }

    fun deleteItem(id: Long) {
        viewModelScope.launch {
            try {
                val response = apiService.deleteItem(id)
                if (response.isSuccessful) {
                    loadMyItems()
                } else {
                    errorMessage.value = "Error al eliminar producto (${response.code()})"
                }
            } catch (e: Exception) {
                errorMessage.value = "Error de conexión: ${e.message}"
            }
        }
    }
}