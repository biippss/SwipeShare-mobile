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
    val counterpartId: String,
    val counterpartName: String,
    val counterpartPhone: String?,
    val offeredItemTitle: String,
    val requestedItemTitle: String,
    val isIncoming: Boolean,
    val hasBeenReviewed: Boolean = false
)

class MatchesViewModel(private val apiService: ApiService) : ViewModel() {

    val matchesList = MutableLiveData<List<MatchUIItem>>(emptyList())
    val isLoading = MutableLiveData(false)
    val errorMessage = MutableLiveData<String?>()

    // Usamos el ID del Match para aislar las tarjetas y no afectar otras del mismo usuario
    private val localReviewedMatchIds = mutableSetOf<Long>()

    fun loadMyMatches() {
        isLoading.value = true
        errorMessage.value = null

        viewModelScope.launch {
            try {
                val profileRes = apiService.getMyProfile()
                val myCognitoId = profileRes.body()?.cognitoId?.trim().orEmpty()

                val response = apiService.getMyMatches()
                if (response.isSuccessful && response.body() != null) {
                    val rawMatches = response.body()!!
                    val enrichedList = mutableListOf<MatchUIItem>()

                    for (match in rawMatches) {
                        val isIncoming = match.user2Id.equals(myCognitoId, ignoreCase = true)
                        val counterpartId = if (match.user1Id.equals(myCognitoId, ignoreCase = true)) {
                            match.user2Id.trim()
                        } else {
                            match.user1Id.trim()
                        }

                        var counterpartName = "Usuario SwipeShare"
                        var counterpartPhone: String? = null

                        if (counterpartId.isNotBlank()) {
                            try {
                                val userRes = apiService.getUserProfile(counterpartId)
                                if (userRes.isSuccessful && userRes.body() != null) {
                                    counterpartName = userRes.body()?.name ?: counterpartName
                                    counterpartPhone = userRes.body()?.phone
                                }
                            } catch (_: Exception) { }
                        }

                        var user1ItemTitle = "Producto"
                        var user2ItemTitle = "Tu Producto"

                        match.offeredItemId?.let { id ->
                            try {
                                val itemRes = apiService.getItemById(id)
                                if (itemRes.isSuccessful) user1ItemTitle = itemRes.body()?.title ?: user1ItemTitle
                            } catch (_: Exception) { }
                        }

                        try {
                            val reqItemRes = apiService.getItemById(match.requestedItemId)
                            if (reqItemRes.isSuccessful) user2ItemTitle = reqItemRes.body()?.title ?: user2ItemTitle
                        } catch (_: Exception) { }

                        val offeredTitle = if (isIncoming) user1ItemTitle else user2ItemTitle
                        val requestedTitle = if (isIncoming) user2ItemTitle else user1ItemTitle

                        // Validamos SOLO con la sesión actual para que siempre inicie en "Calificar Usuario"
                        val hasBeenReviewed = localReviewedMatchIds.contains(match.id)

                        enrichedList.add(
                            MatchUIItem(
                                matchId = match.id,
                                status = match.status,
                                counterpartName = counterpartName,
                                counterpartPhone = counterpartPhone,
                                offeredItemTitle = offeredTitle,
                                requestedItemTitle = requestedTitle,
                                isIncoming = isIncoming,
                                counterpartId = counterpartId,
                                hasBeenReviewed = hasBeenReviewed
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

    // Funciones de actualización de UI compatibles con cualquier pantalla que tengas
    fun markMatchAsReviewed(matchId: Long) {
        localReviewedMatchIds.add(matchId)
        val currentList = matchesList.value ?: return
        matchesList.value = currentList.map { match ->
            if (match.matchId == matchId) {
                match.copy(hasBeenReviewed = true)
            } else {
                match
            }
        }
    }

    // Esta es la función restaurada que usa MainActivity.
    // La magia es que ahora busca solo la PRIMERA tarjeta no calificada y la bloquea.
    fun markUserAsReviewed(counterpartId: String) {
        val currentList = matchesList.value ?: return

        val matchToUpdate = currentList.firstOrNull {
            it.counterpartId == counterpartId && !it.hasBeenReviewed
        }

        if (matchToUpdate != null) {
            localReviewedMatchIds.add(matchToUpdate.matchId)
            matchesList.value = currentList.map { match ->
                if (match.matchId == matchToUpdate.matchId) {
                    match.copy(hasBeenReviewed = true)
                } else {
                    match
                }
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