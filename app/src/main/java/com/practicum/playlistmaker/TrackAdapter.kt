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

    var trackList = ArrayList<Track>()
    var itemClickListener: ((Int, Track) -> Unit)? = null

    class TrackViewHolder(item: ViewGroup ): RecyclerView.ViewHolder(
        LayoutInflater.from(item.context).inflate(R.layout.activity_view_recycler_element, item,false)) {

        private val imageTrack = itemView.findViewById<ImageView>(R.id.image_view_playlist)
        private val nameTrack = itemView.findViewById<TextView>(R.id.text_view_track_name)
        private val nameArtists = itemView.findViewById<TextView>(R.id.text_view_artist_name)
        private val timeTrack = itemView.findViewById<TextView>(R.id.text_view_track_time)

        fun bind(model: Track) {
            Glide.with(itemView.context)
                .load(model.artworkUrl100)
                .placeholder(R.drawable.ic_vector)
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
        val track = trackList[position]
        holder.bind(track)
        holder.itemView.setOnClickListener {itemClickListener?.invoke(position, track)}
    }

    override fun getItemCount(): Int {
        return trackList.size

    }
}
