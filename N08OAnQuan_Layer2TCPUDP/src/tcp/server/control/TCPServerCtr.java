package tcp.server.control;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.Club;
import model.IPPortAddress;
import model.Match;
import model.ObjectWrapper;
import model.Player;
import model.PlayerStat;
import model.PlayingPlayer;
import layer2.view.Layer2Frm;
import model.Capture;
import udp.client.control.UDPClientCtr;

public class TCPServerCtr {

    private Layer2Frm view;
    private ServerSocket myServer;
    private ServerListening myListening;
    private ArrayList<ServerProcessing> myProcess;
    private IPPortAddress myAddress = new IPPortAddress("localhost", 8888); // default server host and port
    private UDPClientCtr udpClientCtr;
    
    public TCPServerCtr(Layer2Frm view) {
        myProcess = new ArrayList<ServerProcessing>();
        this.view = view;
        udpClientCtr = view.getUdpClientCtr();
        openServer();
    }

    public TCPServerCtr(Layer2Frm view, int serverPort) {
        myProcess = new ArrayList<ServerProcessing>();
        this.view = view;
        udpClientCtr = view.getUdpClientCtr();
        myAddress.setPort(serverPort);
        openServer();
    }

    private void openServer() {
        try {
            myServer = new ServerSocket(myAddress.getPort());
            myListening = new ServerListening();
            udpClientCtr = view.getUdpClientCtr();
            myListening.start();
            myAddress.setHost(InetAddress.getLocalHost().getHostAddress());
            view.showTCPServerInfo(myAddress);
            // System.out.println("server started!");
            view.showMessage("TCP server is running at the port " + myAddress.getPort() + "...");
        } catch (Exception e) {
            e.printStackTrace();
            ;
        }
    }

