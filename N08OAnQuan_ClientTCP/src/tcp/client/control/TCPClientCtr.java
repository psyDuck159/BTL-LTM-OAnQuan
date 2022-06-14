package tcp.client.control;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import model.Club;

import model.IPPortAddress;
import model.ObjectWrapper;
import model.Player;
import model.PlayerStat;
import tcp.client.view.LoginFrm;
import tcp.client.view.ClientMainFrm;
import tcp.client.view.ClubFrm;
import tcp.client.view.HomeFrm;
import tcp.client.view.MatchFrm;
import tcp.client.view.RankFrm;
import tcp.client.view.RegisterFrm;

public class TCPClientCtr {

    private Socket mySocket;
    private ClientMainFrm view;
    private ClientListening myListening; // thread to listen the data from the server
    private ArrayList<ObjectWrapper> myFunction; // list of active client functions
    private IPPortAddress serverAddress = new IPPortAddress("localhost", 8888); // default server host and port
    private Player player;
    
    private ObjectOutputStream oosOfClient;
    private ObjectInputStream oisOfClient;

    public TCPClientCtr(ClientMainFrm view) {
        super();
        this.view = view;
        myFunction = new ArrayList<ObjectWrapper>();
    }

    public TCPClientCtr(ClientMainFrm view, IPPortAddress serverAddr) {
        super();
        this.view = view;
        this.serverAddress = serverAddr;
        myFunction = new ArrayList<ObjectWrapper>();
    }

    public boolean openConnection() {
        try {
            mySocket = new Socket(serverAddress.getHost(), serverAddress.getPort());
            
            oosOfClient = new ObjectOutputStream(mySocket.getOutputStream());
            oisOfClient = new ObjectInputStream(mySocket.getInputStream());
            
            myListening = new ClientListening();
            myListening.start();
            view.showMessage("Connected to the server at host: " + serverAddress.getHost() + ", port: "
                    + serverAddress.getPort());
        } catch (Exception e) {
            // e.printStackTrace();
            view.showMessage("Error when connecting to the server!");
            return false;
        }
        return true;
    }

    public boolean sendData(Object obj) {
        try {
            //ObjectOutputStream oos = new ObjectOutputStream(mySocket.getOutputStream());
            ObjectOutputStream oos = oosOfClient;
            oos.writeObject(obj);

        } catch (Exception e) {
            // e.printStackTrace();
            view.showMessage("Error when sending data to the server!");
            return false;
        }
        return true;
    }

    /*
	 * public Object receiveData(){ Object result = null; try { ObjectInputStream
	 * ois = new ObjectInputStream(mySocket.getInputStream()); result =
	 * ois.readObject(); } catch (Exception e) { //e.printStackTrace();
	 * view.showMessage("Error when receiving data from the server!"); return null;
	 * } return result; }
     */
    public boolean closeConnection() {
        try {
            if (myListening != null) {
                myListening.stop();
            }
            if (mySocket != null) {
                this.sendData(new ObjectWrapper(ObjectWrapper.LOGOUT_USER, player));
                mySocket.close();
                view.showMessage("Disconnected from the server!");

            }
            myFunction.clear();
        } catch (Exception e) {
            // e.printStackTrace();
            view.showMessage("Error when disconnecting from the server!");
            return false;
        }
        return true;
    }

    public ArrayList<ObjectWrapper> getActiveFunction() {
        return myFunction;
    }

    class ClientListening extends Thread {

        public ClientListening() {
            super();
        }

