package io.mokshjn.cosmo.activities;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.adapters.SongListAdapter;
import io.mokshjn.cosmo.loader.LibraryLoader;
import io.mokshjn.cosmo.models.Song;

public class AlbumViewActivity extends AppCompatActivity implements SongListAdapter.ClickListener {
    private RecyclerView rvAlbumSongs;
    private ImageView ivAlbumArt;
    private ArrayList<Song> albumSongs;
    private long albumId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_view);

        albumId = getIntent().getLongExtra("albumId", -1);

        initializeAlbumSongList();
        initializeRecycler();
        initializeViews();

    }

    private void initializeViews() {
        ivAlbumArt = (ImageView) findViewById(R.id.ivAlbumArt);

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
        rvAlbumSongs = (RecyclerView) findViewById(R.id.rvAlbumSongs);
        rvAlbumSongs.setLayoutManager(new LinearLayoutManager(this));
        SongListAdapter adapter = new SongListAdapter(albumSongs);
        rvAlbumSongs.setAdapter(adapter);
        rvAlbumSongs.setNestedScrollingEnabled(false);
    }

    @Override
    public void onSongClick(View view, int id) {
        
    }
}
