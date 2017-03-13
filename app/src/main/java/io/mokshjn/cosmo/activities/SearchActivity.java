package io.mokshjn.cosmo.activities;

import android.app.SearchManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import butterknife.BindDimen;
import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.mokshjn.cosmo.R;
import io.mokshjn.cosmo.adapters.SongListAdapter;

public class SearchActivity extends AppCompatActivity {

    @BindView(R.id.searchback)
    ImageButton searchBack;
    @BindView(R.id.searchback_container)
    ViewGroup searchBackContainer;
    @BindView(R.id.search_view)
    SearchView searchView;
    @BindView(R.id.search_background)
    View searchBackground;
    @BindView(android.R.id.empty)
    ProgressBar progress;
    @BindView(R.id.search_results)
    RecyclerView results;
    @BindView(R.id.container) ViewGroup container;
    @BindView(R.id.search_toolbar) ViewGroup searchToolbar;
    @BindView(R.id.results_container) ViewGroup resultsContainer;
    @BindView(R.id.fab) ImageButton fab;
    @BindView(R.id.confirm_save_container) ViewGroup confirmSaveContainer;
    @BindView(R.id.save_dribbble)
    CheckedTextView saveDribbble;
    @BindView(R.id.save_designer_news) CheckedTextView saveDesignerNews;
    @BindView(R.id.scrim) View scrim;
    @BindView(R.id.results_scrim) View resultsScrim;
    @BindInt(R.integer.num_columns) int columns;
    @BindDimen(R.dimen.z_app_bar) float appBarElevation;

    SongListAdapter adapter;
    private TextView noResults;
    private SparseArray<Transition> transitions = new SparseArray<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setupSearch();
    }

    private void setupSearch() {
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint(getString(R.string.search_hint));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }
}
