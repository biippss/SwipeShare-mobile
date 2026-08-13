package ec.edu.puce.swipeshare.viewmodels

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ec.edu.puce.swipeshare.models.ReviewRequest
import ec.edu.puce.swipeshare.services.ApiService
import kotlinx.coroutines.launch

class ReviewViewModel(private val apiService: ApiService) : ViewModel() {

    val toastMessage = MutableLiveData<String?>()

    fun sendReview(targetUserId: String, rating: Int, comment: String?) {
        viewModelScope.launch {
            try {
                val response = apiService.createReview(
                    ReviewRequest(
                        targetUserId = targetUserId,
                        rating = rating,
                        comment = comment
                    )
                )
                if (response.isSuccessful) {
                    toastMessage.value = "¡Reseña enviada con éxito! ⭐"
                } else {
                    toastMessage.value = "Error al enviar reseña (${response.code()})"
                }
            } catch (e: Exception) {
                toastMessage.value = "Error de conexión: ${e.message}"
            }
        }
    }
}