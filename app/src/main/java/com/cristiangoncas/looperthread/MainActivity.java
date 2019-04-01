package com.cristiangoncas.looperthread;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements Team.MatchResultCallback, League.SortLeaderboard {

    private RecyclerView mLeaderboard;
    private LeaderboardAdapter mLeaderboardAdapter;
    private Simulator mSimulator;
    private League league;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLeaderboard = findViewById(R.id.recycler);
        mLeaderboard.setLayoutManager(new LinearLayoutManager(this));
        LinkedList<LeaderboardAdapter.TeamItem> linkedList = new LinkedList<>();
        mLeaderboardAdapter = new LeaderboardAdapter(this, linkedList);
        mLeaderboard.setAdapter(mLeaderboardAdapter);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        league = new League(new LinkedHashMap<Integer, Handler>(), this);

        league.start();
        mSimulator = new Simulator(league, this);
        mSimulator.createTeams(10).start();
    }

    @Override
    public void onMatchResult(final Team team, final Integer result) {
        runOnUiThread(() -> {
            mLeaderboardAdapter.matchEnded(team, result);
            String resultTxt = "Tied";
            if (result == 1) {
                resultTxt = "Won";
            } else if (result == 0) {
                resultTxt = "Lost";
            }

            Log.d("LooperThread", "Match result " + team.getName() + " " + resultTxt);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSimulator.stop();
    }

    @Override
    public void sortLeaderboard() {
        runOnUiThread(() -> {
            Collections.sort(mLeaderboardAdapter.getTeamList(), (o1, o2) -> Integer.compare(o2.points, o1.points));
            mLeaderboardAdapter.notifyDataSetChanged();
        });
    }

}
