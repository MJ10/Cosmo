package io.mokshjn.cosmo.adapters;

import android.content.Context;
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
import io.mokshjn.cosmo.models.Song;

public class SongListAdapter extends RecyclerView.Adapter<SongListAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter {
    private ArrayList<Song> songList;

    private ClickListener clickListener;

    public SongListAdapter(ArrayList<Song> songList) {
        this.songList = songList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.song_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.tvTitle.setText(song.getTitle());
        holder.tvSongArtist.setText(song.getArtist());
    }

    @Override
    public int getItemCount() {
        return songList.size();
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return songList.get(position).getTitle().substring(0 ,1);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvTitle, tvSongArtist;
        CardView cvSong;

        ViewHolder(View itemView) {
            super(itemView);

            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvSongArtist = (TextView) itemView.findViewById(R.id.tvSongArtist);
            cvSong = (CardView) itemView.findViewById(R.id.cvSong);

            cvSong.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(clickListener != null) {
                clickListener.onSongClick(view, getLayoutPosition());
            }
        }
    }

    public interface ClickListener  {
        void onSongClick(View view, int id);
    }
}
