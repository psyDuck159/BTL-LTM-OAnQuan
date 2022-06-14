/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.general;

import java.rmi.*;
import java.util.ArrayList;
import model.PlayerStat;
/**
 *
 * @author Admin
 */
public interface PlayerStatInterface extends Remote{
    public ArrayList<PlayerStat> getRank() throws RemoteException;
}
