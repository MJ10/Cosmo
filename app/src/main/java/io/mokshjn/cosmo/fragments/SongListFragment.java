package io.mokshjn.cosmo.fragments;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.adapters.SongListAdapter;
import io.mokshjn.cosmo.loader.LibraryLoader;
import io.mokshjn.cosmo.models.Song;
import io.mokshjn.cosmo.services.MusicService;
import io.mokshjn.cosmo.utils.StorageUtils;

import static android.content.Context.SEARCH_SERVICE;

public class SongListFragment extends Fragment implements SongListAdapter.ClickListener, SearchView.OnQueryTextListener {

    public static final String Broadcast_PLAY_NEW_AUDIO = "io.mokshjn.cosmo.PlayNewAudio";
    private RecyclerView rvSongsList;
    private SongListAdapter songAdapter;
    private LibraryLoader loader;
    private ArrayList<Song> songList;
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

    /*@Override
    public void onStart() {
        super.onStart();
        if(playIntent == null) {
            playIntent = new Intent(getContext(), MusicService.class);
            getActivity().bindService(playIntent, musicServiceConnection, Context.BIND_AUTO_CREATE);
            getActivity().startService(playIntent);
        }

    }*/

    public static SongListFragment newInstance() {
        SongListFragment fragment = new SongListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);
        songList = new ArrayList<>();
        rvSongsList = (RecyclerView) rootView.findViewById(R.id.rvSongList);
        //initSongList();
        loadAudio();
        initalizeRecyclerView();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.search_menu, menu);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        
        searchView.setBackgroundColor(getResources().getColor(R.color.colorTextSecondary));
        searchView.setOnQueryTextListener(this);
    }

    private void initalizeRecyclerView() {

        rvSongsList.setLayoutManager(new LinearLayoutManager(getContext()));
        songAdapter = new SongListAdapter(songList);
        rvSongsList.setAdapter(songAdapter);
        songAdapter.setClickListener(this);
    }

    /*private void initSongList() {
        cr = getActivity().getContentResolver();
        loader = new LibraryLoader(cr);
        songList = loader.getSongList();
    }*/

    @Override
    public void onSongClick(View view, int id) {
        playSong(id);
    }

    private void playSong(int position) {
        if(!musicBound) {
            StorageUtils storage = new StorageUtils(getContext());
            storage.storeSong(songList);
            storage.storeAudioIndex(position);
            Log.d("SongFrag", "playSong: " + "done" + songList.size());

            Intent playerIntent = new Intent(getContext(), io.mokshjn.cosmo.services.MusicService.class);
            getActivity().startService(playerIntent);
            getActivity().bindService(playerIntent, musicServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            StorageUtils storage = new StorageUtils(getContext());
            storage.storeAudioIndex(position);

            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            getActivity().sendBroadcast(broadcastIntent);
        }
    }

    private void loadAudio() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        loader = new LibraryLoader(contentResolver);
        songList = loader.getSongList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(musicBound) {
            service.stopSong();
            getActivity().unbindService(musicServiceConnection);
            service.stopSelf();
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
