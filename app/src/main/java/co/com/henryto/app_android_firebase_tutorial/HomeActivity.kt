package co.com.henryto.app_android_firebase_tutorial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import co.com.henryto.app_android_firebase_tutorial.databinding.ActivityAuthenticationBinding
import co.com.henryto.app_android_firebase_tutorial.databinding.ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_home)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle: Bundle? = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")
        setup(email ?: "Error email", provider ?: "Error provider")
    }

    fun setup(email: String, provider: String){
        title = "Home"

        binding.textViewEmail.text = "Email: " + email
        binding.textViewProvider.text = "Proveedor de Autenticación: " + provider
        binding.buttonSingOut.setOnClickListener {
            singOut()
        }

    }

    private fun singOut() { // Cierra la sesión.
        FirebaseAuth.getInstance().signOut()
        onBackPressed()
    }
}