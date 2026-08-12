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
    val myItemsList = MutableLiveData<List<ItemResponse>>(emptyList())
    val matchEvent = MutableLiveData<Boolean>()
    val showNoItemsDialog = MutableLiveData<Boolean>()
    val showOfferSelectionDialog = MutableLiveData<Boolean>(false)
    val toastMessage = MutableLiveData<String?>()
    val isLoading = MutableLiveData<Boolean>()

    private var currentIndex = 0
    private var itemList: List<ItemResponse> = emptyList()

    fun loadFeed() {
        isLoading.value = true
        viewModelScope.launch {
            try {
                val myItemsResponse = apiService.getMyItems()
                val myMatchesResponse = apiService.getMyMatches()

                if (myItemsResponse.isSuccessful) {
                    val allMyItems = myItemsResponse.body() ?: emptyList()

                    // Obtener los IDs de ítems en trueques ya APROBADOS
                    val busyItemIds = if (myMatchesResponse.isSuccessful) {
                        myMatchesResponse.body()
                            ?.filter { it.status == "APPROVED" }
                            ?.flatMap { listOfNotNull(it.offeredItemId, it.requestedItemId) }
                            ?.toSet() ?: emptySet()
                    } else {
                        emptySet()
                    }

                    // Solo dejamos disponibles los ítems libres
                    myItemsList.value = allMyItems.filter { it.id !in busyItemIds }
                }

                val response = apiService.getFeed()
                if (response.isSuccessful && response.body() != null) {
                    itemList = response.body()!!
                    currentIndex = 0
                    updateCurrentItem()
                }
            } catch (e: Exception) {
                toastMessage.value = "Error de conexión: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun swipe(action: String) {
        val item = currentItem.value ?: return
        val swipeType = if (action.equals("LIKE", ignoreCase = true)) "LIKE" else "DISLIKE"

        if (swipeType == "DISLIKE") {
            sendSwipeRequest(item.id, swipeType, null)
            return
        }

        val myItems = myItemsList.value ?: emptyList()
        when {
            myItems.isEmpty() -> showNoItemsDialog.value = true
            myItems.size == 1 -> sendSwipeRequest(item.id, "LIKE", myItems.first().id)
            else -> showOfferSelectionDialog.value = true // Activa el modal en el Screen
        }
    }

    fun confirmOfferSelection(offeredItemId: Long) {
        showOfferSelectionDialog.value = false
        val item = currentItem.value ?: return
        sendSwipeRequest(item.id, "LIKE", offeredItemId)
    }

    private fun sendSwipeRequest(targetItemId: Long, type: String, offeredItemId: Long?) {
        viewModelScope.launch {
            try {
                val request = SwipeRequest(targetItemId = targetItemId, type = type, offeredItemId = offeredItemId)
                val response = apiService.sendSwipe(request)
                if (response.isSuccessful && response.body() != null) {
                    if (response.body()!!.isMatch) {
                        matchEvent.value = true
                    } else if (type == "LIKE") {
                        toastMessage.value = "¡Solicitud de trueque enviada!"
                    }
                }
            } catch (_: Exception) {}
            nextItem()
        }
    }

    private fun nextItem() {
        currentIndex++
        updateCurrentItem()
    }

    private fun updateCurrentItem() {
        currentItem.value = itemList.getOrNull(currentIndex)
    }
}