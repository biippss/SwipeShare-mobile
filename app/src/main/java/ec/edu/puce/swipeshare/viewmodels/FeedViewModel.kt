package ec.edu.puce.swipeshare.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.puce.swipeshare.models.ItemResponse
import ec.edu.puce.swipeshare.models.SwipeRequest
import ec.edu.puce.swipeshare.services.ApiService
import kotlinx.coroutines.launch

class FeedViewModel(private val apiService: ApiService) : ViewModel() {

    val currentItem = MutableLiveData<ItemResponse?>()
    val matchEvent = MutableLiveData<Boolean>()
    val isLoading = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String?>()

    private var currentIndex = 0
    private var itemList: List<ItemResponse> = emptyList()

    fun loadFeed() {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val response = apiService.getFeed()
                if (response.isSuccessful && response.body() != null) {
                    itemList = response.body()!!
                    currentIndex = 0
                    updateCurrentItem()
                } else {
                    errorMessage.value = "Error al cargar productos (${response.code()})"
                }
            } catch (e: Exception) {
                errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun swipe(action: String) { // "LIKE" o "PASS"
        val item = currentItem.value ?: return

        viewModelScope.launch {
            try {
                val response = apiService.sendSwipe(SwipeRequest(item.id, action))
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.isMatch) {
                        matchEvent.value = true
                    }
                }
            } catch (e: Exception) {
                // Silencioso para mantener fluidez en el feed
            }
            nextItem()
        }
    }

    private fun nextItem() {
        currentIndex++
        updateCurrentItem()
    }

    private fun updateCurrentItem() {
        if (currentIndex < itemList.size) {
            currentItem.value = itemList[currentIndex]
        } else {
            currentItem.value = null // Fin del catálogo
        }
    }
}