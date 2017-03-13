package io.mokshjn.cosmo.fragments;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.adapters.SongListAdapter;
import io.mokshjn.cosmo.interfaces.LibraryInterface;
import io.mokshjn.cosmo.loader.SongListLoader;
import io.mokshjn.cosmo.models.Song;
import io.mokshjn.cosmo.services.MusicService;
import io.mokshjn.cosmo.utils.StorageUtils;

public class SongListFragment extends Fragment implements SongListAdapter.ClickListener, LibraryInterface.onLoadSongs, SearchView.OnQueryTextListener{

    public static final String Broadcast_PLAY_NEW_AUDIO = "io.mokshjn.cosmo.PlayNewAudio";
    private FastScrollRecyclerView rvSongsList;
    private SongListAdapter songAdapter;
    private ArrayList<Song> songList, backupList;
    private MusicService service;
    public boolean musicBound = false;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("MUSIC_BOUND_STATE", false);
        super.onSaveInstanceState(outState);
    }

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

        if(savedInstanceState != null){
            musicBound = savedInstanceState.getBoolean("MUSIC_BOUND_STATE");
        }
        songList = new ArrayList<>();
        backupList = new ArrayList<>();
        rvSongsList = (FastScrollRecyclerView) rootView.findViewById(R.id.rvSongList);

        loadAudio();
        initalizeRecyclerView();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        setHasOptionsMenu(true);
    }

//    @Override
//    public void onCreateOptionsMenu(Menu menu, final MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.search_menu, menu);
//        MenuItem searchItem = menu.findItem(R.id.action_search);
//        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView.setOnQueryTextListener(this);
//        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
//            @Override
//            public boolean onMenuItemActionExpand(MenuItem item) {
//                return true;
//            }
//
//            @Override
//            public boolean onMenuItemActionCollapse(MenuItem item) {
//                songList.clear();
//                songList.addAll(backupList);
//                songAdapter.notifyDataSetChanged();
//                return true;
//            }
//        });
//    }

    private void initalizeRecyclerView() {

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayout.VERTICAL);
        rvSongsList.setLayoutManager(manager);
        songAdapter = new SongListAdapter(songList);
        rvSongsList.setAdapter(songAdapter);
        songAdapter.setClickListener(this);
        rvSongsList.setHasFixedSize(true);
    }

    @Override
    public void onSongClick(View view, int id) {
        playSong(id);
    }

    private void playSong(int position) {
        if(!musicBound) {
            StorageUtils storage = new StorageUtils(getContext());
            storage.storeSong(songList);
            storage.storeAudioIndex(position);

            Intent playerIntent = new Intent(getContext(), io.mokshjn.cosmo.services.MusicService.class);
            getActivity().startService(playerIntent);
            getActivity().bindService(playerIntent, musicServiceConnection, Context.BIND_AUTO_CREATE);
        } else {
            StorageUtils storage = new StorageUtils(getContext());
            storage.storeSong(songList);
            storage.storeAudioIndex(position);

            Intent broadcastIntent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            getActivity().sendBroadcast(broadcastIntent);
        }
    }

    private void loadAudio() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        new SongListLoader(this, contentResolver).execute();
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
    public void setSongs(ArrayList<Song> songs) {
        songList.clear();
        songList.addAll(songs);
        backupList.addAll(songs);
        songAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ArrayList<Song> searchList = new ArrayList<>();
        for(Song s: backupList){
            if(s.getTitle().toLowerCase().contains(newText.toLowerCase())){
                searchList.add(s);
            }
        }
        songList.clear();
        songList.addAll(searchList);
        songAdapter.notifyDataSetChanged();
        return true;
    }
}
