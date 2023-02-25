package co.com.henryto.app_android_firebase_tutorial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import co.com.henryto.app_android_firebase_tutorial.databinding.ActivityAuthenticationBinding
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics // declaramos la variable "firebaseAnalytics" que vamos a
    //instanciar mas adelante como un objeto de la clase FirebaseAnalytics con el objetivo de integrar Analitics a nuestro
    //proyecto y poder registrar eventos personalizados con el metodo logEvent()

    private lateinit var binding: ActivityAuthenticationBinding

    override fun onCreate(savedInstanceState: Bundle?) { // la función onCreate es la que se encarga de instanciar la vista
        Log.d("========>", "En clase AuthenticationActivity en funcion OnCreate (--1--)")
        super.onCreate(savedInstanceState)
         //setContentView(R.layout.activity_authentication)
        binding = ActivityAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //************* Lanzando eventos personalizados a Google Analitics que podremos ver en nuestra consola de Firebase
        //************* en Events, estos eventos se visualizan despues de 24 horas despues de que que hace la integración

        firebaseAnalytics = Firebase.analytics // instanciamos la variable firebaseAnalytics como un objeto de
                                                   // clase FirebaseAnalytics y asi poder registrar eventos con el metodo logEvent()
            val bundle = Bundle() // creamos la variable bundle que es una instancia de la clase Bundle() a la cual acontinuación
                                // le agregamos uno o varios elementos compuestos por una clave y un valor.
            bundle.putString("message1", "Se ingreso a la Pantalla Inicial de la app")
            bundle.putString("message2", "Se ingreso a la Pantalla Inicial de la app 2")
            firebaseAnalytics.logEvent("InitScreen", bundle) // el metodo logEvent() recibe como parametros la clave
                                                            // "InitScreen" que pusimos como ejemplo pero puede ser cualquier
                                    // otra, y recibe parametros especificos y/o personalizados por medio del bundle que
                                    // creamos anteriormente

        //**************************************************************************************************************//

        setup()
    }

    private  fun setup(){
        title = "Autenticación"

        binding.buttonSignUp.setOnClickListener {
            userRegister()
        }

        binding.buttonLogin.setOnClickListener {
            userLogin()
        }
    }

    private fun userRegister(){ // Para registrar un nuevo usuario con FirebaseAuth.getInstance().createUserWithEmailAndPassword()
        //user registrado de ejemplo => henrybmx@gmail.com  pass: **Pass_EJM**2165
        //user registrado de ejemplo => henrybike@hotmaiil.com  pass: **Pass_EJM**2165
        if (!binding.editTextEmailAddress.text.isNullOrEmpty() && !binding.editTexPassword.text.isNullOrEmpty()){
            // La siguiente operacion, necesita 2 strings, el email y la contraseña que capturamos en el formulario y que son los
            // datos del usuario que quereremos registrar. A esta operacion le añadimos el listener .addOnCompleteListener
            // el cual nos informa si la operación de regitro con email y contraseña fue exitosa o no.
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                binding.editTextEmailAddress.text.toString(),
                binding.editTexPassword.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful){
                    // Si el registro del nuevo usuario es exitoso, la data del nuevo usuario podemos verla en plataforma web
                    // Firebase Autentication del proyecto correspondiente.
                    val gsonIt = Gson().toJson(it)
                    Log.d("========>", "En clase AuthenticationActivity en funcion userRegister (--1--)" +
                            " => gsonIt => " + gsonIt)
                    showHome(it.result?.user?.email ?: "---", ProviderTypeAuthentication.EMAIL_AND_PASSWORD)
                    // it.result?.user?.email ?: "" => si el email no tiene contenido, se envia un strung vacio, sin embargo
                    // esto es una situación que nunca debe pasar, ya que si la respusta es Successful, entonces si o si el
                    //email tiene que existir. Por otro lado, habriamos podido usar binding.editTextEmailAddress.text.toString(),
                    // en lugar de it.result?.user?.email ?: "".
                    //
                }
                else{
                    showAlertError("Error_Register")
                }
            }
        }
        else{
            Toast.makeText(this, "Debe diligenciar el formulario completamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun userLogin(){ // Para hacer login con un nuevo que ya estaba registrado con FirebaseAuth.getInstance().signInWithEmailAndPassword()
        if (!binding.editTextEmailAddress.text.isNullOrEmpty() && !binding.editTexPassword.text.isNullOrEmpty()){
            // La siguiente operacion, necesita 2 strings, el email y la contraseña que capturamos en el formulario y que son los
            // datos del usuario que usaremos para el login. A esta operacion le añadimos el listener .addOnCompleteListener
            // el cual nos informa si la operación de login con email y contraseña fue exitosa o no.
            FirebaseAuth.getInstance().signInWithEmailAndPassword(
                binding.editTextEmailAddress.text.toString(),
                binding.editTexPassword.text.toString()
            ).addOnCompleteListener {
                if (it.isSuccessful){
                    val gsonIt = Gson().toJson(it)
                    Log.d("========>", "En clase AuthenticationActivity en funcion userLogin (--1--)" +
                            " => gsonIt => " +gsonIt)
                    showHome(it.result?.user?.email ?: "---", ProviderTypeAuthentication.EMAIL_AND_PASSWORD)
                    // it.result?.user?.email ?: "" => si el email no tiene contenido, se envia un strung vacio, sin embargo
                    // esto es una situación que nunca debe pasar, ya que si la respusta es Successful, entonces si o si el
                    //email tiene que existir. Por otro lado, habriamos podido usar binding.editTextEmailAddress.text.toString(),
                    // en lugar de it.result?.user?.email ?: "".
                }
                else{
                    showAlertError("Error_Login")
                }
            }
        }
        else{
            Toast.makeText(this, "Debe diligenciar el formulario completamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAlertError(errorOrigin: String){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        if(errorOrigin == "Error_Register")
            builder.setMessage("Se ha producido un error registrando al usuario")
        if(errorOrigin == "Error_Login")
            builder.setMessage("Se ha producido un error iniciando la sesión de usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderTypeAuthentication){
        val homeIntent = Intent(this, HomeActivity::class.java)
        homeIntent.putExtra("email", email)
        homeIntent.putExtra("provider", provider.name)
        startActivity(homeIntent)
    }
}