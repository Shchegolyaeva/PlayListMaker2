package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val share = findViewById<TextView>(R.id.share_app)
        share.setOnClickListener {
            val intent = Intent()
            intent.setAction(Intent.ACTION_SEND)
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "https://practicum.yandex.ru/profile/android-developer/"
            )
            intent.setType("text/plain")
            startActivity(intent)

        }
        val support = findViewById<TextView>(R.id.support)
        support.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "plain/text"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("shchegolyaeva32@yandex.ru"))
            intent.putExtra(
                Intent.EXTRA_SUBJECT,
                "Сообщение разработчикам и разработчицам приложения Playlist Maker"
            )
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Спасибо разработчикам и разработчицам за крутое приложение!"
            )
            startActivity(Intent.createChooser(intent, ""))
        }

        val agree = findViewById<TextView>(R.id.agree)
        agree.setOnClickListener {
            val myWebLink = Intent(Intent.ACTION_VIEW)
            myWebLink.data = Uri.parse("https://yandex.ru/legal/practicum_offer/")
            startActivity(myWebLink)
        }


    }

}