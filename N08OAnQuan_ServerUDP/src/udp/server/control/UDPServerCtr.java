package udp.server.control;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import jdbc.dao.ClubDAO;
import jdbc.dao.MatchDAO;

import jdbc.dao.PlayerDAO;
import jdbc.dao.PlayerStatDAO;
import model.Capture;
import model.Club;
import model.IPPortAddress;
import model.Match;
import model.ObjectWrapper;
import model.Player;
import model.PlayerStat;
import model.PlayingPlayer;
import model.Round;
import udp.server.view.ServerMainFrm;

public class UDPServerCtr {

    private ServerMainFrm view;
    private DatagramSocket myServer;
    private IPPortAddress myAddress = new IPPortAddress("localhost", 5555); //default server address
    private UDPListening myListening;

    public UDPServerCtr(ServerMainFrm view) {
        this.view = view;
    }

    public UDPServerCtr(ServerMainFrm view, int port) {
        this.view = view;
        myAddress.setPort(port);
    }

    public boolean open() {
        try {
            myServer = new DatagramSocket(myAddress.getPort());
            myAddress.setHost(InetAddress.getLocalHost().getHostAddress());
            view.showServerInfo(myAddress);
            myListening = new UDPListening();
            myListening.start();
            view.showMessage("UDP server is running at the host: " + myAddress.getHost() + ", port: " + myAddress.getPort());
        } catch (Exception e) {
            e.printStackTrace();
            view.showMessage("Error to open the datagram socket!");
            return false;
        }
        return true;
    }

    public boolean close() {
        try {
            myListening.stop();
            myServer.close();
        } catch (Exception e) {
            e.printStackTrace();
            view.showMessage("Error to close the datagram socket!");
            return false;
        }
        return true;
    }

    class UDPListening extends Thread {

        public UDPListening() {

        }

