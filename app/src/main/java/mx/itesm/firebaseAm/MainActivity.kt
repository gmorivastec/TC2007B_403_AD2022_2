package mx.itesm.firebaseAm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    lateinit var login : EditText
    lateinit var password : EditText
    lateinit var nombre : EditText
    lateinit var edad : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        login = findViewById(R.id.login)
        password = findViewById(R.id.password)
        nombre = findViewById(R.id.nombre)
        edad = findViewById(R.id.edad)
    }

    fun registro(view : View?){

        var loginData = login.text.toString()
        var passwordData = password.text.toString()

        var authTask = Firebase.auth.createUserWithEmailAndPassword(loginData, passwordData)

        authTask.addOnCompleteListener(this) { resultado ->

            if(resultado.isSuccessful) {

                Toast.makeText(this, "REGISTRO EXITOSO", Toast.LENGTH_SHORT).show()
            } else {

                Toast.makeText(this, "ERROR: ${resultado.exception}", Toast.LENGTH_SHORT).show()
                // Wtf - what a terrible failure!
                Log.wtf("FIREBASE", "ERROR: ${resultado.exception}")
            }
        }

    }

    fun login(view : View?) {

        var loginData = login.text.toString()
        var passwordData = password.text.toString()

        var authTask = Firebase.auth.signInWithEmailAndPassword(loginData, passwordData)

        authTask.addOnCompleteListener(this) { resultado ->

            if(resultado.isSuccessful){

                Toast.makeText(this, "LOGIN EXITOSO", Toast.LENGTH_SHORT).show()
            } else {

                Log.wtf("FIREBASE", "ERROR: ${resultado.exception?.message}")
            }
        }
    }

    fun logout(view : View?) {

        Firebase.auth.signOut()
        Toast.makeText(this, "LOG OUT EXITOSO", Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {

        super.onStart()
        // recomendación -
        // verificar validez de usuario en onstart
        // para asegurarse que siga siendo útil
        verificarUsuario()
    }

    fun verificarUsuario() {

        if(Firebase.auth.currentUser == null){

            Toast.makeText(this, "SIN USUARIO", Toast.LENGTH_SHORT).show()
        } else {

            Toast.makeText(this, Firebase.auth.currentUser?.email, Toast.LENGTH_SHORT).show()
        }
    }

    fun verificarUsuarioGUI(view : View?){

        verificarUsuario()
    }
}