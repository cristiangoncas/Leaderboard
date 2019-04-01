package com.cristiangoncas.looperthread;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

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

    public LinkedList<TeamItem> getTeamList() {
        return teamList;
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
        notifyItemInserted(teamList.size());
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
            teamName.setText(team.name);
            teamDisputedMatches.setText(String.valueOf(team.disputedMatches));
            teamWinedMatches.setText(String.valueOf(team.winedMatches));
            teamLostMatches.setText(String.valueOf(team.lostMatches));
            teamTiedMatches.setText(String.valueOf(team.tiedMatches));
            teamPoints.setText(String.valueOf(team.points));
        }
    }
}