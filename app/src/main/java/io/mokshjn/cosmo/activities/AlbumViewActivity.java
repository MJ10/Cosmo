package io.mokshjn.cosmo.activities;

import android.app.Service;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.adapters.SongListAdapter;
import io.mokshjn.cosmo.fragments.SongListFragment;
import io.mokshjn.cosmo.loader.LibraryLoader;
import io.mokshjn.cosmo.models.Song;
import io.mokshjn.cosmo.services.MusicService;
import io.mokshjn.cosmo.utils.StorageUtils;

public class AlbumViewActivity extends AppCompatActivity implements SongListAdapter.ClickListener {

    public static final String Broadcast_PLAY_NEW_AUDIO = "io.mokshjn.cosmo.PlayNewAudio";
    private RecyclerView rvAlbumSongs;
    private ImageView ivAlbumArt;
    private ArrayList<Song> albumSongs;
    private long albumId;
    private MusicService service;
    private boolean musicBound = false;

    private ServiceConnection musicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) iBinder;
            service = binder.getService();
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicBound = false;
        }
    };

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
        if(!musicBound) {
            StorageUtils storage = new StorageUtils(this);
            storage.storeSong(albumSongs);
            storage.storeAudioIndex(id);

            Intent playerIntent = new Intent(this, io.mokshjn.cosmo.services.MusicService.class);
            startService(playerIntent);
            bindService(playerIntent, musicServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            StorageUtils storage = new StorageUtils(this);
            storage.storeAudioIndex(id);

            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(broadcastIntent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(musicBound) {
            service.stopSong();
            unbindService(musicServiceConnection);
            service.stopSelf();
        }
    }
}
