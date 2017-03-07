package io.mokshjn.cosmo.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;

import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.adapters.ArtistListAdapter;
import io.mokshjn.cosmo.interfaces.LibraryInterface;
import io.mokshjn.cosmo.loader.ArtistListLoader;
import io.mokshjn.cosmo.models.Artist;

/**
 * Created by moksh on 7/3/17.
 */

public class ArtistListFragment extends Fragment implements ArtistListAdapter.artistClickListener, LibraryInterface.onLoadArtists {

    private ArrayList<Artist> artistList;
    private FastScrollRecyclerView rvArtistList;
    private ArtistListAdapter adapter;

    public static ArtistListFragment newInstance(){
        ArtistListFragment fragment = new ArtistListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_artist_list, container, false);
        artistList = new ArrayList<>();
        rvArtistList = (FastScrollRecyclerView) rootView.findViewById(R.id.rvArtistList);

        loadArtists();

        initializeRecyclerView();
        return rootView;
    }

    private void initializeRecyclerView() {
        rvArtistList.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ArtistListAdapter(artistList);
        rvArtistList.setAdapter(adapter);
        adapter.setClickListener(this);
    }

    private void loadArtists() {
        new ArtistListLoader(this, getActivity().getContentResolver()).execute();
    }

    @Override
    public void onArtistClick(View v, int p) {

    }

    @Override
    public void setArtists(ArrayList<Artist> artists) {
        artistList.clear();
        artistList.addAll(artists);
        adapter.notifyDataSetChanged();
    }
}
