package com.shareefoo.pubgcompanion.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.shareefoo.pubgcompanion.R;
import com.shareefoo.pubgcompanion.api.ApiClient;
import com.shareefoo.pubgcompanion.api.ApiInterface;
import com.shareefoo.pubgcompanion.data.SpManager;
import com.shareefoo.pubgcompanion.dialogs.PlayerNameDialogActivity;
import com.shareefoo.pubgcompanion.fragments.MapsFragment;
import com.shareefoo.pubgcompanion.fragments.MatchesFragment;
import com.shareefoo.pubgcompanion.fragments.StatsFragment;
import com.shareefoo.pubgcompanion.model.CollectionPlayersResponse;
import com.shareefoo.pubgcompanion.model.PlayerSeasonMatchesData;
import com.shareefoo.pubgcompanion.utils.NetworkUtils;

import org.json.JSONObject;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements StatsFragment.OnMatchesDataLoad {

    private final int SEARCH_DIALOG_REQUEST_CODE = 100;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private SpManager spManager;

    private final static String KEY_CURRENT_FRAGMENT = "current_fragment";

    private Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        MobileAds.initialize(this, getResources().getString(R.string.admob_app_id));

        spManager = SpManager.getInstance(this);

        if (TextUtils.isEmpty(spManager.getString(SpManager.KEY_PLAYER_NAME, ""))) {
            showSearchDialog();
        }

        if (savedInstanceState != null) {
            fragment = getSupportFragmentManager().getFragment(savedInstanceState, KEY_CURRENT_FRAGMENT);
            loadFragment(fragment);
        } else {
            fragment = new StatsFragment();
            loadFragment(fragment);
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_stats:
                    fragment = new StatsFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_matches:
                    fragment = new MatchesFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_maps:
                    fragment = new MapsFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        //
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

            case R.id.action_search:
                showSearchDialog();
                return true;

            case R.id.action_nearby_players:
                showNearbyPlayersActivity();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSearchDialog() {
        // TODO: use dialog fragment instead of dialog activity
        Intent intent = new Intent(this, PlayerNameDialogActivity.class);
        startActivityForResult(intent, SEARCH_DIALOG_REQUEST_CODE);
    }

    private void showNearbyPlayersActivity() {
        // TODO: make this option in separate Settings Activity
        if (!spManager.getBoolean("share_location", false)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.prompt_share_location);
            builder.setCancelable(false);
            builder.setPositiveButton(
                    R.string.prompt_ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            spManager.putBoolean("share_location", true);
                            Intent intent = new Intent(MainActivity.this, NearbyPlayersActivity.class);
                            startActivity(intent);
                        }
                    });

            builder.setNegativeButton(
                    R.string.prompt_cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert1 = builder.create();
            alert1.show();

        } else {
            Intent intent = new Intent(MainActivity.this, NearbyPlayersActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEARCH_DIALOG_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String playerName = data.getStringExtra(SpManager.KEY_PLAYER_NAME);
                getPlayerByNameRequest(playerName);
            }
        }
    }

    private void getPlayerByNameRequest(final String playerName) {
        if (NetworkUtils.IsNetworkAvailable(this)) {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<CollectionPlayersResponse> playersResponseCall = apiService.getCollectionPlayersByNames(playerName);
            playersResponseCall.enqueue(new Callback<CollectionPlayersResponse>() {
                @Override
                public void onResponse(@NonNull Call<CollectionPlayersResponse> call, @NonNull Response<CollectionPlayersResponse> response) {
                    Timber.d("onResponse: %s", response.toString());

                    if (response.isSuccessful()) {

                        CollectionPlayersResponse playersResponse = response.body();

                        if (playersResponse != null) {

                            String playerId = playersResponse.getPlayerData().get(0).getId();
                            String playerName = playersResponse.getPlayerData().get(0).getAttributes().getName();

                            spManager.putBoolean("player_fetched", true);
                            spManager.putString(SpManager.KEY_PLAYER_ID, playerId);
                            spManager.putString(SpManager.KEY_PLAYER_NAME, playerName);

                            StatsFragment overviewFragment = new StatsFragment();
                            loadFragment(overviewFragment);
                        }

                    } else {
                        try {
                            JSONObject errorObject = new JSONObject(response.errorBody().string());
                            Timber.e("onResponse: %s", errorObject.toString());
                        } catch (Exception e) {
                            Timber.e("onResponse: %s", e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<CollectionPlayersResponse> call, @NonNull Throwable t) {
                    Timber.e("onFailure: %s", t.getMessage());
                }
            });
        } else {
            Toast.makeText(this, R.string.no_connection, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onMatchesLoad(List<PlayerSeasonMatchesData> matchesData) {
        MatchesFragment.matchesData = matchesData;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, KEY_CURRENT_FRAGMENT, fragment);
    }
}
