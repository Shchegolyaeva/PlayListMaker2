package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.switchmaterial.SwitchMaterial


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val switchTheme = findViewById<Switch>(R.id.switch_theme)
        switchTheme.setOnCheckedChangeListener { v, isChecked ->
            AppCompatDelegate.setDefaultNightMode(if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        }

        val shareTextView = findViewById<TextView>(R.id.share_app)
        shareTextView.setOnClickListener {
            Intent().apply {
                setAction(Intent.ACTION_SEND)
                putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.url_course_android_dev)
                )
                setType("text/plain")
                startActivity(this)
            }

        }
        val supportTextView = findViewById<TextView>(R.id.support)
        supportTextView.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "plain/text"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email_developer)))
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.subjectForDevelopers)
                )
                putExtra(
                    Intent.EXTRA_TEXT,
                    getString(R.string.thanksForDevelopers)
                )
                startActivity(Intent.createChooser(this, ""))
            }
        }

        val agreeTextView = findViewById<TextView>(R.id.agree)
        agreeTextView.setOnClickListener {
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.url_practicum_offer))
                startActivity(this)
            }
        }

        val backImageView = findViewById<ImageView>(R.id.back)
        backImageView.setOnClickListener {
            finish()
        }


    }

}