    public void stopServer() {
        try {
            for (ServerProcessing sp : myProcess) {
                sp.stop();
            }
            myListening.stop();
            myServer.close();
            view.showMessage("TCP server is stopped!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publicClientNumber() {
        ObjectWrapper data = new ObjectWrapper(ObjectWrapper.SERVER_INFORM_CLIENT_NUMBER, myProcess.size());
        for (ServerProcessing sp : myProcess) {
            sp.sendData(data);
        }
    }

    /**
     * The class to listen the connections from client, avoiding the blocking of
     * accept connection
     *
     */
    class ServerListening extends Thread {

        public ServerListening() {
            super();
        }

        public void run() {
            view.showMessage("server is listening... ");
            try {
                while (true) {
                    Socket clientSocket = myServer.accept();
                    ServerProcessing sp = new ServerProcessing(clientSocket);
                    sp.start();
                    myProcess.add(sp);
                    view.showMessage("Number of client connecting to the server: " + myProcess.size());
                    publicClientNumber();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * The class to treat the requirement from client
     *
     */
    class ServerProcessing extends Thread {

        private Socket mySocket;
        private Player player;

        private ObjectInputStream oisOfSp;
        private ObjectOutputStream oosOfSp;

        public ServerProcessing(Socket s) {
            super();
            mySocket = s;

            try {
                oisOfSp = new ObjectInputStream(mySocket.getInputStream());
                oosOfSp = new ObjectOutputStream(mySocket.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(TCPServerCtr.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public Player getPlayer() {
            return this.player;
        }

        public ObjectInputStream getOisOfSp() {
            return oisOfSp;
        }

        public ObjectOutputStream getOosOfSp() {
            return oosOfSp;
        }

        public void sendData(Object obj) {
            try {
                //ObjectOutputStream oos = new ObjectOutputStream(mySocket.getOutputStream());
                ObjectOutputStream oos = oosOfSp;
                oos.writeObject(obj);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                while (true) {
                    //ObjectInputStream ois = new ObjectInputStream(mySocket.getInputStream());
                    //ObjectOutputStream oos = new ObjectOutputStream(mySocket.getOutputStream());
                    ObjectOutputStream oos = oosOfSp;
                    //Object o = ois.readObject();
                    Object o = oisOfSp.readObject();
                    if (o instanceof ObjectWrapper) {
                        ObjectWrapper data = (ObjectWrapper) o;

                        switch (data.getPerformative()) {
                            case ObjectWrapper.LOGIN_USER:
                                Player player = (Player) data.getData();
                                //PlayerDAO pd = new PlayerDAO();
                                
                                udpClientCtr.sendData(data);
                                
                                ObjectWrapper receiveData = udpClientCtr.receiveData();
                                if(receiveData.getPerformative() != ObjectWrapper.REPLY_LOGIN_USER){
                                    view.showMessage("Lỗi khi check đăng nhập");
                                    break;
                                }
                                player = (Player) receiveData.getData();
                                if (player != null) {
                                    // oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_LOGIN_USER,"ok"));
                                    // TODO: get rank, clubs of this player                                    
                                    this.player = player;
                                    player.setStatus("online");
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_LOGIN_USER, player));
                                    // need to optimize
                                    ArrayList<Player> onlinePlayers = new ArrayList<>();
                                    for (ServerProcessing sp : myProcess) {
                                        if (sp.equals(this) || sp.getPlayer() == null) {
                                            continue;
                                        }
                                        onlinePlayers.add(sp.getPlayer());
                                        sp.sendData(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_PLAYER_LOGIN, player));
                                    }
                                    this.sendData(
                                            new ObjectWrapper(ObjectWrapper.SERVER_INFORM_ONLINE_FRIENDS, onlinePlayers));
                                    
//                                    PlayerStatDAO ptd = new PlayerStatDAO();
//                                    PlayerStat pst = ptd.getPlayerStat(player);
                                    udpClientCtr.sendData(new ObjectWrapper(ObjectWrapper.GET_PLAYER_STATISTIC, player));
                                    
                                    receiveData = udpClientCtr.receiveData();
                                    if(receiveData.getPerformative() != ObjectWrapper.REPLY_PLAYER_STATISTIC){
                                        view.showMessage("Lỗi khi lấy thống kê người chơi");
                                        break;
                                    }
                                    PlayerStat pst = (PlayerStat) receiveData.getData();
                                    this.sendData(
                                            new ObjectWrapper(ObjectWrapper.REPLY_PLAYER_STATISTIC, pst));
                                    
                                    //ArrayList<Club> joinedClubs = pd.getJoinedClub(player);
                                    udpClientCtr.sendData(new ObjectWrapper(ObjectWrapper.RETURN_JOINED_CLUBS, player));
                                    
                                    receiveData = udpClientCtr.receiveData();
                                    if(receiveData.getPerformative() != ObjectWrapper.RETURN_JOINED_CLUBS){
                                        view.showMessage("Lỗi khi lấy nhóm đã tham gia");
                                        break;
                                    }
                                    ArrayList<Club> joinedClubs = (ArrayList<Club>) receiveData.getData();
                                    this.sendData(
                                            new ObjectWrapper(ObjectWrapper.RETURN_JOINED_CLUBS, joinedClubs));
                                    
                                    //ArrayList<Player> friendRequests = pd.getFriendRequests(player);
                                    udpClientCtr.sendData(new ObjectWrapper(ObjectWrapper.GET_FRIEND_REQUESTS, player));
                                    
                                    receiveData = udpClientCtr.receiveData();
                                    if(receiveData.getPerformative() != ObjectWrapper.GET_FRIEND_REQUESTS) {
                                        view.showMessage("Lỗi khi lấy danh sách kết bạn");
                                        break;
                                    }
                                    ArrayList<Player> friendRequests = (ArrayList<Player>) receiveData.getData();
                                    this.sendData(
                                            new ObjectWrapper(ObjectWrapper.GET_FRIEND_REQUESTS, friendRequests));

                                } else {
                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_LOGIN_USER, "false"));
                                }
                                break;
                            case ObjectWrapper.LOGOUT_USER:
                                for (ServerProcessing sp : myProcess) {
                                    if (sp.equals(this)) {
                                        continue;
                                    }
                                    sp.sendData(new ObjectWrapper(ObjectWrapper.REPLY_LOGOUT_USER, this.getPlayer()));
                                }
                                this.player = null;
                                break;
                            case ObjectWrapper.GET_CLUB_INFO:
                                Club club = (Club) data.getData();
                                //ClubDAO cd = new ClubDAO();
                                //club = cd.getClubMember(club);
                                udpClientCtr.sendData(new ObjectWrapper(ObjectWrapper.GET_CLUB_INFO, club));
                                
                                receiveData = udpClientCtr.receiveData();
                                if(receiveData.getPerformative() != ObjectWrapper.RETURN_CLUB_INFO) {
                                    view.showMessage("Lỗi khi lấy thông tin của nhóm " + club.getName());
                                    break;
                                }
                                club = (Club) receiveData.getData();                                
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.RETURN_CLUB_INFO, club));

                                ArrayList<Player> onlinePlayers = new ArrayList<>();
                                for (ServerProcessing sp : myProcess) {
                                    if (sp.equals(this) || sp.getPlayer() == null) {
                                        continue;
                                    }
                                    onlinePlayers.add(sp.getPlayer());
                                    sp.sendData(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_PLAYER_LOGIN, this.player));
                                }
                                this.sendData(
                                        new ObjectWrapper(ObjectWrapper.SERVER_INFROM_ONLINE_PLAYERS, onlinePlayers));
                                break;
                            case ObjectWrapper.GET_RANK:
                                //PlayerStatDAO pstd = new PlayerStatDAO();
                                //ArrayList<PlayerStat> rank = pstd.getRank();
                                udpClientCtr.sendData(new ObjectWrapper(ObjectWrapper.GET_RANK, ""));
                                
                                receiveData = udpClientCtr.receiveData();
                                if(receiveData.getPerformative() != ObjectWrapper.REPLY_GET_RANK) {
                                    view.showMessage("Lỗi khi lấy bxh");
                                    break;
                                }
                                ArrayList<PlayerStat> rank = (ArrayList<PlayerStat>) receiveData.getData();
                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_GET_RANK, rank));
                                //System.out.println(rank.size());
                                onlinePlayers = new ArrayList<>();
                                for (ServerProcessing sp : myProcess) {
                                    if (sp.equals(this) || sp.getPlayer() == null) {
                                        continue;
                                    }
                                    onlinePlayers.add(sp.getPlayer());
                                    sp.sendData(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_PLAYER_LOGIN, this.player));
                                }
                                this.sendData(
                                        new ObjectWrapper(ObjectWrapper.SERVER_INFROM_ONLINE_PLAYERS, onlinePlayers));
                                break;
                            case ObjectWrapper.ADD_FRIEND_REQUEST:
                                Player[] players = (Player[]) data.getData();
                                //PlayerDAO pd = new PlayerDAO();
                                //pd.addFriend(players[0], players[1]);
                                udpClientCtr.sendData(data);
                                receiveData = udpClientCtr.receiveData();
                                if(receiveData.getPerformative() == ObjectWrapper.ADD_FRIEND_REQUEST) {
                                    boolean isAdded = (boolean) receiveData.getData();
                                    if(!isAdded){
                                        view.showMessage("Lỗi khi thêm lời mời kết bạn");
                                        break;
                                    }
                                }
                                for (ServerProcessing sp : myProcess) {
                                    if (sp.getPlayer().getId() == players[1].getId()) {
                                        sp.sendData(
                                                new ObjectWrapper(ObjectWrapper.INFORM_FRIEND_REQUEST, players[0]));
                                    }
                                }
                                break;
                            case ObjectWrapper.ADD_FRIEND_RESPONSE:
                                Object[] objs = (Object[]) data.getData();
                                String res = (String) objs[0];
                                Player p1 = (Player) objs[1];
                                Player p2 = (Player) objs[2];
                                //PlayerDAO pd = new PlayerDAO();
                                udpClientCtr.sendData(data);
                                receiveData = udpClientCtr.receiveData();
                                if(receiveData.getPerformative() == ObjectWrapper.ADD_FRIEND_RESPONSE) {
                                    boolean isResponsed= (boolean) receiveData.getData();
                                    if(!isResponsed) {
                                        view.showMessage("Lỗi khi phản hồi kết bạn");
                                        break;
                                    }
                                }
                                if (res.equals("accept")) {
                                    //pd.addFriend(p1, p2);

                                    for (ServerProcessing sp : myProcess) {
                                        if (sp.getPlayer() != null && sp.getPlayer().getId() == p2.getId()) {
                                            sp.sendData(new ObjectWrapper(ObjectWrapper.INFORM_FRIEND_RESPONSE, p1));
                                            p2.setStatus("online");
                                        }
                                    }
                                    oos.writeObject(
                                            new ObjectWrapper(ObjectWrapper.INFORM_FRIEND_RESPONSE, p2));
                                } else {
                                    //pd.addFriendDeny(p1, p2);
                                }
                                break;
                            case ObjectWrapper.INVITE_PLAY:
                                Player invitedPlayer = (Player) data.getData();

                                for (ServerProcessing sp : myProcess) {
                                    if (sp.getPlayer().equals(invitedPlayer)) {
                                        sp.sendData(new ObjectWrapper(ObjectWrapper.INVITE_PLAY, this.getPlayer()));
                                    }
                                }
                                break;
                            case ObjectWrapper.REPLY_INVITE_PLAY:
                                objs = (Object[]) data.getData();
                                res = (String) objs[0];
                                
                                if (res.equals("accept")) {
                                    Match m = (Match) objs[1];
                                     p1 = m.getPlayers()[0].getPlayer();
                                     p2 = m.getPlayers()[1].getPlayer();
                                    for (ServerProcessing sp : myProcess) {
                                        if (sp.getPlayer().equals(p2)) {
                                            sp.sendData(new ObjectWrapper(ObjectWrapper.REPLY_INVITE_PLAY, new Object[]{"accept", m}));
                                        }
                                    }
                                    
                                } else {
                                     p1 = (Player) objs[1];
                                     p2 = (Player) objs[2];
                                    for (ServerProcessing sp : myProcess) {
                                        if (sp.getPlayer().equals(p2)) {
                                            sp.sendData(new ObjectWrapper(ObjectWrapper.REPLY_INVITE_PLAY, new Object[]{"deny", p1}));
                                        }
                                    }
                                }
                                break;
                            case ObjectWrapper.REGISTER_USER:
                                udpClientCtr.sendData(data);
                                
                                receiveData = udpClientCtr.receiveData();
                                if(receiveData.getPerformative() == ObjectWrapper.REPLY_REGISTER_USER) {
                                    sendData(receiveData);
                                    Player p = (Player) receiveData.getData();
                                    this.player = p;
                                }else{
                                    view.showMessage("lỗi khi đăng kí");
                                }
                                break;
                            case ObjectWrapper.SEND_MOVE:
                                objs = (Object[]) data.getData();
                                Player p = (Player) objs[0];
                                this.sendData(new ObjectWrapper(ObjectWrapper.RECEIVE_MOVE, objs));
                                for(ServerProcessing sp : myProcess) {
                                    if(p.equals(sp.getPlayer())){
                                        sp.sendData(new ObjectWrapper(ObjectWrapper.RECEIVE_MOVE, objs));
                                    }
                                }
                                break;
                            case ObjectWrapper.CREATE_MATCH:                                
                                udpClientCtr.sendData(data);
                                udpClientCtr.receiveData();
                                objs = (Object[]) data.getData();
                                Match match = (Match) objs[0];
                                ArrayList<Capture> lc1 = (ArrayList<Capture>) objs[1];
                                ArrayList<Capture> lc2 = (ArrayList<Capture>) objs[2];
                                System.out.println("match:pp1:"+match.getPlayers()[0].getListCapture().size());
                                System.out.println("match:pp2:"+match.getPlayers()[1].getListCapture().size());
                                System.out.println("pp1:"+lc1.size());
                                System.out.println("pp2:"+lc2.size());
                                break;
//                            case ObjectWrapper.EDIT_PLAYER -> {
//                                Player pl = (Player) data.getData();
//                                PlayerDAO pd = new PlayerDAO();
//                                boolean ok = pd.editPlayer(pl);
//                                if (ok) {
//                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_EDIT_PLAYER, "ok"));
//                                } else {
//                                    oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_EDIT_PLAYER, "false"));
//                                }
//                            }
//                            case ObjectWrapper.SEARCH_PLAYER_BY_NAME -> {
//                                String key = (String) data.getData();
//                                PlayerDAO pd = new PlayerDAO();
//                                ArrayList<Player> result = pd.searchPlayer(key);
//                                oos.writeObject(new ObjectWrapper(ObjectWrapper.REPLY_SEARCH_PLAYER, result));
//                            }
                        }

                    }
                    // ois.reset();
                    // oos.reset();
                }
            } catch (EOFException | SocketException e) {
                // e.printStackTrace();

                for (ServerProcessing sp : myProcess) {
                    if (sp.equals(this)) {
                        continue;
                    }
                    if (this.getPlayer() != null) {
                        sp.sendData(new ObjectWrapper(ObjectWrapper.REPLY_LOGOUT_USER, this.getPlayer()));
                    }
                }
                myProcess.remove(this);
                view.showMessage("Number of client connecting to the server: " + myProcess.size());
                publicClientNumber();
                try {
                    mySocket.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                this.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

//    class MatchProcessing extends Thread {
//        private ServerProcessing spPlayer1, spPlayer2;
//        private Match match;
//        private ObjectInputStream fromPlayer1, fromPlayer2;
//        private ObjectOutputStream toPlayer1, toPlayer2;
//
//        public MatchProcessing(ServerProcessing spPlayer1, ServerProcessing spPlayer2) {
//            this.spPlayer1 = spPlayer1;
//            this.spPlayer2 = spPlayer2;
//            
//            match = new Match();
//            fromPlayer1 = spPlayer1.getOisOfSp();
//            toPlayer1 = spPlayer1.getOosOfSp();
//            fromPlayer2 = spPlayer2.getOisOfSp();
//            toPlayer2 = spPlayer2.getOosOfSp();
//        }
//        
//        @Override
//        public void run() {
//            
//        }
//        
//    }
}
