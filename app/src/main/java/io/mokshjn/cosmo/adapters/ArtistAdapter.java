package io.mokshjn.cosmo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import io.mokshjn.cosmo.R;

/**
 * Created by moksh on 25/7/17.
 */

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    private ClickListener clickListener;
    private Context context;
    private ArrayList<MediaBrowserCompat.MediaItem> tracks = new ArrayList<>();

    public ArtistAdapter(Context context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MediaBrowserCompat.MediaItem item = tracks.get(position);
        MediaDescriptionCompat descriptionCompat = item.getDescription();
        holder.tvArtist.setText(descriptionCompat.getTitle());
        holder.tvDiscography.setText(descriptionCompat.getSubtitle());
    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setArtists(ArrayList<MediaBrowserCompat.MediaItem> tracks) {
        this.tracks = tracks;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return tracks.get(position).getDescription().getTitle().toString().substring(0, 1);
    }

    public interface ClickListener {
        void onArtistClick(View v, int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvArtist, tvDiscography;
        private CardView cvSong;

        ViewHolder(View itemView) {
            super(itemView);
            cvSong = (CardView) itemView.findViewById(R.id.cvArtist);

            tvArtist = (TextView) itemView.findViewById(R.id.tvArtistName);
            tvDiscography = (TextView) itemView.findViewById(R.id.tvDiscogDetails);
            cvSong.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) {
                clickListener.onArtistClick(view, getAdapterPosition());
            }
        }
    }
}
