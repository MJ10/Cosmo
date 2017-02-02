package io.mokshjn.cosmo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.adapters.AlbumListAdapter;
import io.mokshjn.cosmo.loader.LibraryLoader;
import io.mokshjn.cosmo.models.Album;

/**
 * Created by moksh on 1/2/17.
 */

public class AlbumListFragment extends Fragment implements AlbumListAdapter.albClickListener {

    private RecyclerView rvAlbumList;
    private ArrayList<Album> albumList;
    private AlbumListAdapter adapter;
    private LibraryLoader loader;

    public static AlbumListFragment newInstance() {
        AlbumListFragment fragment = new AlbumListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);
        albumList = new ArrayList<>();
        rvAlbumList = (RecyclerView) rootView.findViewById(R.id.rvSongList);
        loadAlbums();
        initalizeRecyclerView();

        return rootView;
    }

    private void loadAlbums() {
        loader = new LibraryLoader(getActivity().getContentResolver());
        albumList = loader.getAlbumList();

    }

    private void initalizeRecyclerView() {
        rvAlbumList.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new AlbumListAdapter(albumList, getContext());
        rvAlbumList.setAdapter(adapter);
        adapter.setClickListener(this);
    }

    @Override
    public void onAlbumClick(View v, int pos) {

    }
}
