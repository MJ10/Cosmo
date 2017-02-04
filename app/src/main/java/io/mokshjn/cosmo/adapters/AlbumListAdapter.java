package io.mokshjn.cosmo.adapters;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.models.Album;

/**
 * Created by moksh on 1/2/17.
 */

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.ViewHolder> {

    private ArrayList<Album> albumList;
    private Context context;
    private albClickListener clickListener;

    public AlbumListAdapter(ArrayList<Album> albumList, Context context) {
        this.context = context;
        this.albumList = albumList;
        Log.d("Adapter", "AlbumListAdapter: " + albumList.size());
    }

    public void setClickListener(albClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.album_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Album alb = albumList.get(position);
        if(alb != null) {
            holder.tvAlbumTitle.setText(alb.getAlbumTitle());
            holder.tvArtist.setText(alb.getArtist());
            Uri sArtworkUri = Uri
                    .parse("content://media/external/audio/albumart");
            Glide.with(context)
                    .loadFromMediaStore(ContentUris.withAppendedId(sArtworkUri, alb.getAlbumId()))
                    .crossFade()
                    .into(holder.ivAlbumArt);
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvAlbumTitle, tvArtist;
        private CardView cvAlbum;
        private ImageView ivAlbumArt;

        ViewHolder(View itemView) {
            super(itemView);

            tvAlbumTitle = (TextView) itemView.findViewById(R.id.tvAlbumCardTitle);
            tvArtist = (TextView) itemView.findViewById(R.id.tvAlbumCardArtist);
            ivAlbumArt = (ImageView) itemView.findViewById(R.id.ivAlbumArt);

            cvAlbum = (CardView) itemView.findViewById(R.id.cvAlbum);

            cvAlbum.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if(clickListener != null){
                clickListener.onAlbumClick(view, getLayoutPosition());
            }
        }
    }

    public interface albClickListener{
        void onAlbumClick(View v, int pos);
    }
}
