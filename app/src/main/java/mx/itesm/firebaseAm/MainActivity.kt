package mx.itesm.firebaseAm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import mx.itesm.firebaseAm.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // view binding - asociar los elementos de una vista con un objeto
        // de kotlin para agilizar acceso y reducir errores

        // siguiente paso - data binding
        // asociar datos específicos de mi actividad con un elemento específico
        // De la vista
    }

    fun registro(view : View?){

        var loginData = binding.login.text.toString()
        var passwordData = binding.password.text.toString()

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

        var loginData = binding.login.text.toString()
        var passwordData = binding.password.text.toString()

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

    fun registrarDatos(view : View?){

        // guardar nueva info en db remota
        // la info se guarda por medio de hashmaps
        // firestore es no-relacional
        // estructura es por documentos similar a JSON

        // hashmap

        val perrito = hashMapOf(
            "nombre" to binding.nombre.text.toString(),
            "edad" to binding.edad.text.toString()
        )

        // obtenemos referencia a la colección
        val coleccion : CollectionReference =
            Firebase.firestore.collection("perritos")

        // con la referencia a la colección agregamos el dato
        val taskAdd = coleccion.add(perrito)

        // escuchamos al término de la ejecución
        // de guardado
        taskAdd.addOnSuccessListener { documentReference ->

            Toast.makeText(
                this,
                "id: ${documentReference.id}",
                Toast.LENGTH_SHORT
            ).show()

        }.addOnFailureListener{ error ->

            Toast.makeText(
                this,
                "ERROR AL GUARDADO",
                Toast.LENGTH_SHORT
            ).show()

            Log.e("FIRESTORE", "error: $error")
        }
    }

    fun solicitarDatos (view : View?){

        // aquí es el query

        // 1- query "regular"
        // solicitamos datos y obtenemos información
        val coleccion = Firebase.firestore.collection("perritos")

        val queryTask = coleccion.get()

        queryTask.addOnSuccessListener { result ->

            // algo sencillo - recorrer datos
            Toast.makeText(
                this,
                "QUERY EXITOSO",
                Toast.LENGTH_SHORT
            ).show()

            for(documentoActual in result){
                Log.d(
                    "FIRESTORE",
                    "${documentoActual.id} ${documentoActual.data}"
                )
            }
        }.addOnFailureListener{ error ->

            Log.e("FIRESTORE", "error en query: $error")
        }

        // 2- suscribirse a actualizaciones
        coleccion.addSnapshotListener{ datos, e ->

            // esta lógica se va a ejecutar cada vez que
            // haya un cambio en la colección

            if(e != null){

                Log.e("FIRESTORE", "error: $e")

                // return con scope
                // aclara qué parte del código es la que afecta
                // el comando
                return@addSnapshotListener
            }

            Log.e("REALTIME", "ENTRANDO A CAMBIOS")
            // ejemplo igual de sencillo -
            // recorrer datos de colección
            for (cambios in datos!!.documentChanges){

                when(cambios.type){

                    DocumentChange.Type.ADDED ->
                        Log.d(
                            "REALTIME",
                            "agregado: ${cambios.document.data}"
                        )

                    DocumentChange.Type.MODIFIED ->
                        Log.d(
                            "REALTIME",
                            "modificado: ${cambios.document.data}"
                        )

                    DocumentChange.Type.REMOVED ->
                        Log.d(
                            "REALTIME",
                            "removido: ${cambios.document.data}"
                        )
                }
            }
        }
    }
}