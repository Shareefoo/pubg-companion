package com.shareefoo.pubgcompanion.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.shareefoo.pubgcompanion.R;
import com.shareefoo.pubgcompanion.api.ApiClient;
import com.shareefoo.pubgcompanion.api.ApiInterface;
import com.shareefoo.pubgcompanion.data.SpManager;
import com.shareefoo.pubgcompanion.model.Duo;
import com.shareefoo.pubgcompanion.model.DuoFpp;
import com.shareefoo.pubgcompanion.model.MatchesDuo;
import com.shareefoo.pubgcompanion.model.MatchesDuoFPP;
import com.shareefoo.pubgcompanion.model.MatchesSolo;
import com.shareefoo.pubgcompanion.model.MatchesSoloFPP;
import com.shareefoo.pubgcompanion.model.MatchesSquad;
import com.shareefoo.pubgcompanion.model.MatchesSquadFPP;
import com.shareefoo.pubgcompanion.model.PlayerSeasonMatchesData;
import com.shareefoo.pubgcompanion.model.PlayerSeasonResponse;
import com.shareefoo.pubgcompanion.model.SeasonData;
import com.shareefoo.pubgcompanion.model.SeasonResponse;
import com.shareefoo.pubgcompanion.model.Solo;
import com.shareefoo.pubgcompanion.model.SoloFpp;
import com.shareefoo.pubgcompanion.model.Squad;
import com.shareefoo.pubgcompanion.model.SquadFpp;
import com.shareefoo.pubgcompanion.utils.NetworkUtils;
import com.shareefoo.pubgcompanion.widget.PUBGAppWidget;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


public class StatsFragment extends Fragment {

    @BindView(R.id.textView_playerName)
    TextView textViewPlayerName;

    @BindView(R.id.spinner_seasons)
    Spinner spinnerSeasons;

    @BindView(R.id.spinner_modes)
    Spinner spinnerModes;

    @BindView(R.id.textView_games)
    TextView textViewGames;

    @BindView(R.id.textView_wins)
    TextView textViewWins;

    @BindView(R.id.textView_top10)
    TextView textViewTop10;

    @BindView(R.id.textView_kd)
    TextView textViewKD;

    @BindView(R.id.textView_avg_dmg)
    TextView textViewAvgDmg;

    @BindView(R.id.adView)
    AdView adView;

    private String mPlayerId;
    private String mPlayerName;

    private SpManager spManager;

    OnMatchesDataLoad mCallback;

    private int games;
    private int wins;
    private int top10;
    private double kd;
    private double dmg;

    private ProgressDialog progressDialog;

//    private static final String KEY_GAMES = "key_games";
//    private static final String KEY_WINS = "key_wins";
//    private static final String KEY_TOP10 = "key_top10";
//    private static final String KEY_KD = "key_kd";
//    private static final String KEY_DMG = "key_dmg";

    public StatsFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param playerId   Id of the player.
//     * @param playerName name of the player.
//     * @return A new instance of fragment StatsFragment.
//     */
//    public static StatsFragment newInstance(String playerId, String playerName) {
//        StatsFragment fragment = new StatsFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PLAYER_ID, playerId);
//        args.putString(ARG_PLAYER_NAME, playerName);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (getArguments() != null) {
//            mPlayerId = getArguments().getString(ARG_PLAYER_ID);
//            mPlayerName = getArguments().getString(ARG_PLAYER_NAME);
//
//        } else if (spManager.getBoolean("player_fetched", false)) {
//            mPlayerId = spManager.getString("player_id", "");
//            mPlayerName = spManager.getString("player_name", "");
//        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_overview, container, false);
        ButterKnife.bind(this, rootView);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        spManager = SpManager.getInstance(getContext());

        //
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");

        mPlayerId = spManager.getString(SpManager.KEY_PLAYER_ID, "");
        mPlayerName = spManager.getString(SpManager.KEY_PLAYER_NAME, "");

        // Check if player name selected
        if (!TextUtils.isEmpty(mPlayerName)) {

            textViewPlayerName.setText(mPlayerName);

//            if (savedInstanceState != null) {
//                games = savedInstanceState.getInt(KEY_GAMES);
//                wins = savedInstanceState.getInt(KEY_WINS);
//                top10 = savedInstanceState.getInt(KEY_TOP10);
//                kd = savedInstanceState.getDouble(KEY_KD);
//                dmg = savedInstanceState.getDouble(KEY_DMG);
//
//                textViewGames.setText(games);
//                textViewWins.setText(wins);
//                textViewTop10.setText(top10);
//                textViewKD.setText(String.valueOf(kd));
//                textViewAvgDmg.setText(String.valueOf(dmg));
//
//            } else {

            spinnerModes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String mode = adapterView.getSelectedItem().toString();

//                    String mode;
//
//                    if (!spManager.getString("game_mode", "").isEmpty()) {
//                        mode = spManager.getString("game_mode", "");
//                    } else {
//                        mode = adapterView.getSelectedItem().toString();
//                    }
//
//                    spManager.putString("game_mode", mode);

                    getSeasons(mode);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                    //
                }
            });

