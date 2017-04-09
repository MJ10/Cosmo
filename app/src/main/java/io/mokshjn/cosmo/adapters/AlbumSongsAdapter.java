package io.mokshjn.cosmo.adapters;

import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import io.mokshjn.cosmo.R;

/**
 * Created by moksh on 9/4/17.
 */

public class AlbumSongsAdapter extends RecyclerView.Adapter<AlbumSongsAdapter.ViewHolder> {
    private AlbumSongsAdapter.ClickListener clickListener;
    private ArrayList<MediaBrowserCompat.MediaItem> tracks = new ArrayList<>();

    public AlbumSongsAdapter(ArrayList<MediaBrowserCompat.MediaItem> tracks) {
        this.tracks = tracks;
    }

    public AlbumSongsAdapter() {
    }

    @Override
    public AlbumSongsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_song_card, parent, false);
        return new AlbumSongsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AlbumSongsAdapter.ViewHolder holder, int position) {
        MediaBrowserCompat.MediaItem item = tracks.get(position);
        MediaDescriptionCompat descriptionCompat = item.getDescription();

        holder.tvSongTitle.setText(descriptionCompat.getTitle());
        holder.tvSongArtist.setText(descriptionCompat.getSubtitle());
        holder.tvTrackNumber.setText(String.valueOf(position + 1));
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public void setClickListener(AlbumSongsAdapter.ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setTracks(ArrayList<MediaBrowserCompat.MediaItem> tracks) {
        this.tracks = tracks;
    }

    public interface ClickListener {
        void onSongClick(View v, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvSongTitle, tvSongArtist, tvTrackNumber;
        private CardView cvSong;

        public ViewHolder(View itemView) {
            super(itemView);
            cvSong = (CardView) itemView.findViewById(R.id.cvSong);

            tvSongArtist = (TextView) itemView.findViewById(R.id.tvSongArtist);
            tvSongTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvTrackNumber = (TextView) itemView.findViewById(R.id.tvTrackNumber);
            cvSong.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onSongClick(view, getAdapterPosition());
            }
        }
    }
}
