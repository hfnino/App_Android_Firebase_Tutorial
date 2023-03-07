package co.com.henryto.app_android_firebase_tutorial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsLogger

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        // Debemos tener claro que en el AndroidManifest le configuramos a la activity SplashActivity
        // el tema "@style/Theme_Splash_Screen_1" que es el encargado de mostrar el splash_screen_1 antes de que
        // se ejecute la función onCreate anterior (tener en cuenta el ciclo de vida de una Activity).

        Log.d("========>", "En clase SplashActivity en funcion OnCreate")

        Thread.sleep(2500) // para mostrar el splash_screen_1 por 2,5 seg

        super.onCreate(savedInstanceState)

        startActivity(Intent(this, AuthenticationActivity::class.java))
    }
}