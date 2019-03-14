package com.cristiangoncas.looperthread;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
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
        linkedList.add(new LeaderboardAdapter.TeamItem(true));
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
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLeaderboardAdapter.matchEnded(team, result);
                String resultTxt = "Tied";
                if (result == 1) {
                    resultTxt = "Won";
                } else if (result == 0) {
                    resultTxt = "Lost";
                }
                Log.d("LooperThread", "Match result " + team.getName() + " " + resultTxt);
//                Log.d("LooperThread", "Match result\n"
//                        + local.getName()
//                        + "\n - Skills: " + local.getSkills() + " +30% as local - " + (int) (local.getSkills() + (local.getSkills() * 0.3))
//                        + "\n vs ----------------------> Winner: " + ((team == local.getId()) ? local.getName() : visitor.getName()) + "\n"
//                        + visitor.getName()
//                        + "\n - Skills: " + visitor.getSkills());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSimulator.stop();
    }

    @Override
    public void sortLeaderboard() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Collections.sort(mLeaderboardAdapter.teamList, new Comparator<LeaderboardAdapter.TeamItem>() {
                    @Override
                    public int compare(LeaderboardAdapter.TeamItem o1, LeaderboardAdapter.TeamItem o2) {
                        return Collator.getInstance().compare(String.valueOf(o1.points), String.valueOf(o2.points));
                    }
                });
                mLeaderboardAdapter.notifyDataSetChanged();
            }
        });
    }

    public static class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

        private LinkedList<TeamItem> teamList;
        private Context context;

        public LeaderboardAdapter(Context context, LinkedList<TeamItem> teamList) {
            this.context = context;
            this.teamList = teamList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.leaderboard_item, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.onBind(position);
        }

        @Override
        public int getItemCount() {
            return teamList.size();
        }

        public void matchEnded(Team team, int result) {
            TeamItem teamItem;
            for (int i = 0; i < teamList.size(); i++) {
                if (team.getId() == teamList.get(i).id) {
                    teamItem = teamList.get(i);
                    teamItem.disputedMatch(result);
                    notifyItemChanged(i);
                    return;
                }
            }
            teamItem = new TeamItem(team.getId(), team.getName());
            teamItem.disputedMatch(result);
            teamList.add(teamItem);
            notifyItemInserted(teamList.size() - 1);
        }

        public static class TeamItem {
            int id;
            String name;
            int disputedMatches;
            int winedMatches;
            int lostMatches;
            int tiedMatches;
            int points;

            public TeamItem(int teamId, String teamName) {
                id = teamId;
                name = teamName;
                disputedMatches = 0;
                winedMatches = 0;
                lostMatches = 0;
                points = 0;
            }

            public TeamItem(boolean initializeAsHeader) {
                id = -99;
                name = "Team Name";
                disputedMatches = 0;
                winedMatches = 0;
                lostMatches = 0;
                points = 0;
            }

            public void disputedMatch(int result) {
                disputedMatches++;
                if (result == 1) {
                    winedMatches++;
                    points += 3;
                } else if (result == 0) {
                    lostMatches++;
                } else {
                    tiedMatches++;
                    points++;
                }
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView teamName;
            TextView teamDisputedMatches;
            TextView teamWinedMatches;
            TextView teamLostMatches;
            TextView teamTiedMatches;
            TextView teamPoints;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                teamName = itemView.findViewById(R.id.team_name);
                teamDisputedMatches = itemView.findViewById(R.id.team_disputed_matches);
                teamWinedMatches = itemView.findViewById(R.id.team_wined_matches);
                teamLostMatches = itemView.findViewById(R.id.team_lost_matches);
                teamTiedMatches = itemView.findViewById(R.id.team_tied_matches);
                teamPoints = itemView.findViewById(R.id.team_points);
            }

            public void onBind(int position) {
                TeamItem team = teamList.get(position);
                if (team.id == -99) {
                    teamName.setText("Team name");
                    teamDisputedMatches.setText("Matches");
                    teamWinedMatches.setText("Win");
                    teamLostMatches.setText("Lost");
                    teamTiedMatches.setText("Tie");
                    teamPoints.setText("Points");
                } else {
                    teamName.setText(team.name);
                    teamDisputedMatches.setText(String.valueOf(team.disputedMatches));
                    teamWinedMatches.setText(String.valueOf(team.winedMatches));
                    teamLostMatches.setText(String.valueOf(team.lostMatches));
                    teamTiedMatches.setText(String.valueOf(team.tiedMatches));
                    teamPoints.setText(String.valueOf(team.points));
                }
            }
        }
    }
}
