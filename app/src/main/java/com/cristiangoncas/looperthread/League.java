package com.cristiangoncas.looperthread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;

public class League extends HandlerThread {

    private static final String TAG = League.class.getSimpleName();
    private LinkedHashMap<Integer, Handler> mTeamsMap;
    private SortLeaderboard mCallback;

    public League(LinkedHashMap<Integer, Handler> teamsMap, SortLeaderboard callback) {
        super(TAG);
        mTeamsMap = teamsMap;
        mCallback = callback;
    }

    public synchronized void register(final Team team) throws Exception {
        if (team == null) {
            throw new Exception();
        }
        if (mTeamsMap.containsKey(team.getId())) {
            throw new Exception();
        }
        final WeakReference<Team> teamWeakReference = new WeakReference<>(team);
        Handler handler = new Handler(getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Team team = teamWeakReference.get();
                if (team != null) {
                    team.onMatchResultReceived(new MatchResult(msg.arg1));
                }
            }
        };
        mTeamsMap.put(team.getId(), handler);
    }

    public synchronized void playMatch(Match match) throws Exception {
        if (!mTeamsMap.containsKey(match.getTeamLocal().getId()) || !mTeamsMap.containsKey(match.getTeamVisitor().getId())) {
            throw new Exception();
        }
        Match.Result result = match.getResult();

        Handler handlerLocal = mTeamsMap.get(result.getWinner());
        Message messageLocal = new Message();
        messageLocal.arg1 = result.getResultWinner();
        handlerLocal.sendMessage(messageLocal);

        Handler handlerVisitor = mTeamsMap.get(result.getLooser());
        Message messageVisitor = new Message();
        messageVisitor.arg1 = result.getResultLooser();
        handlerVisitor.sendMessage(messageVisitor);
    }

    public void sortLeaderboard() {
        mCallback.sortLeaderboard();
    }

    public interface SortLeaderboard {
        void sortLeaderboard();
    }
}