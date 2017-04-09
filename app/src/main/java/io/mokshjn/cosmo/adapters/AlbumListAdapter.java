package io.mokshjn.cosmo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.helpers.AlbumArtCache;
import io.mokshjn.cosmo.models.Album;
import io.mokshjn.cosmo.utils.LibUtils;

/**
 * Created by moksh on 1/2/17.
 */

public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    private ArrayList<Album> albumList;
    private Context context;
    private albClickListener clickListener;

    public AlbumListAdapter(ArrayList<Album> albumList, Context context) {
        this.context = context;
        this.albumList = albumList;
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
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Album alb = albumList.get(position);
        if(alb != null) {
            holder.tvAlbumTitle.setText(alb.getAlbumTitle());
            holder.tvArtist.setText(alb.getArtist());
            final String sArtworkUri = LibUtils.getMediaStoreAlbumCoverUri(alb.getAlbumId()).toString();
//            Glide.with(context)
//                    .loadFromMediaStore(ContentUris.withAppendedId(sArtworkUri, alb.getAlbumId()))
//                    .crossFade()
//                    .into(holder.ivAlbumArt);
            AlbumArtCache cache = AlbumArtCache.getInstance();
            final Bitmap art = cache.getBigImage(sArtworkUri);
            if (art != null) {
                holder.ivAlbumArt.setImageBitmap(art);
            } else {
                cache.fetch(context, sArtworkUri, new AlbumArtCache.FetchListener() {
                    @Override
                    public void onFetched(String artUrl, Bitmap bigImage, Bitmap iconImage) {
                        if (artUrl.equals(sArtworkUri)) {
                            holder.ivAlbumArt.setImageBitmap(bigImage);
                        }
                    }
                });
            }

        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return albumList.get(position).getAlbumTitle().substring(0, 1);
    }

    public interface albClickListener {
        void onAlbumClick(View v, int pos);
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
}
