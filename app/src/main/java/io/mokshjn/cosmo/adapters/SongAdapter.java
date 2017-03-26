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
 * Created by moksh on 19/3/17.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {

    private ClickListener clickListener;
    private ArrayList<MediaBrowserCompat.MediaItem> tracks = new ArrayList<>();

    public SongAdapter(ArrayList<MediaBrowserCompat.MediaItem> tracks) {
        this.tracks = tracks;
    }

    public SongAdapter() {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MediaBrowserCompat.MediaItem item = tracks.get(position);
        MediaDescriptionCompat descriptionCompat = item.getDescription();

        holder.tvSongTitle.setText(descriptionCompat.getTitle());
        holder.tvSongArtist.setText(descriptionCompat.getSubtitle());
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setTracks(ArrayList<MediaBrowserCompat.MediaItem> tracks) {
        this.tracks = tracks;
    }

    public interface ClickListener {
        void onSongClick(View v, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvSongTitle, tvSongArtist;
        private CardView cvSong;

        public ViewHolder(View itemView) {
            super(itemView);
            cvSong = (CardView) itemView.findViewById(R.id.cvSong);

            tvSongArtist = (TextView) itemView.findViewById(R.id.tvSongArtist);
            tvSongTitle = (TextView) itemView.findViewById(R.id.tvTitle);

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
