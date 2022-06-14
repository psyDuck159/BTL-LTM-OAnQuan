package model;
 
import java.io.Serializable;
 
public class ObjectWrapper  implements Serializable{
    private static final long serialVersionUID = 20210811011L;
    public static final int LOGIN_USER = 1;
    public static final int REPLY_LOGIN_USER = 2;
    public static final int EDIT_PLAYER = 3;
    public static final int REPLY_EDIT_PLAYER = 4;
    public static final int SEARCH_PLAYER_BY_NAME = 5;
    public static final int REPLY_SEARCH_PLAYER = 6;
    public static final int SERVER_INFORM_CLIENT_NUMBER= 7;
    public static final int SERVER_INFORM_PLAYER_LOGIN = 8;
    public static final int CREATE_MATCH = 9;
    public static final int REPLY_CREATE_MATCH = 10;
    public static final int CREATE_TOURNAMENT = 11;
    public static final int REPLY_CREATE_TOURNAMENT = 12;
    public static final int SERVER_INFORM_ONLINE_FRIENDS = 13;
    public static final int GET_PLAYER_STATISTIC = 14;
    public static final int REPLY_PLAYER_STATISTIC = 15;
    public static final int GET_RANK = 16;
    public static final int REPLY_GET_RANK = 17;
    public static final int RETURN_JOINED_CLUBS = 18;
    public static final int LOGOUT_USER = 19;
    public static final int REPLY_LOGOUT_USER = 20;
    public static final int GET_CLUB_INFO = 21;
    public static final int RETURN_CLUB_INFO = 22;
    public static final int SERVER_INFROM_ONLINE_PLAYERS = 23;
    public static final int ADD_FRIEND_REQUEST = 24;
    public static final int ADD_FRIEND_RESPONSE = 25;
    public static final int GET_FRIEND_REQUESTS = 26;
    public static final int INFORM_FRIEND_REQUEST = 27;
    public static final int INFORM_FRIEND_RESPONSE = 28;
    public static final int INVITE_PLAY = 29;
    public static final int REPLY_INVITE_PLAY = 30;
    public static final int GAME_PLAY = 31;
    public static final int SEND_MOVE = 32;
    public static final int RECEIVE_MOVE = 33;
    public static final int REGISTER_USER = 34;
    public static final int REPLY_REGISTER_USER = 35;
     
    private int performative;
    private Object data;
    public ObjectWrapper() {
        super();
    }
    public ObjectWrapper(int performative, Object data) {
        super();
        this.performative = performative;
        this.data = data;
    }
    public int getPerformative() {
        return performative;
    }
    public void setPerformative(int performative) {
        this.performative = performative;
    }
    public Object getData() {
        return data;
    }
    public void setData(Object data) {
        this.data = data;
    }   
}