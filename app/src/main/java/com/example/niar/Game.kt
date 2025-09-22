package com.example.niar

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text
import java.util.LinkedList
import kotlin.random.Random

class Game : AppCompatActivity(){

    private lateinit var brojevi: LinkedList<Int>;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.game)

        var thrown_card = findViewById<TextView>(R.id.thrown_card)
        val throw_card = findViewById<Button>(R.id.throw_card)
        var cards_in_hand = findViewById<TextView>(R.id.cards_in_hand)
        var level = findViewById<TextView>(R.id.level)
        var lives = findViewById<TextView>(R.id.lives_remaining)

        WebSocketManager.yourCardsLiveData.observe(this){ cards ->
            cards_in_hand.text = cards.joinToString(", ")
            throw_card.isEnabled = cards.isNotEmpty()
        }

        throw_card.setOnClickListener{
            WebSocketManager.throw_card()
        }

        WebSocketManager.thrownCardLiveData.observe(this){
                card -> thrown_card.text = card.toString()
        }

        WebSocketManager.thrownCardIsWrongLiveData.observe(this) { array ->
            var thrownCard = array[0].first
            var playerWhoThrewWrongCard = array[0].second
            var shouldBeThrownCard = array[1].first
            var playerWhoHadCorrectCardInHand = array[1].second

            var dialog = AlertDialog.Builder(this)
                .setTitle("Wrong card thrown!")
                .setMessage(
                    playerWhoThrewWrongCard + " threw " + thrownCard + "\n" +
                            playerWhoHadCorrectCardInHand + " had " + shouldBeThrownCard
                )
                .setCancelable(false)
                .create()

            dialog.show()

            dialog.window?.decorView?.postDelayed({
                dialog.dismiss()
            }, 3000)
        }

        WebSocketManager.roundGeneratorLiveData.observe(this){triple ->
            val (cardsListTriple, levelTriple, livesTriple) = triple
            cards_in_hand.text = cardsListTriple.joinToString(", ")
            thrown_card.text = "Throw the first card!"
            level.text = "Level " + levelTriple.toString()
            lives.text = "Lives " + livesTriple.toString()

            throw_card.isEnabled = cardsListTriple.isNotEmpty()
        }

        WebSocketManager.levelLiveData.observe(this){ level ->
            val dialog = AlertDialog.Builder(this)
                .setTitle("Level passed!")
                .setMessage(
                    "Congratulations, you passed level " + level
                )
                .setCancelable(false)
                .create()

            dialog.show()

            dialog.window?.decorView?.postDelayed({
                dialog.dismiss()
            }, 3000)
        }

    }

}