        public void run() {
            try {
                while (true) {
                    //ObjectInputStream ois = new ObjectInputStream(mySocket.getInputStream());
                    ObjectInputStream ois = oisOfClient;
                    Object obj = ois.readObject();
                    if (obj instanceof ObjectWrapper) {
                        ObjectWrapper data = (ObjectWrapper) obj;

                        switch (data.getPerformative()) {
                            case ObjectWrapper.SERVER_INFORM_CLIENT_NUMBER:
                                view.showMessage("Number of client connecting to the server: " + data.getData());
                                break;
                            case ObjectWrapper.REPLY_LOGIN_USER:
                                ObjectWrapper fto = null;
                                for (ObjectWrapper ft : myFunction) {
                                    if (ft.getData() instanceof LoginFrm) {
                                        fto = ft;
                                    }
                                }
                                //player = (Player) data.getData();
                                LoginFrm loginView = (LoginFrm) fto.getData();
                                loginView.receivedDataProcessing(data);
                                break; 
                            case ObjectWrapper.REPLY_REGISTER_USER:  
                                fto = null;
                                for (ObjectWrapper ft : myFunction) {
                                    if (ft.getData() instanceof RegisterFrm) {
                                        fto = ft;
                                    }
                                }
                                //player = (Player) data.getData();
                                RegisterFrm registerFrm = (RegisterFrm) fto.getData();
                                registerFrm.receivedDataProcessing(data);
                                break; 
                            case ObjectWrapper.RECEIVE_MOVE:
                                fto = null;
                                for (ObjectWrapper ft : myFunction) {
                                    if (ft.getData() instanceof MatchFrm) {
                                        fto = ft;
                                    }
                                }
                                //player = (Player) data.getData();
                                MatchFrm matchFrm = (MatchFrm) fto.getData();
                                matchFrm.receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_INFORM_ONLINE_FRIENDS:
                            case ObjectWrapper.REPLY_PLAYER_STATISTIC:
                            case ObjectWrapper.RETURN_JOINED_CLUBS:
                            case ObjectWrapper.GET_FRIEND_REQUESTS:
                            case ObjectWrapper.INFORM_FRIEND_REQUEST:
                            case ObjectWrapper.INVITE_PLAY:
                            case ObjectWrapper.REPLY_INVITE_PLAY:
                                fto = null;
                                for (ObjectWrapper ft : myFunction) {
                                    if (ft.getData() instanceof HomeFrm) {
                                        fto = ft;
                                    }
                                }
                                if (fto != null) {
                                    HomeFrm home = (HomeFrm) fto.getData();
                                    home.receivedDataProcessing(data);
                                }
                                
                                break;
                            case ObjectWrapper.RETURN_CLUB_INFO:                            
                                fto = null;
                                for (ObjectWrapper ft : myFunction) {
                                    if (ft.getData() instanceof ClubFrm) {
                                        fto = ft;
                                    }
                                }
                                if (fto == null) {
                                    break;
                                }
                                ClubFrm clubFrm = (ClubFrm) fto.getData();
                                clubFrm.receivedDataProcessing(data);                               
                                break;
                            
                            case ObjectWrapper.INFORM_FRIEND_RESPONSE:
                            case ObjectWrapper.SERVER_INFORM_PLAYER_LOGIN:
                            case ObjectWrapper.REPLY_LOGOUT_USER:                            
                                fto = null;
                                ObjectWrapper ftoClub = null;
                                ObjectWrapper ftoRank = null;
                                for (ObjectWrapper ft : myFunction) {
                                    if (ft.getData() instanceof HomeFrm) {
                                        fto = ft;
                                    }else if (ft.getData() instanceof ClubFrm){
                                        ftoClub = ft;
                                    }else if (ft.getData() instanceof RankFrm) {
                                        ftoRank = ft;
                                    }
                                }
                                if (fto != null){                                                                   
                                    HomeFrm home = (HomeFrm) fto.getData();
                                    home.receivedDataProcessing(data);
                                }
                                if (ftoClub != null){                                    
                                    clubFrm = (ClubFrm) ftoClub.getData();
                                    clubFrm.receivedDataProcessing(data);
                                }
                                if (ftoRank == null)
                                    break;                               
                                RankFrm rankFrm = (RankFrm) ftoRank.getData();
                                rankFrm.receivedDataProcessing(data);
                                break;
                            case ObjectWrapper.SERVER_INFROM_ONLINE_PLAYERS:                                
                                ftoRank = null;
                                ftoClub = null;
                                for (ObjectWrapper ft : myFunction) {
                                    if (ft.getData() instanceof RankFrm) {
                                        ftoRank = ft;
                                    }else if (ft.getData() instanceof ClubFrm){
                                        ftoClub = ft;
                                    }
                                }
                                if (ftoRank != null){                                                                   
                                    rankFrm = (RankFrm) ftoRank.getData();
                                    rankFrm.receivedDataProcessing(data);
                                }
                                if (ftoClub != null){
                                    clubFrm = (ClubFrm) ftoClub.getData();
                                    clubFrm.receivedDataProcessing(data);
                                }
                                break;
                                
                            case ObjectWrapper.REPLY_GET_RANK:
                                ftoRank = null;
                                for (ObjectWrapper ft : myFunction) {
                                    if (ft.getData() instanceof RankFrm) {
                                        ftoRank = ft;
                                    }
                                }
                                if (ftoRank != null){                              
                                    rankFrm = (RankFrm) ftoRank.getData();                                
                                    rankFrm.receivedDataProcessing(data);                                
                                }
                                break;
                            default:
                                for (ObjectWrapper ft : myFunction) {
                                    if (ft.getPerformative() == data.getPerformative()) {
                                        switch (data.getPerformative()) {

                                        }
                                    }
                                }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                view.showMessage("Error when receiving data from the server!");
                view.resetClient();
            }
        }
    }
}
