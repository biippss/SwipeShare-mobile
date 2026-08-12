package ec.edu.puce.swipeshare.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.puce.swipeshare.models.UpdateMatchStatusRequest
import ec.edu.puce.swipeshare.services.ApiService
import kotlinx.coroutines.launch

data class MatchUIItem(
    val matchId: Long,
    val status: String,
    val counterpartName: String,
    val counterpartPhone: String?,
    val offeredItemTitle: String,
    val requestedItemTitle: String,
    val isIncoming: Boolean
)

class MatchesViewModel(private val apiService: ApiService) : ViewModel() {

    val matchesList = MutableLiveData<List<MatchUIItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    fun loadMyMatches() {
        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch {
            try {
                val profileRes = apiService.getMyProfile()
                val myCognitoId = profileRes.body()?.cognitoId ?: ""

                val response = apiService.getMyMatches()
                if (response.isSuccessful && response.body() != null) {
                    val rawMatches = response.body()!!
                    val enrichedList = mutableListOf<MatchUIItem>()

                    for (match in rawMatches) {
                        val isIncoming = (match.user2Id == myCognitoId)
                        val counterpartId = if (match.user1Id == myCognitoId) match.user2Id else match.user1Id

                        val userRes = apiService.getUserProfile(counterpartId)
                        val counterpartName = userRes.body()?.name ?: "Usuario SwipeShare"
                        val counterpartPhone = userRes.body()?.phone

                        var user1ItemTitle = "Producto"
                        var user2ItemTitle = "Tu Producto"

                        // Cargar título del producto de User 1 (offeredItemId)
                        match.offeredItemId?.let { id ->
                            val itemRes = apiService.getItemById(id)
                            if (itemRes.isSuccessful) user1ItemTitle = itemRes.body()?.title ?: user1ItemTitle
                        }

                        // Cargar título del producto de User 2 (requestedItemId)
                        val reqItemRes = apiService.getItemById(match.requestedItemId)
                        if (reqItemRes.isSuccessful) user2ItemTitle = reqItemRes.body()?.title ?: user2ItemTitle

                        // INVERSIÓN DINÁMICA DE TÍTULOS SEGÚN EL USUARIO AUTENTICADO:
                        val offeredTitle: String
                        val requestedTitle: String

                        if (isIncoming) {
                            // Si yo soy User 2: Me ofrecen el ítem de User 1 a cambio de Mi ítem (User 2)
                            offeredTitle = user1ItemTitle
                            requestedTitle = user2ItemTitle
                        } else {
                            // Si yo soy User 1: Me ofrecen el ítem de User 2 a cambio de Mi ítem (User 1)
                            offeredTitle = user2ItemTitle
                            requestedTitle = user1ItemTitle
                        }

                        enrichedList.add(
                            MatchUIItem(
                                matchId = match.id,
                                status = match.status,
                                counterpartName = counterpartName,
                                counterpartPhone = counterpartPhone,
                                offeredItemTitle = offeredTitle,
                                requestedItemTitle = requestedTitle,
                                isIncoming = isIncoming
                            )
                        )
                    }
                    matchesList.value = enrichedList
                } else {
                    errorMessage.value = "Error al obtener matches (${response.code()})"
                }
            } catch (e: Exception) {
                errorMessage.value = "Error de conexión: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateMatchStatus(matchId: Long, newStatus: String) {
        viewModelScope.launch {
            try {
                val request = UpdateMatchStatusRequest(status = newStatus)
                val response = apiService.updateMatchStatus(matchId, request)
                if (response.isSuccessful) {
                    loadMyMatches()
                } else {
                    errorMessage.value = "Error al actualizar match (${response.code()})"
                }
            } catch (e: Exception) {
                errorMessage.value = "Error de conexión: ${e.message}"
            }
        }
    }
}