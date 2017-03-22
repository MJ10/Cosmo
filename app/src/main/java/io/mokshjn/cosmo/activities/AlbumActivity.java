package io.mokshjn.cosmo.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.widgets.ElasticDragDismissFrameLayout;

/**
 * Created by moksh on 22/3/17.
 */

public class AlbumActivity extends AppCompatActivity {

    @BindView(R.id.draggable_frame)
    ElasticDragDismissFrameLayout draggableFrame;

    ElasticDragDismissFrameLayout.SystemChromeFader chromeFader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_view_activity);
        ButterKnife.bind(this);
        chromeFader = new ElasticDragDismissFrameLayout.SystemChromeFader(this) {
            @Override
            public void onDragDismissed() {
                finishAfterTransition();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        draggableFrame.addListener(chromeFader);
    }

    @Override
    protected void onPause() {
        super.onPause();
        draggableFrame.removeListener(chromeFader);
    }
}
