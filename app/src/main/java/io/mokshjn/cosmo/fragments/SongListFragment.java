package io.mokshjn.cosmo.fragments;

import android.content.ContentResolver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.adapters.SongListAdapter;
import io.mokshjn.cosmo.interfaces.LibraryInterface;
import io.mokshjn.cosmo.loader.SongListLoader;
import io.mokshjn.cosmo.models.Song;

public class SongListFragment extends Fragment implements SongListAdapter.ClickListener, LibraryInterface.onLoadSongs{

    public static final String Broadcast_PLAY_NEW_AUDIO = "io.mokshjn.cosmo.PlayNewAudio";
    private FastScrollRecyclerView rvSongsList;
    private SongListAdapter songAdapter;
    private ArrayList<Song> songList, backupList;

    public static SongListFragment newInstance() {
        SongListFragment fragment = new SongListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("MUSIC_BOUND_STATE", false);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);

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

    }

    private void loadAudio() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        new SongListLoader(this, contentResolver).execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void setSongs(ArrayList<Song> songs) {
        songList.clear();
        songList.addAll(songs);
        backupList.addAll(songs);
        songAdapter.notifyDataSetChanged();
    }
}
