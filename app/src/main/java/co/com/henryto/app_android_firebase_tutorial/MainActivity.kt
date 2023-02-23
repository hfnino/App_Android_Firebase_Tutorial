package co.com.henryto.app_android_firebase_tutorial

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics // declaramos la variable "firebaseAnalytics" que vamos a
    //instanciar mas adelante como un objeto de la clase FirebaseAnalytics con el objetivo de integrar Analitics a nuestro
    //proyecto y poder registrar eventos personalizados con el metodo logEvent()
    //

    override fun onCreate(savedInstanceState: Bundle?) { // la función onCreate es la que se encarga de instanciar la vista

        Thread.sleep(2000) // solo para mostrar el splash screen 2 seg mas.

        setTheme(R.style.Theme_App_Android_Firebase_Tutorial) // Debemos tener claro que en el AndroidManifest le configuramos
        // al MainActivity el tema "@style/Theme_Splash_Screen" que es el encargado de mostrar el splash screen antes de que
        // se ejecute la función onCreate anterior (tener en cuenta el ciclo de vida de una Activity). el metodo setTheme(),
        // lo usamos para establecer nuevamente el tema por defecto cuando se ejecuta la funcion onCreate, por lo que
        // el splash screen desaparece,
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //************* Lanzando eventos personalizados a Google Analitics que podremos ver en nuestra consola de Firebase
        //************* en Events, estos eventos se visualizan despues de 24 horas despues de que que hace la integración

        firebaseAnalytics = Firebase.analytics // instanciamos la variable firebaseAnalytics como un objeto de
                                                   // clase FirebaseAnalytics y asi poder registrar eventos con el metodo logEvent()
            val bundle = Bundle() // creamos la variable bundle que es una instancia de la clase Bundle() a la cual acontinuación
                                // le agregamos un elemento compuestos por una clave y un valor.
            bundle.putString("message", "Se ingreso a la Pantalla Inicial de la app")
            firebaseAnalytics.logEvent("InitScreen", bundle) // el metodo logEvent() recibe como parametros la clave
                                                            // "InitScreen" que pusimos como ejemplo pero puede ser cualquier
                                    // otra, y recibe parametros especificos y/o personalizados por medio del bundle que
                                    // creamos anteriormente

        //**************************************************************************************************************//
    }
}