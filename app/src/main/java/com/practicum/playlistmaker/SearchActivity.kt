package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.google.gson.Gson
import com.practicum.playlistmaker.audioplayer.domain.Track
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class SearchActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var linearNothingFound: LinearLayout
    private lateinit var linearNoInternet: LinearLayout

    private lateinit var historySearchGroup: ConstraintLayout

    private lateinit var updateButton: Button
    private lateinit var clearButton: Button
    private lateinit var cleanHistoryButton: Button

    private lateinit var recycler: RecyclerView
    private lateinit var recyclerViewHistory: RecyclerView

    private lateinit var returnItemImageView: ImageView

    private lateinit var inputEditText: EditText
    private lateinit var placeholderMessage: TextView

    private lateinit var progressBar: ProgressBar

    private var lastRequest: String = ""
    var inputSaveText: String = ""
    private val itunesBaseUrl = "https://itunes.apple.com"

    private var isClickAllowed = true
    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable { search(inputEditText.text.toString()) }


    private val retrofit = Retrofit.Builder()
        .baseUrl(itunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val itunesService = retrofit.create(ApiForItunes::class.java)

    private var trackList = ArrayList<Track>()
    private var adapter = TrackAdapter()
    private val historyAdapter = TrackAdapter()

    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var searchHistory: SearchHistory

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        sharedPrefs = getSharedPreferences(HISTORY_SEARCH, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPrefs)

        findItems()
        recyclerSetting()
        addChangeListeners()
        addButtonListeners()

        sharedPrefs.registerOnSharedPreferenceChangeListener(this)

        // Кнопка очищения истории поиска
        cleanHistoryButton.setOnClickListener {
            sharedPrefs.edit().clear().apply()
            historySearchGroup.isVisible = false
        }

        // Реагирует на смену фокуса в EditText
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            historySearchGroup.isVisible = hasFocus && inputEditText.text.isEmpty() &&
                searchHistory.get().isNotEmpty()
        }

        // Реагирует на ввод текста в EditText и через 2 сек производит поиск
        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                historySearchGroup.visibility = if (inputEditText.hasFocus() && p0?.isEmpty() == true
                    && searchHistory.get().isNotEmpty() && !progressBar.isVisible) View.VISIBLE else View.GONE
                recycler.visibility = if (inputEditText.hasFocus() && p0?.isEmpty() == true) {
                    trackList.clear()
                    adapter.notifyDataSetChanged()
                    View.GONE
                } else {
                    if (linearNoInternet.isVisible || linearNothingFound.isVisible || progressBar.isVisible) {
                        View.GONE
                    } else View.VISIBLE
                }
                if (inputEditText.text.isNotEmpty()) {
                    searchDebounce()
                }
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        })

        adapter.trackList = trackList
        historyAdapter.trackList = searchHistory.get().toCollection(ArrayList())

        inputSaveText = inputEditText.text.toString()

        // Реагирует на нажатие песни в поиске
        adapter.itemClickListener = { _, track ->
            searchHistory.add(track)
            if (clickDebounce()) {
                putGsonForAudioPlayerActivity(track)
            }
        }

        historyAdapter.itemClickListener = { _, track ->
            putGsonForAudioPlayerActivity(track)
        }
    }


    private fun putGsonForAudioPlayerActivity(track: Track) {
        val intent = Intent(this, AudioPlayerActivity::class.java).apply {
            putExtra(AudioPlayerActivity.TRACK_OBJECT, Gson().toJson(track))
        }
        startActivity(intent)
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
        const val HISTORY_SEARCH = "HISTORY_SEARCH"
        const val KEY_LIST_TRACKS = "tracks_history"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
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
        progressBar.visibility = View.VISIBLE
        linearNoInternet.isVisible = false
        linearNothingFound.isVisible = false
        recycler.isVisible = false

        itunesService.search(lastText)
            .enqueue(object : Callback<ItunesResponse> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<ItunesResponse>,
                                        response: Response<ItunesResponse>
                ) {
                    progressBar.visibility = View.GONE
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                trackList.clear()
                                trackList.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                                recycler.isVisible = true
                                linearNothingFound.isVisible = false
                                linearNoInternet.isVisible = false
                                progressBar.isVisible = false
                            } else {
                                linearNothingFound.isVisible = true
                                linearNoInternet.isVisible = false
                                recycler.isVisible = false
                                historySearchGroup.isVisible = false
                                progressBar.isVisible = false
                            }
                        }
                        else -> {
                            linearNoInternet.isVisible = true
                            linearNothingFound.isVisible = false
                            recycler.isVisible = false
                            historySearchGroup.isVisible = false
                            progressBar.isVisible = false
                        }
                    }
                }

                override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    linearNoInternet.visibility = View.VISIBLE
                    linearNothingFound.visibility = View.GONE
                    recycler.visibility = View.GONE
                }
            })
    }

    @SuppressLint("RestrictedApi", "NotifyDataSetChanged")
    private fun addButtonListeners() {
        // Кнопка обновить запрос, когда нет сети
        updateButton.setOnClickListener {
            search(lastRequest)
        }
        // Кнопка назад
        returnItemImageView.setOnClickListener {
            finish()
        }
        // Кнопка очистить поле ввода
        clearButton.setOnClickListener {
            inputEditText.setText("")
            hideKeyboard(currentFocus ?: View(this))
            trackList.clear()
            adapter.notifyDataSetChanged()
            linearNothingFound.isVisible = false
            linearNoInternet.isVisible = false

        }
    }

    private fun addChangeListeners() {
        // Реагирует на нажатие кнопки на клавиатуре и выполняет поиск
        inputEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search(lastText = inputEditText.text.toString())
                lastRequest = inputEditText.text.toString()
                true
            }
            false
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
        // Реагирует на ввод текста в EditText для отображения кнопок
        inputEditText.addTextChangedListener(simpleTextWatcher)
    }

    private fun findItems() {
        linearNothingFound = findViewById(R.id.error_block_nothing_found)
        linearNoInternet = findViewById(R.id.error_block_setting)
        clearButton = findViewById(R.id.exit)
        updateButton= findViewById(R.id.button_update)
        returnItemImageView = findViewById(R.id.return_n)
        cleanHistoryButton = findViewById(R.id.clean_history_button)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        inputEditText = findViewById(R.id.search_content)
        historySearchGroup = findViewById(R.id.history_search_group)
        recycler = findViewById(R.id.recyclerView)
        recyclerViewHistory = findViewById(R.id.recycler_view_history)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun recyclerSetting() {
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter
        recyclerViewHistory.layoutManager = LinearLayoutManager(this)
        recyclerViewHistory.adapter = historyAdapter
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == KEY_LIST_TRACKS) {
            historyAdapter.trackList = searchHistory.get().toCollection(ArrayList())
            historyAdapter.notifyDataSetChanged()
        }
    }
    // задерживает кликабельность на песню из списка на одну секунду
    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }
    //функция выполняющая отложенный запрос поиска через 2 сек
    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }
}
