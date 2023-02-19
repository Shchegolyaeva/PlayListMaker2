package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TrackAdapter(): RecyclerView.Adapter<TrackAdapter.TrackViewHolder>() {

 //  companion object {
    var trackList = ArrayList<Track>()
  //  (listOf(
 //       Track("Smells Like Teen Spirit", "Nirvana", "5:01", "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"),
  //      Track("Billie Jean", "Michael Jackson", "4:35", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"),
  //      Track("Stayin' Alive", "Bee Gees", "4:10", "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg"),
  //      Track("Whole Lotta Love", "Led Zeppelin", "5:33", "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"),
  //      Track("Sweet Child O'Mine", "Guns N' Roses", "5:03", "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg")
  //  ))
 //  }

    class TrackViewHolder(item: ViewGroup ): RecyclerView.ViewHolder(
        LayoutInflater.from(item.context).inflate(R.layout.activity_view_recycler_element, item,false)) {

        private val imageTrack = itemView.findViewById<ImageView>(R.id.image_view_playlist)
        private val nameTrack = itemView.findViewById<TextView>(R.id.text_view_track_name)
        private val nameArtists = itemView.findViewById<TextView>(R.id.text_view_artist_name)
        private val timeTrack = itemView.findViewById<TextView>(R.id.text_view_track_time)

        fun bind(model: Track) {
            Glide.with(itemView.context)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.ic_circular)
                .centerCrop()
                .transform(RoundedCorners(10))
                .into(imageTrack)
            nameTrack.text = model.trackName
            nameArtists.text = model.artistName
            timeTrack.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
    }

    override fun getItemCount(): Int {
        return trackList.size

    }
}