//            }

        } else {
            //
            Toast.makeText(getContext(), R.string.prompt_player_search, Toast.LENGTH_LONG).show();
        }

        return rootView;
    }

    /**
     * This method will inflate the season spinner
     *
     * @param mode game mode
     */
    private void getSeasons(final String mode) {
        //
        progressDialog.show();
        //
        if (NetworkUtils.IsNetworkAvailable(getContext())) {
            ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<SeasonResponse> seasonResponseCall = apiService.getSeasons();
            seasonResponseCall.enqueue(new Callback<SeasonResponse>() {
                @Override
                public void onResponse(@NonNull Call<SeasonResponse> call, @NonNull Response<SeasonResponse> response) {
                    Timber.d("onResponse: %s", response.toString());

                    //
                    progressDialog.dismiss();

                    if (response.isSuccessful()) {

                        SeasonResponse seasonResponse = response.body();

                        if (seasonResponse != null) {

                            List<String> seasonsNames = new ArrayList<>();

                            final List<SeasonData> seasonDataList = seasonResponse.getSeasonData();
                            for (SeasonData seasonData : seasonDataList) {
                                String id = seasonData.getId();
                                int startIndex = id.lastIndexOf(".");
                                String seasonName = id.substring(startIndex + 1, id.length());
                                seasonsNames.add(seasonName);
                            }

                            Collections.reverse(seasonsNames);

                            Collections.reverse(seasonDataList);

                            ArrayAdapter<String> seasonsAdapter = new ArrayAdapter<String>(getContext(), R.layout.simple_spinner_item, seasonsNames);
                            seasonsAdapter.setDropDownViewResource(R.layout.simple_spinner_item);

                            spinnerSeasons.setAdapter(seasonsAdapter);
                            spinnerSeasons.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    int selectedIndex = adapterView.getSelectedItemPosition();

                                    String seasonsId = null;

                                    for (int j = 0; j < seasonDataList.size(); j++) {
                                        if (selectedIndex == j) {
                                            seasonsId = seasonDataList.get(j).getId();
                                        }
                                    }

                                    //
                                    getPlayerSeason(mPlayerId, seasonsId, mode);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    //
                                }
                            });

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
                public void onFailure(@NonNull Call<SeasonResponse> call, @NonNull Throwable t) {
                    Timber.d("onFailure: %s", t.getMessage());

                    //
                    progressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(getContext(), R.string.no_connection, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * This methods will get player stats in the selected season and mode
     *
     * @param playerId player id
     * @param seasonId season id
     * @param mode     game mode
     */
    private void getPlayerSeason(String playerId, String seasonId, final String mode) {
        //
        progressDialog.show();

        ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<PlayerSeasonResponse> playerSeasonResponseCall = apiService.getPlayerSeason(playerId, seasonId);
        playerSeasonResponseCall.enqueue(new Callback<PlayerSeasonResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlayerSeasonResponse> call, @NonNull Response<PlayerSeasonResponse> response) {
                Timber.d("onResponse: %s", response.toString());

                //
                progressDialog.dismiss();

                if (response.isSuccessful()) {

                    PlayerSeasonResponse playerSeasonResponse = response.body();

                    if (playerSeasonResponse != null) {

                        List<PlayerSeasonMatchesData> matchesData;

                        // TODO: refactor code (reduce duplication)

                        if (mode.equals("Solo")) {
                            Solo solo = playerSeasonResponse.getPlayerSeasonData().getAttributes().getGameModeStats().getSolo();

                            games = solo.getRoundsPlayed();
                            wins = solo.getWins();
                            top10 = solo.getTop10s();
                            kd = solo.getKills() / (1 + solo.getLosses());
                            dmg = solo.getDamageDealt();

                            MatchesSolo matches = playerSeasonResponse.getPlayerSeasonData().getRelationships().getMatchesSolo();
                            matchesData = matches.getData();

                            mCallback.onMatchesLoad(matchesData);

                        } else if (mode.equals("Solo FPP")) {
                            SoloFpp soloFpp = playerSeasonResponse.getPlayerSeasonData().getAttributes().getGameModeStats().getSoloFpp();

                            games = soloFpp.getRoundsPlayed();
                            wins = soloFpp.getWins();
                            top10 = soloFpp.getTop10s();
                            kd = soloFpp.getKills() / (1 + soloFpp.getLosses());
                            dmg = soloFpp.getDamageDealt();

                            MatchesSoloFPP matches = playerSeasonResponse.getPlayerSeasonData().getRelationships().getMatchesSoloFPP();
                            matchesData = matches.getData();

                            mCallback.onMatchesLoad(matchesData);

                        } else if (mode.equals("Duo")) {
                            Duo duo = playerSeasonResponse.getPlayerSeasonData().getAttributes().getGameModeStats().getDuo();

                            games = duo.getRoundsPlayed();
                            wins = duo.getWins();
                            top10 = duo.getTop10s();
                            kd = duo.getKills() / (1 + duo.getLosses());
                            dmg = duo.getDamageDealt();

                            MatchesDuo matches = playerSeasonResponse.getPlayerSeasonData().getRelationships().getMatchesDuo();
                            matchesData = matches.getData();

                            mCallback.onMatchesLoad(matchesData);

                        } else if (mode.equals("Duo FPP")) {
                            DuoFpp duoFpp = playerSeasonResponse.getPlayerSeasonData().getAttributes().getGameModeStats().getDuoFpp();

                            games = duoFpp.getRoundsPlayed();
                            wins = duoFpp.getWins();
                            top10 = duoFpp.getTop10s();
                            kd = duoFpp.getKills() / (1 + duoFpp.getLosses());
                            dmg = duoFpp.getDamageDealt();

                            MatchesDuoFPP matches = playerSeasonResponse.getPlayerSeasonData().getRelationships().getMatchesDuoFPP();
                            matchesData = matches.getData();

                            mCallback.onMatchesLoad(matchesData);

                        } else if (mode.equals("Squad")) {
                            Squad squad = playerSeasonResponse.getPlayerSeasonData().getAttributes().getGameModeStats().getSquad();

                            games = squad.getRoundsPlayed();
                            wins = squad.getWins();
                            top10 = squad.getTop10s();
                            kd = squad.getKills() / (1 + squad.getLosses());
                            dmg = squad.getDamageDealt();

                            MatchesSquad matches = playerSeasonResponse.getPlayerSeasonData().getRelationships().getMatchesSquad();
                            matchesData = matches.getData();

                            mCallback.onMatchesLoad(matchesData);

                        } else if (mode.equals("Squad FPP")) {
                            SquadFpp squadFpp = playerSeasonResponse.getPlayerSeasonData().getAttributes().getGameModeStats().getSquadFpp();

                            games = squadFpp.getRoundsPlayed();
                            wins = squadFpp.getWins();
                            top10 = squadFpp.getTop10s();
                            kd = squadFpp.getKills() / (1 + squadFpp.getLosses());
                            dmg = squadFpp.getDamageDealt();

                            MatchesSquadFPP matches = playerSeasonResponse.getPlayerSeasonData().getRelationships().getMatchesSquadFPP();
                            matchesData = matches.getData();

                            mCallback.onMatchesLoad(matchesData);
                        }

                        textViewGames.setText(String.valueOf(games));
                        textViewWins.setText(String.valueOf(wins));
                        textViewTop10.setText(String.valueOf(top10));
                        textViewKD.setText(String.valueOf(kd));
                        textViewAvgDmg.setText(String.valueOf(dmg));

                        spManager.putInt("no_games", games);
                        spManager.putInt("no_wins", wins);
                        spManager.putInt("no_top10", top10);

                        // update the widget
                        Intent intent = new Intent(getContext(), PUBGAppWidget.class);
                        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
                        // since it seems the onUpdate() is only fired on that:
                        int[] ids = AppWidgetManager.getInstance(getContext()).getAppWidgetIds(new ComponentName(getActivity(), PUBGAppWidget.class));
                        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                        getContext().sendBroadcast(intent);

//                        Intent intent = new Intent(getContext(), PUBGAppWidget.class);
//                        intent.setAction(PUBGAppWidget.ACTION_APPWIDGET);
//                        intent.putExtra("games", 8);
//                        intent.putExtra("wins", 1);
//                        intent.putExtra("top10", 3);
//                        getContext().sendBroadcast(intent);
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
            public void onFailure(@NonNull Call<PlayerSeasonResponse> call, @NonNull Throwable t) {
                Timber.e("onFailure: %s", t.getMessage());

                //
                progressDialog.dismiss();
            }
        });
    }

    public interface OnMatchesDataLoad {
        public void onMatchesLoad(List<PlayerSeasonMatchesData> matchesData);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnMatchesDataLoad) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    // TODO: save fragment state
//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        outState.putInt(KEY_GAMES, games);
//        outState.putInt(KEY_WINS, wins);
//        outState.putInt(KEY_TOP10, top10);
//        outState.putDouble(KEY_KD, kd);
//        outState.putDouble(KEY_DMG, dmg);
//    }

}
