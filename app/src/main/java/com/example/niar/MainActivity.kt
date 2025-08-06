package com.example.niar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.java_websocket.WebSocketListener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.mainactivity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        fun toastAlert(context: Context, string: String) {
            Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
        }

        val name = intent.getStringExtra("USER_NAME")
        var ime = findViewById<TextView>(R.id.ime)
        ime.text = "Welcome, " + name;


        var create_lobby = findViewById<Button>(R.id.create_lobby)
        var join_lobby = findViewById<Button>(R.id.join_lobby)
        var join_lobby_id = findViewById<EditText>(R.id.join_lobby_ID)

        //ovo neka magija da dopustam da se u join lobby upisuje samo 4 charactera i to samo brojevi i uppercase slova
        val filter = InputFilter { source, _, _, _, _, _ ->
            val input = source.toString()
            input.uppercase().filter { it.isDigit() || it in 'A'..'Z' }
        }
        join_lobby_id.filters = (arrayOf(filter, InputFilter.LengthFilter(4)))

        create_lobby.setOnClickListener{
            val intent = Intent(this, LobbyWaitingRoom::class.java)
            intent.putExtra("USER_NAME", name)
            WebSocketManager.create_lobby() //zovi fju createlobby iz websocketmanagera ide gass
            startActivity(intent)
        }

        //šaljem zahtjev serveru za joinanje
        join_lobby.setOnClickListener{
            var lobby_id = join_lobby_id.text.toString()
            WebSocketManager.join_lobby(lobby_id)
        }
        //dobivam odgovor od servera da li mogu joinat u obliku doesLobbyExist
        WebSocketManager.doesLobbyExistLiveData.observe(this){ doesLobbyExist ->
            if (!doesLobbyExist) {
                toastAlert(this, "Lobby does not exist") //popup alertic mnogo sladak
            }
            else{
                val intent = Intent(this, LobbyWaitingRoom::class.java)
                startActivity(intent)
            }
        }
    }
}