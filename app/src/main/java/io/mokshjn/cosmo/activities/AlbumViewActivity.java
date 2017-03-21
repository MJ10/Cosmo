package io.mokshjn.cosmo.activities;

import android.content.ContentUris;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.adapters.SongListAdapter;
import io.mokshjn.cosmo.loader.LibraryLoader;
import io.mokshjn.cosmo.models.Song;


public class AlbumViewActivity extends AppCompatActivity implements SongListAdapter.ClickListener {

    @BindView(R.id.rvAlbumSongs) RecyclerView rvAlbumSongs;
    @BindView(R.id.ivAlbumArt) ImageView ivAlbumArt;
    private ArrayList<Song> albumSongs;
    private long albumId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_view);
        ButterKnife.bind(this);

        albumId = getIntent().getLongExtra("albumId", -1);

        initializeAlbumSongList();
        initializeRecycler();
        initializeViews();

    }

    private void initializeViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.avToolbar);
        setSupportActionBar(toolbar);
        setTitle(albumSongs.get(0).getAlbum());
        toolbar.setSubtitle(albumSongs.get(0).getArtist());

        Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

        Glide.with(this)
                .loadFromMediaStore(ContentUris.withAppendedId(sArtworkUri, albumId))
                .crossFade()
                .into(ivAlbumArt);

    }

    private void initializeAlbumSongList() {
        albumSongs = new LibraryLoader(getContentResolver()).getSongsByAlbumId(albumId);
    }

    private void initializeRecycler() {
        rvAlbumSongs.setLayoutManager(new LinearLayoutManager(this));
        SongListAdapter adapter = new SongListAdapter(albumSongs);
        rvAlbumSongs.setAdapter(adapter);
        adapter.setClickListener(this);
        rvAlbumSongs.setNestedScrollingEnabled(false);
        rvAlbumSongs.setHasFixedSize(true);
    }

    @Override
    public void onSongClick(View view, int id) {
        Log.d("clock", "onSongClick: ");
        playSong(id);
    }

    private void playSong(int id) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
