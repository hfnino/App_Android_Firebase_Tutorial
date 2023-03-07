package co.com.henryto.app_android_firebase_tutorial

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import co.com.henryto.app_android_firebase_tutorial.databinding.ActivityAuthenticationBinding
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson

class AuthenticationActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics // declaramos la variable "firebaseAnalytics" que vamos a
    //instanciar mas adelante como un objeto de la clase FirebaseAnalytics con el objetivo de integrar Analitics a nuestro
    //proyecto y poder registrar eventos personalizados con el metodo logEvent()

    private lateinit var binding: ActivityAuthenticationBinding

    private val GOOGLE_SIGN_IN = 100
    private val callbackManager = CallbackManager.Factory.create() // la variable callbackManager es una instancia de la clase
        // CallbackManager de la libreria de Facebook que nos ayuda a administrar la devolución de llamadas y se encarga de las
        // respuestas de inicio de sesión

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
        validateSesion()
        setup()
    }

    private fun validateSesion() {

        val validatePrefsUserData: SharedPreferences = getSharedPreferences(getString(R.string.file_share_preferents), Context.MODE_PRIVATE)
        val email: String? = validatePrefsUserData.getString("email", null)
        val provider: String? = validatePrefsUserData.getString("provider", null)
        val idProvider: String? = validatePrefsUserData.getString("idProvider", null)
        val nameUser: String? = validatePrefsUserData.getString("nameUser", null)
        val imageProfile: String? = validatePrefsUserData.getString("imageProfile", null)

        validateSesionFacebook() //Para validar si existe una sesión de facebook iniciada

        if(!email.isNullOrEmpty() && !provider.isNullOrEmpty()){
            showHome(email, ProviderTypeAuthentication.valueOf(provider), idProvider?: "Sin Información",
                nameUser?: "Sin Información", imageProfile?: "Sin Información")
        }
    }

    private  fun validateSesionFacebook() {
        val accessToken = AccessToken.getCurrentAccessToken()
        if (accessToken != null && !accessToken.isExpired){
            Toast.makeText(this, "El inicio de sesión con Facebook esta activo", Toast.LENGTH_LONG).show()
        }
    }

    private  fun setup(){
        title = "Autenticación"

        binding.buttonSignUp.setOnClickListener {
            userRegisterEmailAndPassword()
        }

        binding.buttonLogin.setOnClickListener {
            userLoginEmailAndPassword()
        }

        binding.buttonGoogle.setOnClickListener {
            userLoginGoogleProvider()
        }

        binding.buttonFacebook.setOnClickListener {
            userLoginFacebookProvider()
        }
    }

    private fun userLoginFacebookProvider() {

        Log.d("========>", "En clase AuthenticationActivity en funcion userLoginFacebookProvider() (--0--)" )

        // LoginManager es una clase que pertenece a la libreria de autenticacion de Facebook
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile"/*,"email"*/) ) // la operación logInWithReadPermissions abre la
            // pantalla de autenticación nativa de Facebook a la cual le pasamos la activity correspondiente y una lista con los permisos
            // que queremos leer del usuario se se va a autenticar, que para el ejemplo sera el "perfil publico" y  el "email".

        // Callback registration, el siguient codigo no se ejecuta sino hasta que callbackManager recibe un resultado en la funcion
        //override fun onActivityResult....
        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult>{
                // FacebookCallback<LoginResult> es una operación que se llama a modo de callback  en el momento en que finalice
                // la authenticación con facebook, este callback permite implementar acciones cuando onSuccess, onCancel y onError
                override fun onSuccess(result: LoginResult) { // result puede ser nulo

                    // El login fon facebook no funciona correctamente debido a permisos que no se pudieron activar en
                    // https://developers.facebook.com, para mas información ver archivo README.md

                    val gsonResult = Gson().toJson(result)
                    Log.d("========>", "En clase AuthenticationActivity en funcion userLoginFacebookProvider() en funcion onSuccess (--1--)" +
                            " => gsonIt => " + gsonResult)

                    result.let { //Desempaquetamos el resultado del login exitoso con facebook en caso de que sea diferente de nulo.
                        // el método 'let' es una función de extensión utilizada para ejecutar un bloque de código sobre un objeto, sin tener que
                        // crear una variable para almacenarlo temporalmente. Esto es útil para evitar el uso de variables temporales innecesarias.
                        // El método 'let' se puede usar para ejecutar cualquier tipo de acción sobre un objeto, como realizar operaciones, filtrar
                        // por condiciones, comprobar si es nulo, etc.

                        val token = it.accessToken // capturamos el accessToken

                        val gsonToken = Gson().toJson(token)
                        Log.d("========>", "En clase AuthenticationActivity en funcion userLoginFacebookProvider() en funcion onSuccess (--2--)" +
                                " => gsonIt => " + gsonToken)

                        // Despues de habernos autenticado correctamente en facebook, llamaremos al servicio de Firebase encargado de guardar el
                        //registro de los usuarios en dicha plataforma

                        val credencial = FacebookAuthProvider.getCredential(token.token) // creamos la credencial que necesitamos con ayuda del token
                        // para registrar los datos del usuario en firebase

                        FirebaseAuth.getInstance().signInWithCredential(credencial).addOnCompleteListener {

                            // it es el objeto que llega como respuesta al proceso de registro del nuevo usuario en firebase.

                            if (it.isSuccessful){

                                // Si el registro del nuevo usuario es exitoso, la data del nuevo usuario podemos verla en plataforma web
                                // Firebase Autentication del proyecto correspondiente. OJOOOOOOOOO... Tener muy presente que si el email
                                // correspondiente ya se encuentra registrado en firebase Authentication con otro proveedor de login como
                                // el de "google" o el de "email y contraseña" entonces no el usuario no se va a poder registrar y/o
                                //iniciar sesión.

                                val gsonIt = Gson().toJson(it)
                                Log.d("========>", "En clase AuthenticationActivity en funcion userLoginFacebookProvider() en funcion onSuccess (--3--)" +
                                        " => gsonIt => " + gsonIt)


                                //showHome(it.result?.user?.email?: "---", ProviderTypeAuthentication.FACEBOOK)
                                // it.result?.user?.email ?: "" => si el email no tiene contenido, se envia un string con 3 guiones, sin embargo
                                // esto es una situación que nunca debe pasar, ya que si la respuesta de firebase es Successful, entonces si o si el
                                //email tiene que existir.
                                // La data del perfil de usuario que trae el objeto it de firebase tiene muy poca información del usuario, por tal motivo
                                // se decidio usar el siguiente bloque de codigo para poder obtener mas información del perfil de usuario de facebook
                                // y llamar a showHome(xxxxxxx xxxxx) =>


                                // *******************************Inicio bloque de codigo ************************************
                                // Este bloque de codigo, captura la información del perfil basico de Facebook.
                                // fuente => https://www.youtube.com/watch?v=b9AMK7s5xp4

                                val tokenfb = AccessToken.getCurrentAccessToken()
                                val request = GraphRequest.newMeRequest(tokenfb){ `object`, response ->
                                    val fbId = `object`?.getString("id")
                                    val fbEmail = `object`?.getString("email")
                                    val fbNameProfile = `object`?.getString("name")
                                    val fbImageProfile = `object`?.getJSONObject("picture")
                                        ?.getJSONObject("data")?.getString("url")

                                    Log.d("========>", "En clase AuthenticationActivity en funcion userLoginFacebookProvider() en funcion onSuccess (--4--)" +
                                            " => fbId => " + fbId +
                                            " => fbEmail => " + fbEmail +
                                            " => fbNameProfile => " + fbNameProfile +
                                            " => fbImageProfile => " + fbImageProfile
                                    )

                                    showHome(fbEmail?: "---", ProviderTypeAuthentication.FACEBOOK, fbId?: "---",
                                                fbNameProfile?: "---", fbImageProfile?: "---")
                                }

                                val parameters = Bundle()
                                parameters.putString("fields", "id, name, link, picture.type(large), email ")
                                request.parameters = parameters
                                request.executeAsync()

                                //******************************* Fin bloque de codigo ************************************
                            }
                            else{
                                Log.d("========>", "En clase AuthenticationActivity en funcion userLoginFacebookProvider() en funcion onSuccess (--5--)")
                                showAlertError("Error_Register")
                            }
                        }
                    }
                }

                override fun onCancel() {
                    Toast.makeText(this@AuthenticationActivity, "Has cancelado el inicio de sesión con Facebook", Toast.LENGTH_SHORT).show()
                }

                override fun onError(error: FacebookException) {
                    showAlertError("Error_Login")
                }
            }
        )
    }

    private fun userLoginGoogleProvider() {
        //Configuración del login con google
        val googleConfing = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN) // Usamos el Login por defecto de nuestra cuenta de Google
            .requestIdToken(getString(R.string.default_web_client_id)) // R.string.default_web_client_id) es el token id asociado a nuestra app
            .requestEmail() // es el dato del Email que queremos solicitar de la data que se captura del la cuenta de google.
            .build()

        //Creamos el cliente de autenticación con Google
        val googleClient = GoogleSignIn.getClient(this, googleConfing)
        googleClient.signOut() // para cerrar la sesión de la cuenta que pueda estar auenticada en ese momento. Lo anterior es util si tenemos mas de una cuenta
                                //asociada a nuestro dispositivo movil

        startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN)  // Muestra la pantalla de autenticación de google para que el usuario seleccione una cuenta
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        Log.d("========>", "En clase AuthenticationActivity en funcion onActivityResult() (--0--)" )

        callbackManager.onActivityResult(requestCode, resultCode, data) // desencadena una llamada a una de las 3 operaciones configuradas en la función
        //userLoginFacebookProvider(), es decir a override fun onSuccess(result: LoginResult?) o a override fun onCancel() o a override fun onError(error: FacebookException?)
        //dependiendo de lo que se halla obtenido como resultado en el proceso de login con facebook.

        super.onActivityResult(requestCode, resultCode, data)

        //Para que el proceso de authenticación con cuentas de google funcione correctamente, en la plataforma Firebase => configuracion del proyecto.
        // debemos añadir el certificado de nuestra app, es decir la huella digital SHA1 de nuestro proyecto Android, el cual obtenemos desde
        // AndroidStudio en la opción Gradle => App_Android_Firebase_Tutorial => Tasks => android => signingReport.  la configuración del certificado,
        // nos asegura que nuestro proyecto de firebase esya relacionado con nuestro proyecto de android studio.

        // Si cargaramos nuestra app a la tienda de aplicaciones Google Play Storem tambien tendremos que crear una huella digital asociada a la app de
        // producción.

        if(requestCode == GOOGLE_SIGN_IN){  // Significa que la respuesta corresponde al proceso de autenticación de google.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)  //Capturamos todos los datos de la cuenta

            // Sintaxis 1 para obtener los datos de la cuenta
            Log.d("========>", "En clase AuthenticationActivity en funcion onActivityResult()  (--1--)" +
                    " => task.result_googleId => " + (task.result.id?: "---") +
                    " => task.result_googleEmail => " + (task.result.email?: "---") +
                    " => task.result_googleNameProfile => " + (task.result.givenName?: "---") +
                    " => task.result_googleImageProfile => " + (task.result.photoUrl?: "---")
            )

            try {
                val account = task.getResult(ApiException::class.java) //Capturamos todos los satos de la cuenta

                if (account != null){

                    // Sintaxis 2 para obtener los datos de la cuenta
                    Log.d("========>", "En clase AuthenticationActivity en funcion onActivityResult()  (--2--)" +
                            " => account_googleId => " + (account.id?: "---") +
                            " => account_googleEmail => " + (account.email?: "---") +
                            " => account_googleNameProfile => " + (account.givenName?: "---") +
                            " => account_googleImageProfile => " + (account.photoUrl?: "---")
                    )

                    val credencial = GoogleAuthProvider.getCredential(account.idToken, null) // creamos la credencial que necesitamos con ayuda del token
                    // para registrar los datos del usuario en firebase
                    FirebaseAuth.getInstance().signInWithCredential(credencial).addOnCompleteListener {

                        // it es el objeto que llega como respuesta al proceso de registro del nuevo usuario en firebase.

                        if (it.isSuccessful){

                            // Si el registro del nuevo usuario es exitoso, la data del nuevo usuario podemos verla en plataforma web
                            // Firebase Autentication del proyecto correspondiente. OJOOOOOOOOO... Tener muy presente que si el email
                            // correspondiente ya se encuentra registrado en firebase Authentication con otro proveedor de login como
                            // el de "google" o el de "email y contraseña" entonces el usuario no se va a poder registrar y/o
                            //iniciar sesión.

                            val gsonIt = Gson().toJson(it)

                            Log.d("========>", "En clase AuthenticationActivity en funcion onActivityResult() (--3--)" +
                                    " => gsonIt => " + gsonIt)


                            // Sintaxis 3 para obtener los datos de la cuenta, esta ves teniencue en cuenta
                            Log.d("========>", "En clase AuthenticationActivity en funcion onActivityResult()  (--4--)" +
                                    " => it.result.user_googleId => " + (it.result.user?.providerId?: "---") +
                                    " => it.result.user_googleEmail => " + (it.result.user?.email?: "---") +
                                    " => it.result.user_googleNameProfile => " + (it.result.user?.tenantId?: "---") +
                                    " => it.result.user_googleImageProfile => " + (it.result.user?.photoUrl?: "---")
                            )

                            //showHome(it.result?.user?.email?: "---", ProviderTypeAuthentication.GOOGLE)
                            // it.result?.user?.email ?: "" => si el email no tiene contenido, se envia un string con 3 guiones, sin embargo
                            // esto es una situación que nunca debe pasar, ya que si la respuesta de firebase es Successful, entonces si o si el
                            //email tiene que existir.
                            // La data del perfil de usuario que trae el objeto it de firebase tiene muy poca información del usuario, por tal motivo
                            // se decidio usar el objeto account  =>

                            showHome(account.email?: "---", ProviderTypeAuthentication.GOOGLE, account.id?: "---",
                                        account.givenName?: "---", account.photoUrl.toString()?: "---")


                        }
                        else{
                            showAlertError("Error_Register")
                        }
                    }
                }
                else{
                    Toast.makeText(this, "Error en la authenticación con Google, intente nuevamente", Toast.LENGTH_SHORT).show()
                }
            }catch (e: ApiException){
                showAlertError("Error_Register")
            }
        }
    }

    private fun userRegisterEmailAndPassword(){ // Para registrar un nuevo usuario con FirebaseAuth.getInstance().createUserWithEmailAndPassword()
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
                    Log.d("========>", "En clase AuthenticationActivity en funcion userRegisterEmailAndPassword (--1--)" +
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

    private fun userLoginEmailAndPassword(){ // Para hacer login con un nuevo que ya estaba registrado con FirebaseAuth.getInstance().signInWithEmailAndPassword()
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
                    Log.d("========>", "En clase AuthenticationActivity en funcion userLoginEmailAndPassword (--1--)" +
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
            builder.setMessage("Se ha producido un error registrando al usuario, posiblemente, " +
                    "la cuenta de correo asociada ya se encuentre registrada")
        if(errorOrigin == "Error_Login")
            builder.setMessage("Se ha producido un error iniciando la sesión de usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun showHome(email: String, provider: ProviderTypeAuthentication, idProvider: String = "Sin Información",
                         nameUser: String = "Sin Información", imageProfile: String = "Sin Información"){
        val homeIntent = Intent(this, HomeActivity::class.java)
        homeIntent.putExtra("email", email)
        homeIntent.putExtra("provider", provider.name)
        homeIntent.putExtra("idProvider", idProvider)
        homeIntent.putExtra("nameUser", nameUser)
        homeIntent.putExtra("imageProfile", imageProfile)
        startActivity(homeIntent)
        finish()
    }
}