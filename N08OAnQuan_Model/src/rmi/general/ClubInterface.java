/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rmi.general;
import java.rmi.*;
import model.Club;
/**
 *
 * @author Admin
 */
public interface ClubInterface extends Remote{
    Club getClubMember(Club club) throws RemoteException;
}
