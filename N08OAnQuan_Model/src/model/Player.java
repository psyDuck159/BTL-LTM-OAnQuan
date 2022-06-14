package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {

    private int id;
    private String username;
    private String password;
    private String name;
    private String country;
    private String email;
    private String urlAvartar;
    private ArrayList<Player> listFriend;
    private String status;

    public Player() {
        super();
        status = "offline";
        listFriend = new ArrayList<>();
    }

    public Player(int id, String username, String password, String name, String country, String email, String urlAvatar) {
        super();
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.country = country;
        this.email = email;
        this.urlAvartar = urlAvatar;
        listFriend = new ArrayList<>();
        status = "offline";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrlAvartar() {
        return urlAvartar;
    }

    public void setUrlAvartar(String urlAvartar) {
        this.urlAvartar = urlAvartar;
    }

    public ArrayList<Player> getListFriend() {
        return listFriend;
    }

    public void setListFriend(ArrayList<Player> listFriend) {
        this.listFriend = listFriend;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!((obj instanceof Player) || (obj instanceof PlayerStat))) {
            return false;
        }
        final Player other = (Player) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return  name + '#' + id;
    }
    
}
