package ec.edu.puce.swipeshare.ui.profile

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ec.edu.puce.swipeshare.R
import ec.edu.puce.swipeshare.services.RetrofitClient
import ec.edu.puce.swipeshare.services.TokenManager
import ec.edu.puce.swipeshare.viewmodels.ProfileViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var viewModel: ProfileViewModel

    private lateinit var tvKarma: TextView
    private lateinit var etEmail: EditText
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etBio: EditText
    private lateinit var btnSave: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        initViews()
        setupViewModel()
        observeViewModel()

        viewModel.loadMyProfile()
    }

    private fun initViews() {
        tvKarma = findViewById(R.id.tvKarma)
        etEmail = findViewById(R.id.etEmail)
        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etBio = findViewById(R.id.etBio)
        btnSave = findViewById(R.id.btnSave)
        progressBar = findViewById(R.id.progressBar)

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val bio = etBio.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.updateProfile(name, bio, phone)
        }
    }

    private fun setupViewModel() {
        val tokenManager = TokenManager(this)
        val apiService = RetrofitClient.getApiService(tokenManager)

        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(apiService) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.userProfile.observe(this) { profile ->
            profile?.let {
                tvKarma.text = "Karma: ${it.karma}"
                etEmail.setText(it.email)
                etName.setText(it.name)
                etPhone.setText(it.phone ?: "")
                etBio.setText(it.bio ?: "")
            }
        }

        viewModel.updateSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "¡Perfil actualizado con éxito!", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        }
    }
}