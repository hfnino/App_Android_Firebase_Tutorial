package co.com.henryto.app_android_firebase_tutorial

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import co.com.henryto.app_android_firebase_tutorial.databinding.ActivityAuthenticationBinding
import co.com.henryto.app_android_firebase_tutorial.databinding.ActivityHomeBinding
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
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
        val idProvider = bundle?.getString("idProvider")
        val nameUser = bundle?.getString("nameUser")
        val imageProfile = bundle?.getString("imageProfile")


        setup(email ?: "Error email", provider ?: "Error provider", idProvider?: "Error Id Provider",
            nameUser?: "Error Name User", imageProfile?: "Error image Profile")
    }

    fun setup(email: String, provider: String, idProvider: String, nameUser: String, imageProfile:String){
        title = "Home"

        binding.textViewEmail.text = "Email: " + email
        binding.textViewProvider.text = "Proveedor de Autenticación: " + provider
        binding.textViewIdProvider.text = "Id del Proveedor de Login: " + idProvider
        binding.textViewNameUser.text = "Nombre: " + nameUser
        Glide.with(applicationContext).load(imageProfile).into(binding.imageViewProfile)


        saveUserData(email, provider, idProvider, nameUser, imageProfile) // Guardamos la información del usuario localmente con share preferents

        binding.buttonSingOut.setOnClickListener {
            singOut(email, provider)
        }

    }

    private fun saveUserData(email: String, provider: String, idProvider: String, nameUser: String, imageProfile: String) {
        val savePrefsUserData: SharedPreferences.Editor = getSharedPreferences(getString(R.string.file_share_preferents), Context.MODE_PRIVATE).edit()
        savePrefsUserData.putString("email", email)
        savePrefsUserData.putString("provider", provider)
        savePrefsUserData.putString("idProvider", idProvider)
        savePrefsUserData.putString("nameUser", nameUser)
        savePrefsUserData.putString("imageProfile", imageProfile)
        savePrefsUserData.apply() // confirmamos para guardar los datos anteriores.
    }

    private fun deleteUserData(email: String, provider: String) {
        val removePrefsUserData: SharedPreferences.Editor = getSharedPreferences(getString(R.string.file_share_preferents), Context.MODE_PRIVATE).edit()
        removePrefsUserData.clear()
        removePrefsUserData.apply() // confirmamos para remover los datos anteriores.
    }

    private fun singOut(email: String, provider: String) { // Cierra la sesión
        deleteUserData(email, provider) // Borra los datos del usuario de las share preferents

        if(provider == ProviderTypeAuthentication.FACEBOOK.name){
            LoginManager.getInstance().logOut()  // Cerramos sesión en facebook
        }

        FirebaseAuth.getInstance().signOut()

        startActivity(Intent(this, AuthenticationActivity::class.java))
        finish()
    }

}