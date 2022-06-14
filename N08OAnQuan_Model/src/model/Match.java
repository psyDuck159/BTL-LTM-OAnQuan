package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Match implements Serializable {

    private int id;
    private Date date;
    private int time;
    private Result result;
    private PlayingPlayer[] players;
    private Board board;
    private Round round;

    public Match(Player p1, Player p2) {
        super();
        this.date = new Date();
        this.board = new Board();
        PlayingPlayer pp1 = new PlayingPlayer(p1);        
        PlayingPlayer pp2 = new PlayingPlayer(p2);
        players = new PlayingPlayer[]{pp1, pp2};
    }

    public Match(int id, int time, Result result, PlayingPlayer[] players, Board board) {
        super();
        this.id = id;
        this.date = new Date();
        this.time = time;
        this.result = result;
        this.players = players;
        this.board = board;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public PlayingPlayer[] getPlayers() {
        return players;
    }

    public void setPlayers(PlayingPlayer[] players) {
        this.players = players;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public Round getRound() {
        return round;
    }

    public void setRound(Round round) {
        this.round = round;
    }

}
