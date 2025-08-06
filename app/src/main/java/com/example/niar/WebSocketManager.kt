package com.example.niar

import android.content.Context
import android.widget.Toast
import androidx.annotation.IntegerRes
import androidx.lifecycle.MutableLiveData
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONException
import org.json.JSONObject
import java.net.URI
import java.util.LinkedList

object WebSocketManager {
    private var client: WebSocketClient? = null
    var lobbyId: String? = null
    public var users_list : LinkedList<String> = LinkedList()
    public val usersLiveData = MutableLiveData<List<String>>()
    public var readyCountLiveData = MutableLiveData<Int>()
    public var all_players_ready : Boolean = false
    public var all_players_readyLiveData = MutableLiveData<Boolean>()
    public var roundGeneratorLiveData = MutableLiveData<Triple<List<Int>, Int, Int>>()
    public val yourCardsLiveData = MutableLiveData<List<Int>>()
    public var thrownCard : Int? = null
    public val thrownCardLiveData = MutableLiveData<Int>()
    public val thrownCardIsWrongLiveData = MutableLiveData<LinkedList<Pair<Int, String>>>()
    public var levelPassed : Boolean = false
    public val levelPassedLiveData = MutableLiveData<Boolean>()
    public var level : Int = 1
    public val levelLiveData = MutableLiveData<Int>()
    public val doesLobbyExistLiveData = MutableLiveData<Boolean>()
    public val leaveLobbyLiveData = MutableLiveData<Boolean>()

    fun connect(serverUrl: String) {
        if (client == null || client?.isOpen == false) {
            client = object : WebSocketClient(URI(serverUrl)) {
                override fun onOpen(handshakedata: ServerHandshake?) {
                    println("Connected to server")

                }

                override fun onMessage(message: String?) {
                    try {
                        val json = JSONObject(message)
                        val type = json.getString("type")
                        if (type == "lobby_created"){
                            lobbyId = json.getString("lobbyId")
                            users_list.clear()
                            users_list.add(json.getString("name"))
                            usersLiveData.postValue(users_list)
                        }
                        else if(type == "user_joined_lobby"){
                            var users_in_lobby = json.getJSONArray("users_in_lobby")
                            users_list.clear()

                            for(i in 0 until users_in_lobby.length()){
                                val name = users_in_lobby.getString(i)
                                users_list.add(name)
                            }
                            usersLiveData.postValue(users_list)
                            lobbyId = json.getString("lobbyId")
                            doesLobbyExistLiveData.postValue(true)
                        }

                        else if(type == "user_left_lobby"){
                            var users_in_lobby = json.getJSONArray("users_in_lobby")
                            users_list.clear()
                            for(i in 0 until users_in_lobby.length()){
                                val name = users_in_lobby.getString(i)
                                users_list.add(name)
                            }
                            lobbyId = json.getString("lobbyId")
                            usersLiveData.postValue(users_list)
                        }

                        else if(type == "you_left_lobby"){
                            leaveLobbyLiveData.postValue(true)
                        }

                        else if(type == "lobby_does_not_exist"){
                            doesLobbyExistLiveData.postValue(false)
                        }

                        else if(type == "someone_clicked_ready"){
                            var ready_count = json.getInt("ready_count")
                            readyCountLiveData.postValue(ready_count)
                        }

                        else if(type == "someone_clicked_unready"){
                            var ready_count = json.getInt("ready_count")
                            readyCountLiveData.postValue(ready_count)
                        }

                        else if(type == "all_players_ready"){
                            all_players_ready = true
                            all_players_readyLiveData.postValue(true)
                        }

                        else if(type == "round_generated"){
                            var your_cards_array = json.getJSONArray("cards")
                            var level = json.getInt("level")
                            var lives = json.getInt("lives")
                            val cardsList = mutableListOf<Int>()


                            for(i in 0 until your_cards_array.length()){
                                cardsList.add(your_cards_array.getInt(i))
                            }
                            roundGeneratorLiveData.postValue(Triple(cardsList, level, lives))
                        }

                        else if(type == "thrown_card_is_correct"){
                            var thrown_card = json.getInt("card")
                            thrownCardLiveData.postValue(thrown_card)

                            var your_cards_array = json.getJSONArray("cards")
                            val cardsList = mutableListOf<Int>()

                            for(i in 0 until your_cards_array.length()){
                                cardsList.add(your_cards_array.getInt(i))
                            }
                            yourCardsLiveData.postValue(cardsList.sorted())
                        }

                        else if(type == "other_player_threw"){
                            var thrown_card = json.getInt("card")
                            thrownCardLiveData.postValue(thrown_card)
                        }

                        else if(type == "thrown_card_is_wrong"){ //ovo je haos
                            var thrownCard = json.getInt("thrownCard")
                            var shouldBeThrownCard = json.getInt("shouldBeThrownCard")
                            var playerWhoThrewWrongCard = json.getString("playerWhoThrewWrongCard")
                            var playerWhoHadCorrectCardInHand = json.getString("playerWhoHadCorrectCardInHand")
                            var lista = LinkedList<Pair<Int, String>>()
                            lista.add(Pair(thrownCard, playerWhoThrewWrongCard))
                            lista.add(Pair(shouldBeThrownCard, playerWhoHadCorrectCardInHand))
                            thrownCardIsWrongLiveData.postValue(lista)
                        }

                        else if(type == "level_passed"){
                            var level = json.getInt("level")
                            levelLiveData.postValue(level)
                        }
                    }

                    catch (e: JSONException){
                        e.printStackTrace()
                    }
                }

                override fun onClose(code: Int, reason: String?, remote: Boolean) {
                    println("Disconnected: $reason")
                }

                override fun onError(ex: Exception?) {
                    println("Error: ${ex?.message}")
                }
            }
            client?.connect()
        }
    }

    fun send(message: String) {
        if (client?.isOpen == true) {
            client?.send(message)
        } else {
            println("nisi spojen kume")
        }
    }

    fun create_lobby(){
        val jsonString = "{\"type\":\"create_lobby\"}" //treba da bude jsonString kad saljemo serveru tako kaže chatgpt da je praksa
        if(client?.isOpen == true){
            client?.send(jsonString)
        }
        else{
            println("nisi spojen kume")
        }
    }

    fun join_lobby(lobbyid : String){
        val jsonString = "{\"type\":\"join_lobby\",\"id\":\"$lobbyid\"}"
        if(client?.isOpen == true){
            client?.send(jsonString)
        }
        else{
            println("nisi spojen kume")
        }
    }

    fun ready(){
        val jsonString = "{\"type\":\"ready\"}"
        if(client?.isOpen == true){
            client?.send(jsonString)
        }
        else{
            println("nisi spojen kume")
        }
    }

    fun unready(){
        val jsonString = "{\"type\":\"unready\"}"
        if(client?.isOpen == true){
            client?.send(jsonString)
        }
        else{
            println("nisi spojen kume")
        }
    }

    fun leave_lobby(){
        val jsonString = "{\"type\":\"leave_lobby\"}"
        if(client?.isOpen == true){
            client?.send(jsonString)
        }
        else{
            println("nisi spojen kume")
        }
    }

    fun throw_card(){
        val jsonString = "{\"type\":\"throw_card\"}"
        if(client?.isOpen == true){
            client?.send(jsonString)
        }
        else{
            println("nisi spojen kume")
        }
    }

    fun close() {
        client?.close()
        client = null
    }
}
