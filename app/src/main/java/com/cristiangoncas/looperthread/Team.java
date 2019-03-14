package com.cristiangoncas.looperthread;

import java.util.LinkedHashMap;
import java.util.Random;

public class Team {

    public static LinkedHashMap<Integer, Team> sTeamMap = new LinkedHashMap<>();
    private static int sCounter;
    private int mId;
    private int mSkills;
    private String mName;
    private MatchResultCallback mCallback;

    public Team(String name, MatchResultCallback callback) {
        mId = sCounter++;
        mName = name;
        mSkills = new Random().nextInt(100);
        sTeamMap.put(mId, this);
        mCallback = callback;
    }

    public static void disposeAll() {
        sTeamMap.clear();
    }

    public String getName() {
        return mName;
    }

    public int getId() {
        return mId;
    }

    public int getSkills() {
        return mSkills;
    }

    public synchronized void onMatchResultReceived(MatchResult matchResult) {
        mCallback.onMatchResult(this, matchResult.getResult());
    }

    public interface MatchResultCallback {
        void onMatchResult(Team winner, Integer result);
    }
}
