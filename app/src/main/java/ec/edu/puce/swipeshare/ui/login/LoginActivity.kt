package ec.edu.puce.swipeshare.ui.login

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ec.edu.puce.swipeshare.R
import ec.edu.puce.swipeshare.services.RetrofitClient
import ec.edu.puce.swipeshare.services.TokenManager
import ec.edu.puce.swipeshare.viewmodels.AuthViewModel
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar

class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Asegúrate que el nombre coincida

        // Inicializar componentes
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)

        // Inicializar ViewModel manualmente (sin Factory por simplicidad)
        val tokenManager = TokenManager(this)
        val apiService = RetrofitClient.getApiService(tokenManager)

        // Factory simple para pasar parámetros al VM
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(apiService, tokenManager) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        // Observar estados
        viewModel.isLoading.observe(this) { loading ->
            progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        }

        viewModel.loginResult.observe(this) { error ->
            if (error == null) {
                Toast.makeText(this, "Login Exitoso!", Toast.LENGTH_SHORT).show()
                // Aquí deberías navegar a la siguiente Activity
            } else {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        }

        btnLogin.setOnClickListener {
            viewModel.login(etEmail.text.toString(), etPassword.text.toString())
        }
    }
}