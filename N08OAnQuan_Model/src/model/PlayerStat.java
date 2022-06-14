package model;

import java.io.Serializable;

public class PlayerStat extends Player implements Serializable {

    private int totalPoint;
    private int winTotal;
    private int loseTotal;
    private float winAverage;
    private int matchTotal;
    private int totalScore;

    public PlayerStat() {
        super();
    }

    public PlayerStat(int id, String username, String password, String name, String country, String email,
            String urlAvatar, int totalPoint, int winTotal, float winAverage, int matchTotal) {
        super(id, username, password, name, country, email, urlAvatar);
        this.totalPoint = totalPoint;
        this.winTotal = winTotal;
        this.winAverage = winAverage;
        this.matchTotal = matchTotal;
    }
    
    public int getDrawTotal() {
        return matchTotal - loseTotal - winTotal;
    }
    
    public int getTotalPoint() {
        return totalPoint = winTotal * 3 + getDrawTotal();
    }

    public int getWinTotal() {
        return winTotal;
    }

    public void setWinTotal(int winTotal) {
        this.winTotal = winTotal;
    }

    public int getLoseTotal() {
        return loseTotal;
    }

    public void setLoseTotal(int loseTotal) {
        this.loseTotal = loseTotal;
    }
        

    public float getWinProb() {
        if(matchTotal == 0) return (float) 0.0;
        return (float) winTotal * 100 / matchTotal;
    }

    public float getWinAverage() {
        if(matchTotal == 0) return 0;
        return winAverage = (float) winTotal * 100 / matchTotal;
    }

    public int getMatchTotal() {
        return matchTotal;
    }

    public void setMatchTotal(int matchTotal) {
        this.matchTotal = matchTotal;
    }    

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }
    
}
