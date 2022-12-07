package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       val image = findViewById<Button>(R.id.button_search)
        image.setOnClickListener {
            val displayIntent = Intent(this, Activity1::class.java)
            startActivity(displayIntent)
        }


    //    val imageClickListener: View.OnClickListener = object : View.OnClickListener {
    //        override fun onClick(v: View?) {
    //            Toast.makeText(this@MainActivity, "Нажали на кнопку!", Toast.LENGTH_SHORT).show()
    //        }
    //    }

    //    image.setOnClickListener(imageClickListener)


        val button = findViewById<Button>(R.id.button_media)
        button.setOnClickListener {
            val displayIntent = Intent(this, Activity2::class.java)
            startActivity(displayIntent)
        }

    //    button.setOnClickListener {

    //        Toast.makeText(this@MainActivity, "Нажали на кнопку!", Toast.LENGTH_SHORT).show()
    //    }

       // val buttonSetting = findViewById<Button>(R.id.button_setting)

       // buttonSetting.setOnClickListener {

       //     Toast.makeText(this@MainActivity, "Нажали на кнопку!", Toast.LENGTH_SHORT).show()
       // }

        val buttonSetting = findViewById<Button>(R.id.button_setting)
        buttonSetting.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }
    }
}