package io.mokshjn.cosmo.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;

import io.mokshjn.cosmo.R;

public class AlbumViewActivity extends AppCompatActivity {
    private RecyclerView rvAlbumSongs;
    private TextView tvAlbumTitle, tvAlbumArtist;
    private ImageView ivAlbumArt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_view);

    }
}
