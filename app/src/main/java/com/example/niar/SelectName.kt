package com.example.niar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout

class SelectName : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.selectname)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        WebSocketManager.connect("ws://10.0.2.2:3000")

        var confirm_name = findViewById<Button>(R.id.confirm_name)
        var input_name = findViewById<EditText>(R.id.name)

        confirm_name.setOnClickListener(){
            var name = input_name.text.toString()
            if(name == null){
                //ništa
            }
            else {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("USER_NAME", name)
                val jsonString = "{\"type\":\"select_name\",\"name\":\"$name\"}"
                WebSocketManager.send(jsonString)
                startActivity(intent)
            }
        }
    }
}