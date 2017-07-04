package io.mokshjn.cosmo.activities;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.fragments.AlbumFragment;
import io.mokshjn.cosmo.fragments.SongsFragment;
import io.mokshjn.cosmo.helpers.LogHelper;
import io.mokshjn.cosmo.transitions.FabTransform;
import io.mokshjn.cosmo.utils.SettingsUtils;

public class MainActivity extends BaseActivity implements SongsFragment.MediaFragmentListener {

    public static final String EXTRA_START_FULLSCREEN =
            "io.mokshjn.cosmo.EXTRA_START_FULLSCREEN";

    public static final String EXTRA_CURRENT_MEDIA_DESCRIPTION =
            "io.mokshjn.cosmo.CURRENT_MEDIA_DESCRIPTION";

    private static final String TAG = LogHelper.makeLogTag(MainActivity.class);
    private static final String SAVED_MEDIA_ID = "io.mokshjn.cosmo.MEDIA_ID";
    private static final int RC_SEARCH = 0;
    private static final int PERMISSION_REQUEST_CODE = 1;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    private Bundle mVoiceSearchParams;
    private ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (!checkPermission()) {
            askPermission();
        } else {
            initializeMediaBrowser();
            initializePager();
        }

        setSupportActionBar(toolbar);
        initializeToolbar();
//        initializeFromParams(savedInstanceState, getIntent());
        if (savedInstanceState == null) {
            startFullScreenActivityIfNeeded(getIntent());
        }
    }

    @OnClick(R.id.fab)
    public void onFabClick(View v) {
        PlaybackStateCompat state = getSupportMediaController().getPlaybackState();
        if (state != null) {
            MediaControllerCompat.TransportControls controls = getSupportMediaController().getTransportControls();
            switch (state.getState()) {
                case PlaybackStateCompat.STATE_PLAYING: // fall through
                case PlaybackStateCompat.STATE_BUFFERING:
                    controls.pause();
                    break;
                case PlaybackStateCompat.STATE_PAUSED:
                case PlaybackStateCompat.STATE_STOPPED:
                    controls.play();
                    break;
                default:
                    LogHelper.d(TAG, "onClick with state ", state.getState());
            }
        }
    }

    @OnLongClick(R.id.fab)
    public boolean onFabLongClick(View v) {
        Intent intent = new Intent(MainActivity.this, FullScreenPlayerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(EXTRA_CURRENT_MEDIA_DESCRIPTION, intent.getParcelableArrayExtra(EXTRA_CURRENT_MEDIA_DESCRIPTION));
        FabTransform.addExtras(intent, ContextCompat.getColor(MainActivity.this, R.color.colorAccent), R.drawable.ic_play_arrow_24dp);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, fab, getString(R.string.transition_player));
        startActivity(intent, options.toBundle());
        return true;
    }

    protected void initializeFromParams(Bundle savedInstanceState, Intent intent) {
//        String mediaId = null;
//        // check if we were started from a "Play XYZ" voice search. If so, we save the extras
//        // (which contain the query details) in a parameter, so we can reuse it later, when the
//        // MediaSession is connected.
//        if (intent.getAction() != null
//                && intent.getAction().equals(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH)) {
//            mVoiceSearchParams = intent.getExtras();
//            LogHelper.d(TAG, "Starting from voice search query=",
//                    mVoiceSearchParams.getString(SearchManager.QUERY));
//        } else {
//            if (savedInstanceState != null) {
//                // If there is a saved media ID, use it
//                mediaId = savedInstanceState.getString(SAVED_MEDIA_ID);
//            }
//        }
    }

    private void startFullScreenActivityIfNeeded(Intent intent) {
        if (intent != null && intent.getBooleanExtra(EXTRA_START_FULLSCREEN, false)) {
            Intent fullScreenIntent = new Intent(this, FullScreenPlayerActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    .putExtra(EXTRA_CURRENT_MEDIA_DESCRIPTION,
                            intent.getParcelableExtra(EXTRA_CURRENT_MEDIA_DESCRIPTION));
            FabTransform.addExtras(intent, ContextCompat.getColor(MainActivity.this, R.color.colorAccent), R.drawable.ic_play_arrow_24dp);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, fab, getString(R.string.transition_player));
            startActivity(fullScreenIntent, options.toBundle());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        String mediaId = getMediaId();
        if (mediaId != null) {
            outState.putString(SAVED_MEDIA_ID, mediaId);
        }
        super.onSaveInstanceState(outState);
    }

    public String getMediaId() {
        return ((SongsFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.main_content + ":" + 0)).getMediaId();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        menu.findItem(R.id.action_repeat).setChecked(SettingsUtils.isRepeatEnabled(this));
        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        LogHelper.d(TAG, "onNewIntent, intent=" + intent);
//        initializeFromParams(null, intent);
        startFullScreenActivityIfNeeded(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cosmo:
                startActivity(new Intent(MainActivity.this, CosmoActivity.class));
                return true;
            case R.id.action_search:
                View searchMenuView = toolbar.findViewById(R.id.action_search);
                Bundle options = ActivityOptions.makeSceneTransitionAnimation(this, searchMenuView,
                        getString(R.string.transition_search_back)).toBundle();
                startActivityForResult(new Intent(this, SearchActivity.class), RC_SEARCH, options);
                return true;
            case R.id.action_repeat:
                SettingsUtils.toggleRepeat(this);
                item.setChecked(SettingsUtils.isRepeatEnabled(this));
                return true;
            default:
                return false;
        }
    }

    private void askPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(MainActivity.this, "Write External Storage permission allows us to read songs. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeMediaBrowser();
                    initializePager();
                } else {
                    Toast.makeText(this, "Permission not granted shutting down app", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_SEARCH:
                View searchMenuView = toolbar.findViewById(R.id.action_search);
                if (searchMenuView != null) {
                    searchMenuView.setAlpha(1f);
                }
                break;
        }
    }

    private void initializePager() {
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.main_content);
        mViewPager.setAdapter(mPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onMediaControllerConnected() {
        if (mVoiceSearchParams != null) {
            // If there is a bootstrap parameter to start from a search query, we
            // send it to the media session and set it to null, so it won't play again
            // when the activity is stopped/started or recreated:
            String query = mVoiceSearchParams.getString(SearchManager.QUERY);
            getSupportMediaController().getTransportControls()
                    .playFromSearch(query, mVoiceSearchParams);
            mVoiceSearchParams = null;
        }
        ((SongsFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.main_content + ":" + 0)).onConnected();
        ((AlbumFragment) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.main_content + ":" + 1)).onConnected();
    }

    private class PagerAdapter extends FragmentPagerAdapter {

        PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SongsFragment();
//                case 2:
//                    return AlbumListFragment.newInstance();
                case 1:
                    return new AlbumFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Songs";
                case 1:
                    return "Albums";
//                case 2:
//                    return "Artists";
            }
            return null;
        }
    }
}
