package io.mokshjn.cosmo.activities;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.adapters.SongListAdapter;
import io.mokshjn.cosmo.models.Song;
import io.mokshjn.cosmo.utils.StorageUtils;
import jp.wasabeef.blurry.Blurry;

public class AlbumViewActivity extends AppCompatActivity {
    private RecyclerView rvAlbumSongs;
    private TextView tvAlbumTitle, tvAlbumArtist;
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
        albumSongs = new ArrayList<>();
        String selection = "is_music != 0 and album_id = " + albumId;
        String[] projection = {
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ALBUM_ID
        };
        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";

        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri, projection, selection, null, null );
        if(cursor != null && cursor.moveToFirst()){
            do {
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                long albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                Song song = new Song(id, title, artist,albumId, album);
                albumSongs.add(song);
            } while (cursor.moveToNext());
        }
    }

    private void initializeRecycler() {
        rvAlbumSongs = (RecyclerView) findViewById(R.id.rvAlbumSongs);
        rvAlbumSongs.setLayoutManager(new LinearLayoutManager(this));
        SongListAdapter adapter = new SongListAdapter(albumSongs);
        rvAlbumSongs.setAdapter(adapter);
        rvAlbumSongs.setNestedScrollingEnabled(false);
        
    }
}
