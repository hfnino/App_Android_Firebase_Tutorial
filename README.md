esta app fue creada siguiendo el curso de firebase de MoureDev

==> https://www.youtube.com/watch?v=KYPc7CAYJOw&t=2s  

==> Logos firebase => https://firebase.google.com/brand-guidelines?hl=es-419

https://makeappicon.com/ ==> en esta pagina pudemos cargar una unica imagen, y como resultado
obtenemos automaticamente varias imagenes (iconos) de diferentes tamaños, para ser agregadas a nuesta app.
==> https://www.youtube.com/watch?v=_y904vt4Nzk

crear splash screen => https://www.youtube.com/watch?v=ksaaMt8Lo6U
                    => https://www.youtube.com/watch?v=60_Vao-oaKI&t=5s (Este metodo es similar pero mejor)

==> Implementación de Facebook Authentication =>  
    
    => creamos cuenta y nos hacemos login en https://developers.facebook.com/ =>
    creamos un nuevo proyecto, obtenemos el  Identificador de la app y Clave secreta del proyecto creado => 
    los agregamos en firebase Authentication y activamos el servicio de autenticación con facebook en Firebase.
    => En la plataforma de facebook para desarrolladores https://developers.facebook.com, agregamos un nuevo producto 
    para inicio de sesión con facebook => para Android => y seguimos el instructivo paso a paso.

    => En el paso 4 "Agregar los hashes de clave de desarrollo y activación", podemos generar el hash de clave de desarrollo 
    de 2 formas:

    la primara forma es => 

        - AndroidStudio en la opción Gradle => App_Android_Firebase_Tutorial => Tasks => android => signingReport t copliamos la cadena
          SHA1 correspondiente.

        - abrimos la pagina http://tomeko.net/online_tools/hex_to_base64.php u otra similar y comvertimos el SHA1 en base 64,  despues 
          de lo anterior, se nos muestra la clave por ejemplo XJY8M6LRLENEhru5+BlCNqJR7ZM= que es el hash de clave de desarrollo 
    
          fuente => https://stackoverflow.com/questions/5306009/facebook-android-generate-key-hash/12405323#12405323

    en windows asi:

        -  Descargamos la Biblioteca OpenSSL para Windows openssl-for-windows desde https://code.google.com/archive/p/openssl-for-windows/downloads), 
           la descomprimimos y la ubicamos en cualquier ruta, para el ejemplo, la deje en C:\Users\director.ti\AndroidStudioProjects\openssl-0.9.8k_X64

        -  En el paso a paso de la guia de facebook nos dan el comando => 
            
            keytool -exportcert -alias androiddebugkey -keystore "C:\Users\USERNAME\.android\debug.keystore" | "PATH_TO_OPENSSL_LIBRARY\bin\openssl" sha1 -binary | "PATH_TO_OPENSSL_LIBRARY\bin\openssl" base64
          
            el cual personalizamos con las rutas correspondientes y en mi caso qedo asi =>

            keytool -exportcert -alias androiddebugkey -keystore "C:\Users\director.ti\.android\debug.keystore" | "C:\Users\director.ti\AndroidStudioProjects\openssl-0.9.8k_X64\bin\openssl" sha1 -binary | "C:\Users\director.ti\AndroidStudioProjects\openssl-0.9.8k_X64\bin\openssl" base64

        -   vamos a la ruta donde esta instalado el JDK (Kit de desarrollo de Java) que en mi caso es C:\Windows\System32\cmd.exe
            y en esa ruta abrimos una consola de comandos cmd y ejecutamos el comando anterior => el sistema no pide poner una contraseña para el
            almacen de claves, para el ejemplo le pusimos: Pass_KeyStore_2023  ===> despues de lo anterior, se nos muestra la clave 
                            XJY8M6LRLENEhru5+BlCNqJR7ZM=
            que es el hash de clave de desarrollo 

        -   Recordar que si la app es desplegada en tienda Google Play Store, se debe generar tambien la clave de activación que es la de producción
            ya que no sera suficiente la clave de desarrollo.

    => OJO => En la plataforma para desarrolladores de Facebook, => Productos => Inicio de sesión don Facebook => Configuración => debemos diligenciar el
       campo "URI de redireccionamiento de OAuth válidos", donde ponermos el URI que obtenemos en Firebase => Proyecto correspondiente => Authentication
       Sign-in-method => editar proveedor Facebook. Lo anterior es importante ya que la plataformas de desarroladores de Facebook y la plataforma
       Firebase Autentication con Fecebook deben estar relacionados ya que nos autenticatemos con una cuenta de Facebook y luego nuestros usuarios van a 
       quedar registrados de forma centralizada en Firebase
    

==> vamos en 44 min y 10 seg