        public void run() {
            while (true) {
                try {
                    //prepare the buffer and fetch the received data into the buffer
                    byte[] receiveData = new byte[10240];
                    DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                    myServer.receive(receivePacket);

                    //read incoming data from the buffer 
                    ByteArrayInputStream bais = new ByteArrayInputStream(receiveData);
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    ObjectWrapper receivedData = (ObjectWrapper) ois.readObject();

                    //processing
                    ObjectWrapper resultData = new ObjectWrapper();
                    switch (receivedData.getPerformative()) {
                        case ObjectWrapper.LOGIN_USER:
                            // login
                            Player p = (Player) receivedData.getData();
                            resultData.setPerformative(ObjectWrapper.REPLY_LOGIN_USER);
                            PlayerDAO pd = new PlayerDAO();
                            if (pd.checkLogin(p)) {
                                resultData.setData(p);
                            } else {
                                resultData.setData(null);
                            }
                            break;
                        case ObjectWrapper.REGISTER_USER:
                            p = (Player) receivedData.getData();
                            pd = new PlayerDAO();
                            String result = pd.addPlayer(p);
                            resultData.setPerformative(ObjectWrapper.REPLY_REGISTER_USER);
                            if (result.equals("ok")) {
                                resultData.setData(p);
                            } else {
                                resultData.setData(result);
                            }
                            break;
                        case ObjectWrapper.GET_RANK:
                            PlayerStatDAO pds = new PlayerStatDAO();
                            ArrayList<PlayerStat> rank = pds.getRank();
                            resultData.setPerformative(ObjectWrapper.REPLY_GET_RANK);
                            resultData.setData(rank);
                            break;
                        case ObjectWrapper.GET_FRIEND_REQUESTS:
                            p = (Player) receivedData.getData();
                            pd = new PlayerDAO();
                            ArrayList<Player> listF = pd.getFriendRequests(p);
                            resultData.setPerformative(ObjectWrapper.GET_FRIEND_REQUESTS);
                            resultData.setData(listF);
                            break;
                        case ObjectWrapper.GET_PLAYER_STATISTIC:
                            p = (Player) receivedData.getData();
                            PlayerStatDAO ptd = new PlayerStatDAO();
                            PlayerStat pst = ptd.getPlayerStat(p);
                            resultData.setPerformative(ObjectWrapper.REPLY_PLAYER_STATISTIC);
                            resultData.setData(pst);
                            break;
                        case ObjectWrapper.RETURN_JOINED_CLUBS:
                            p = (Player) receivedData.getData();
                            pd = new PlayerDAO();
                            ArrayList<Club> joinedClubs = pd.getJoinedClub(p);
                            resultData.setPerformative(ObjectWrapper.RETURN_JOINED_CLUBS);
                            resultData.setData(joinedClubs);
                            break;
                        case ObjectWrapper.GET_CLUB_INFO:
                            Club club = (Club) receivedData.getData();
                            ClubDAO cd = new ClubDAO();
                            club = cd.getClubMember(club);
                            resultData.setPerformative(ObjectWrapper.RETURN_CLUB_INFO);
                            resultData.setData(club);
                            break;
                        case ObjectWrapper.ADD_FRIEND_REQUEST:
                            Player[] players = (Player[]) receivedData.getData();
                            pd = new PlayerDAO();
                            boolean addFriend = pd.addFriend(players[0], players[1]);
                            resultData.setPerformative(ObjectWrapper.ADD_FRIEND_REQUEST);
                            resultData.setData(addFriend);
                            break;
                        case ObjectWrapper.ADD_FRIEND_RESPONSE:
                            Object[] objs = (Object[]) receivedData.getData();
                            String res = (String) objs[0];
                            Player p1 = (Player) objs[1];
                            Player p2 = (Player) objs[2];
                            pd = new PlayerDAO();
                            boolean resA;
                            if (res.equals("accept")) {
                                resA = pd.addFriend(p1, p2);
                            } else {
                                resA = pd.addFriendDeny(p1, p2);
                            }
                            resultData.setPerformative(ObjectWrapper.ADD_FRIEND_RESPONSE);
                            resultData.setData(resA);
                            break;
                        case ObjectWrapper.CREATE_MATCH:
                            objs = (Object[]) receivedData.getData();
                            Match match = (Match) objs[0];

                            ArrayList<Capture> lc1 = (ArrayList<Capture>) objs[1];
                            ArrayList<Capture> lc2 = (ArrayList<Capture>) objs[2];
                            match.getPlayers()[0].setListCapture(lc1);
                            match.getPlayers()[1].setListCapture(lc2);
                            MatchDAO matchDAO = new MatchDAO();
                            
                            boolean addMatch = matchDAO.addMatch(match);
                            System.out.println(addMatch);
                            resultData.setPerformative(ObjectWrapper.REPLY_CREATE_MATCH);
                            resultData.setData(addMatch);
                            break;
                        /*
                            case ObjectWrapper.SEARCH_CUSTOMER_BY_NAME: // search customer by name
                            String key = (String)receivedData.getData();
                            resultData.setPerformative(ObjectWrapper.REPLY_SEARCH_CUSTOMER);
                            resultData.setData(new CustomerDAO().searchCustomer(key));
                            break;
                            case ObjectWrapper.EDIT_CUSTOMER:           // edit customer
                            Customer cus = (Customer)receivedData.getData();
                            resultData.setPerformative(ObjectWrapper.REPLY_EDIT_CUSTOMER);
                            if(new CustomerDAO().editCustomer(cus))
                            resultData.setData("ok");
                            else
                            resultData.setData("false");
                            break;
                         */

 /*
                            case ObjectWrapper.SEARCH_CUSTOMER_BY_NAME: // search customer by name
                            String key = (String)receivedData.getData();
                            resultData.setPerformative(ObjectWrapper.REPLY_SEARCH_CUSTOMER);
                            resultData.setData(new CustomerDAO().searchCustomer(key));
                            break;
                            case ObjectWrapper.EDIT_CUSTOMER:           // edit customer
                            Customer cus = (Customer)receivedData.getData();
                            resultData.setPerformative(ObjectWrapper.REPLY_EDIT_CUSTOMER);
                            if(new CustomerDAO().editCustomer(cus))
                            resultData.setData("ok");
                            else
                            resultData.setData("false");
                            break;
                         */
                    }

                    //prepare the buffer and write the data to send into the buffer
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(resultData);
                    oos.flush();

                    //create data package and send
                    byte[] sendData = baos.toByteArray();
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
                    myServer.send(sendPacket);
                } catch (Exception e) {
                    e.printStackTrace();
                    view.showMessage("Error when processing an incoming package");
                }
            }
        }
    }
}
