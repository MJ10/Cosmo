package io.mokshjn.cosmo.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import io.mokshjn.cosmo.R;

import static android.support.v4.media.MediaBrowserCompat.MediaItem;

/**
 * Created by moksh on 15/6/17.
 */

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

    private ArrayList<MediaItem> albumList = new ArrayList<>();
    private Context context;
    private albClickListener clickListener;

    public AlbumAdapter(Context context) {
        this.context = context;
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
        MediaItem alb = albumList.get(position);
        if (alb != null) {
            holder.tvAlbumTitle.setText(alb.getDescription().getTitle());
            holder.tvArtist.setText(alb.getDescription().getSubtitle());
            Glide.with(context)
                    .load(alb.getDescription().getIconUri())
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .crossFade()
                    .into(new ImageViewTarget<GlideDrawable>(holder.ivAlbumArt) {
                        @Override
                        protected void setResource(GlideDrawable resource) {
                            holder.ivAlbumArt.setImageDrawable(resource.getCurrent());
                            Palette palette = Palette.from(((GlideBitmapDrawable) resource.getCurrent()).getBitmap()).generate();
                            Palette.Swatch vibrant = palette.getVibrantSwatch();
                            if (vibrant != null) {
                                holder.cvAlbum.setBackgroundColor(vibrant.getRgb());
                                holder.tvAlbumTitle.setTextColor(vibrant.getTitleTextColor());
                                holder.tvArtist.setTextColor(vibrant.getBodyTextColor());
                            } else {
                                Palette.Swatch muted = palette.getDarkVibrantSwatch();
                                if (muted != null) {
                                    holder.cvAlbum.setBackgroundColor(muted.getRgb());
                                    holder.tvAlbumTitle.setTextColor(muted.getTitleTextColor());
                                    holder.tvArtist.setTextColor(muted.getBodyTextColor());
                                }
                            }
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    public void setAlbums(ArrayList<MediaItem> albums) {
        this.albumList = albums;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return albumList.get(position).getDescription().getTitle().toString().substring(0, 1);
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
            if (clickListener != null) {
                clickListener.onAlbumClick(view, getLayoutPosition());
            }
        }
    }
}
