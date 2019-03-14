package com.cristiangoncas.looperthread;

import android.util.Log;

import java.util.Random;

public class Simulator implements Runnable {

    private League mLeague;
    private Random mRandom;
    private Thread mThread;
    private boolean controller;
    private Team.MatchResultCallback mCallback;

    public Simulator(League mLeague, Team.MatchResultCallback callback) {
        this.mLeague = mLeague;
        mRandom = new Random(System.currentTimeMillis());
        mThread = new Thread(this);
        controller = true;
        mCallback = callback;
    }

    public Simulator createTeams(int numTeams) {
        for (int i = 0; i < numTeams; i++) {
            try {
                mLeague.register(new Team("Team " + i, mCallback));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public synchronized void start() {
        if (!mThread.isAlive()) {
            mThread.start();
        }
    }

    @Override
    public void run() {
        controller = true;
        int counter = 0;
        int numTeams = Team.sTeamMap.size();
        Team teamLocal = Team.sTeamMap.get(counter);
        while (controller) {
            for (int i = 0; i < numTeams; i++) {
                if (i != teamLocal.getId()) {
                    try {
                        Team teamVisitor = Team.sTeamMap.get(i);
                        Log.d("LooperThread", "Match " + teamLocal.getName() + " vs " + teamVisitor.getName());
                        Match match = new Match(teamLocal, teamVisitor);
                        mLeague.playMatch(match);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            wait(1000);
            if (counter + 1 < numTeams) {
                counter++;
                teamLocal = Team.sTeamMap.get(counter);
                mLeague.sortLeaderboard();
                wait(200);
            } else {
                controller = false;
                stop();
            }
        }
    }

    public void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        controller = false;
        Team.disposeAll();
    }
}
