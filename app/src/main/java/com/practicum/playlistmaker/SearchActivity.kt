package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ViewUtils.hideKeyboard
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity() {

    private lateinit var linearNothingFound: LinearLayout
    private lateinit var linearNoInternet: LinearLayout
    private lateinit var updateButton: Button
    private var lastRequest: String = ""
    private lateinit var recycler: RecyclerView


    private val itunesBaseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ApiForItunes::class.java)

    private var trackList = ArrayList<Track>()
    private var adapter = TrackAdapter()

    private lateinit var inputEditText: EditText
    private lateinit var placeholderMessage: TextView

   var inputSaveText: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        linearNothingFound = findViewById(R.id.error_block_nothing_found)
        linearNoInternet = findViewById(R.id.error_block_setting)
        updateButton= findViewById(R.id.button_update)

        adapter.trackList = trackList

        placeholderMessage = findViewById(R.id.placeholderMessage)
        inputEditText = findViewById(R.id.search_content)

        recycler = findViewById<RecyclerView>(R.id.recyclerView)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        val inputEditText = findViewById<EditText>(R.id.search_content)
        inputSaveText = inputEditText.text.toString()
        val clearButton = findViewById<Button>(R.id.exit)
        val reternItemImageView = findViewById<ImageView>(R.id.return_n)

        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(lastText = inputEditText.text.toString())
                lastRequest = inputEditText.text.toString()
                true
            }
            false
        }

        updateButton.setOnClickListener {
            search(lastRequest)
        }


        reternItemImageView.setOnClickListener {
            finish()
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard(currentFocus ?: View(this))
            trackList.clear()
            adapter.notifyDataSetChanged()
            linearNothingFound.visibility = View.GONE
            linearNoInternet.visibility = View.GONE


        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                inputSaveText = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        }
        inputEditText.addTextChangedListener(simpleTextWatcher)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    companion object {
        const val PRODUCT_AMOUNT = "PRODUCT_AMOUNT"
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(PRODUCT_AMOUNT, inputSaveText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val inputEditText = findViewById<EditText>(R.id.search_content)
        val text = savedInstanceState.getString(PRODUCT_AMOUNT)
        if (!text.isNullOrEmpty()) {
            inputEditText.setText(text)

        }
    }

    private fun search(lastText: String) {
        itunesService.search(lastText)
            .enqueue(object : Callback<ItunesResponse> {
                override fun onResponse(call: Call<ItunesResponse>,
                                        response: Response<ItunesResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                trackList.clear()
                                trackList.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                                recycler.visibility = View.VISIBLE
                                linearNothingFound.visibility = View.GONE
                                linearNoInternet.visibility = View.GONE

                            } else {
                                linearNothingFound.visibility = View.VISIBLE
                                linearNoInternet.visibility = View.GONE
                                recycler.visibility = View.GONE
                            }

                        }
                        else -> {
                            linearNoInternet.visibility = View.VISIBLE

                            linearNothingFound.visibility = View.GONE
                            recycler.visibility = View.GONE
                        }

                    }

                }

                override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                    linearNoInternet.visibility = View.VISIBLE
                    linearNothingFound.visibility = View.GONE
                    recycler.visibility = View.GONE

                }

            })
    }
}
