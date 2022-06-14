/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.general;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import model.Club;
import model.Player;
/**
 *
 * @author Admin
 */
public interface PlayerInterface extends Remote{
    public Player checkLogin(Player p) throws RemoteException;
    public int register(Player p) throws RemoteException;
    public boolean addFriendRequest(Player p1, Player p2) throws RemoteException;
    public boolean addFriendResponse(Player p1, Player p2) throws RemoteException;
    public boolean addFriendDeny(Player p1, Player p2) throws RemoteException;
    public ArrayList<Player> getFriendRequests(Player p) throws RemoteException;
    public ArrayList<Club> getJoinedClubs(Player p) throws RemoteException;
}
