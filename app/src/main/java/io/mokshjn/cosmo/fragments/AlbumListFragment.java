package io.mokshjn.cosmo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.activities.AlbumViewActivity;
import io.mokshjn.cosmo.adapters.AlbumListAdapter;
import io.mokshjn.cosmo.loader.LibraryLoader;
import io.mokshjn.cosmo.models.Album;

/**
 * Created by moksh on 1/2/17.
 */

public class AlbumListFragment extends Fragment implements AlbumListAdapter.albClickListener {

    private FastScrollRecyclerView rvAlbumList;
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
        View rootView = inflater.inflate(R.layout.fragment_album_list, container, false);
        albumList = new ArrayList<>();
        rvAlbumList = (FastScrollRecyclerView) rootView.findViewById(R.id.rvAlbumList);

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
        Intent intent = new Intent(getActivity(), AlbumViewActivity.class);
        intent.putExtra("albumId", albumList.get(pos).getAlbumId());
        startActivity(intent);
    }
}
