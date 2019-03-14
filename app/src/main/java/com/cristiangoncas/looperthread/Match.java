package com.cristiangoncas.looperthread;

import java.util.LinkedHashMap;

public class Match {

    private static LinkedHashMap<Integer, Match> sMatchesMap = new LinkedHashMap<>();
    private static int sCounter;
    private int mId;
    private Team mTeamLocal;
    private Team mTeamVisitor;

    public Match(Team local, Team visitor) {
        mId = sCounter++;
        this.mTeamLocal = local;
        this.mTeamVisitor = visitor;
        sMatchesMap.put(mId, this);
    }

    public Integer getId() {
        return mId;
    }

    public Team getTeamLocal() {
        return mTeamLocal;
    }

    public Team getTeamVisitor() {
        return mTeamVisitor;
    }

    public Result getResult() {
        int localSkills = increaseLocalSkills();
        int visitorSkills = mTeamVisitor.getSkills();
        if (localSkills == visitorSkills) {
            return new Result(mTeamLocal.getId(), mTeamVisitor.getId(), -1);
        } else if (localSkills >= visitorSkills) {
            return new Result(mTeamLocal.getId(), mTeamVisitor.getId(), 1);
        } else {
            return new Result(mTeamVisitor.getId(), mTeamLocal.getId(), 0);
        }
    }

    /**
     * Increase local skills by 30%
     */
    private int increaseLocalSkills() {
        int localSkills = mTeamLocal.getSkills();
        int increasedSkills = (int) (localSkills + (localSkills * 0.3));
        return (increasedSkills >= 100) ? 100 : increasedSkills;
    }

    public class Result {
        private int winner;
        private int looser;
        private int result;

        public Result(int winnerId, int looserId, int result) {
            winner = winnerId;
            looser = looserId;
            this.result = result;
        }

        public int getWinner() {
            return winner;
        }

        public int getLooser() {
            return looser;
        }

        public int getResultWinner() {
            return (result != -1) ? 1 : result;
        }

        public int getResultLooser() {
            return (result != -1) ? 0 : result;
        }
    }
}
