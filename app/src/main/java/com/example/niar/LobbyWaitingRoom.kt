package com.example.niar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log
import org.java_websocket.WebSocket
import com.example.niar.WebSocketManager
import org.w3c.dom.Text

class LobbyWaitingRoom: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.lobbywaitingroom)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val name = intent.getStringExtra("USER_NAME")
        var readyCount: Int = 0

        var lobbyId = findViewById<TextView>(R.id.lobbyId)
        val players_in_lobby = findViewById<TextView>(R.id.players_in_lobby)
        var ready = findViewById<Button>(R.id.ready)
        var leave_lobby = findViewById<Button>(R.id.leave_lobby)
        var ready_count = findViewById<TextView>(R.id.ready_count)
        var isReady : Boolean = false

        fun updateLobbyUI() {
            lobbyId.text = "Lobby ID: ${WebSocketManager.lobbyId ?: ""}"
            players_in_lobby.text = WebSocketManager.users_list.joinToString("\n")
            var playerCount = WebSocketManager.users_list.size
            ready_count.text = "${readyCount}/${playerCount}"
        }

        players_in_lobby.text = ""

        updateLobbyUI()

        WebSocketManager.usersLiveData.observe(this){ //neka asinkrona magija on bukv magično sazna kad se usersLiveData updateuje i izvrti ovo
            updateLobbyUI()
            Log.d("DEBUG", "updatean lobbyUI ")
        }

        WebSocketManager.all_players_readyLiveData.observe(this){ readyy ->
            if (readyy == true) {
                isReady = false;
                ready.text = "Ready"
                val intent = Intent(this, Game::class.java)
                startActivity(intent)
            }
        }


        ready.setOnClickListener(){
            if(isReady == false){
                isReady = true
                WebSocketManager.ready()
                ready.text = "Unready"
            }
            else{
                isReady = false;
                WebSocketManager.unready()
                ready.text = "Ready"
            }
        }

        WebSocketManager.readyCountLiveData.observe(this){count ->
            readyCount = count
            updateLobbyUI()
        }

        leave_lobby.setOnClickListener(){
            WebSocketManager.leave_lobby()
            finish()
            Log.d("DEBUG", "kliknut gumb leave_lobby")
        }

        WebSocketManager.leaveLobbyLiveData.observe(this){ bool ->
            if(bool == true){
            val intent = Intent(this@LobbyWaitingRoom, MainActivity::class.java)
            intent.putExtra("USER_NAME", name)
            startActivity(intent)
            }
            Log.d("DEBUG", "leaveLobby ocitan")

        }
    }
}
