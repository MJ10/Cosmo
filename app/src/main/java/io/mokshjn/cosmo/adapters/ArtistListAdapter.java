package io.mokshjn.cosmo.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.models.Artist;

/**
 * Created by moksh on 7/3/17.
 */

public class ArtistListAdapter extends RecyclerView.Adapter<ArtistListAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    private ArrayList<Artist> artistList;

    private artistClickListener clickListener;

    public ArtistListAdapter(ArrayList<Artist> artistList) {
        this.artistList = artistList;
    }

    @Override
    public ArtistListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArtistListAdapter.ViewHolder holder, int position) {
        Artist artist = artistList.get(position);

        holder.tvArtistName.setText(artist.getName());
        holder.tvDiscogDetails.setText(artist.getSongsNo() + " Songs | " + artist.getAlbumsNo() + " Albums");
    }

    @Override
    public int getItemCount() {
        return artistList.size();
    }

    public void setClickListener(artistClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return artistList.get(position).getName().substring(0,1);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvArtistName, tvDiscogDetails;
        private CardView cvArtist;

        ViewHolder(View itemView) {
            super(itemView);

            cvArtist = (CardView) itemView.findViewById(R.id.cvArtist);

            tvArtistName = (TextView) itemView.findViewById(R.id.tvArtistName);
            tvDiscogDetails = (TextView) itemView.findViewById(R.id.tvDiscogDetails);

            cvArtist.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(clickListener != null){
                clickListener.onArtistClick(view, getLayoutPosition());
            }
        }
    }
    public interface artistClickListener{
        void  onArtistClick(View v, int p);
    }
}
