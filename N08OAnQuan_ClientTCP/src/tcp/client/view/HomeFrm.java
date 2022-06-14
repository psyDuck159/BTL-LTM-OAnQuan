/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcp.client.view;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import model.Club;
import model.Match;
import model.ObjectWrapper;
import model.Player;
import model.PlayerStat;
import tcp.client.control.TCPClientCtr;

/**
 *
 * @author Admin
 */
public class HomeFrm extends javax.swing.JFrame implements ActionListener {

    private final TCPClientCtr myControl;
    private final Player player;
    private ArrayList<Club> listClub;
    private ArrayList<Player> listFriendRequest;
    private ArrayList<JButton> listBtnDuel;
    private InvitePlayDialog inviteDialog;
    
    /**
     * Creates new form HomeFrm
     *
     * @param clientCtr
     * @param player
     */
    public HomeFrm(TCPClientCtr clientCtr, Player player) {
        super("Home view");
        this.myControl = clientCtr;
        this.player = player;
        this.listBtnDuel = new ArrayList<>();
        
        inviteDialog = new InvitePlayDialog(this, false);
        //System.out.println(player);
        initComponents();
        initInformation();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        myControl.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.SERVER_INFORM_PLAYER_LOGIN, this));
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                myControl.sendData(new ObjectWrapper(ObjectWrapper.LOGOUT_USER, player));
                myControl.getActiveFunction().clear();
                //To change body of generated methods, choose Tools | Templates.
            }

        });
    }

    public TCPClientCtr getMyControl() {
        return myControl;
    }

    public Player getPlayer() {
        return player;
    }        

    private void initInformation() {
        lblName.setText(player.toString());
        
        tblFriend.setModel(new FriendTableModel());
        int size = player.getListFriend().size();

        TableCellRenderer buttonRenderer = new JTableButtonRenderer();
        tblFriend.getColumn("Duel").setCellRenderer(buttonRenderer);
        tblFriend.addMouseListener(new JTableButtonMouseListener(tblFriend));
        for (int i = 0; i < player.getListFriend().size(); i++) {
            JButton btn = new JButton("Duel");
            btn.addActionListener(this);
            listBtnDuel.add(btn);
        }
        ((DefaultTableModel) tblFriend.getModel()).fireTableDataChanged();
    }

    public void receivedDataProcessing(ObjectWrapper data) {
        switch (data.getPerformative()) {
            case ObjectWrapper.SERVER_INFORM_PLAYER_LOGIN:
                Player friend = (Player) data.getData();
//                DefaultListModel<String> model = (DefaultListModel<String>) jListFriend.getModel();
//                int index = model.indexOf(getPlayerSimpleInformation(friend));
//                model.set(index, getPlayerSimpleInformation(friend) + " (online)");
                for (int i = 0; i < player.getListFriend().size(); i++) {
                    Player pl = player.getListFriend().get(i);
                    if (friend.getId() == pl.getId()) {
                        pl.setStatus("online");
                    }
                }
                ((DefaultTableModel) tblFriend.getModel()).fireTableDataChanged();
                break;
            case ObjectWrapper.SERVER_INFORM_ONLINE_FRIENDS:
                ArrayList<Player> onlineFriends = (ArrayList<Player>) data.getData();
                if (onlineFriends.isEmpty()) {
                    break;
                }
//                DefaultListModel<String> model = (DefaultListModel<String>) jListFriend.getModel();
//                int index;
//                for (Player onlineFriend : onlineFriends) {
//                    index = model.indexOf(getPlayerSimpleInformation(onlineFriend));
//                    model.set(index, model.get(index) + " (online)");
//                }
                for (int i = 0; i < player.getListFriend().size(); i++) {
                    Player pl = player.getListFriend().get(i);
                    for(int j=0; j<onlineFriends.size(); j++){
                        Player fr = onlineFriends.get(j);
                        if(pl.getId() == fr.getId()){
                            pl.setStatus("online");
                        }
                    }
                }
                ((DefaultTableModel) tblFriend.getModel()).fireTableDataChanged();
                break;
            case ObjectWrapper.REPLY_PLAYER_STATISTIC:
                PlayerStat pst = (PlayerStat) data.getData();
                lblWinProb.setText(String.format("%.2f %%", pst.getWinProb()));
                lblWinTotal.setText("" + pst.getWinTotal());
                lblMatchTotal.setText("" + pst.getMatchTotal());
                break;
            case ObjectWrapper.RETURN_JOINED_CLUBS:
                this.listClub = (ArrayList<Club>) data.getData();
                ArrayList<Club> clubs = this.listClub;
                DefaultListModel<String> modelClub = new DefaultListModel<>();
                for (Club cl : clubs) {
                    modelClub.addElement(cl.getName());
                }
                jListClub.setModel(modelClub);
                break;
            case ObjectWrapper.GET_FRIEND_REQUESTS:
                this.listFriendRequest = (ArrayList<Player>) data.getData();
                DefaultListModel<String> model = new DefaultListModel<>();
                for(Player frq : listFriendRequest) {
                    model.addElement(frq.getName() + "#" + frq.getId());
                }
                jListFriendReq.setModel(model);
                break;
            case ObjectWrapper.INFORM_FRIEND_REQUEST:
                Player frq = (Player) data.getData();
                listFriendRequest.add(frq);
                ((DefaultListModel) jListFriendReq.getModel()).addElement(frq.toString());
                break;
            case ObjectWrapper.INFORM_FRIEND_RESPONSE:
                Player newFriend = (Player) data.getData();
                player.getListFriend().add(newFriend);
                JButton btnDuel = new JButton("Duel");
                btnDuel.addActionListener(this);
                listBtnDuel.add(btnDuel);
                ((DefaultTableModel) tblFriend.getModel()).fireTableDataChanged();
                break;
            case ObjectWrapper.INVITE_PLAY :
                Player invitedPlayer = (Player) data.getData();  
                
                inviteDialog.addInvite(invitedPlayer);
                if(!inviteDialog.isVisible())
                    inviteDialog.setVisible(true);
                //inviteDialog.setVisible(true);
                break;
            case ObjectWrapper.REPLY_INVITE_PLAY:
                Object[] objs = (Object[]) data.getData();
                String res = (String) objs[0];
                
                //String title = getPlayerSimpleInformation(player);
                //String message = getPlayerSimpleInformation(pl) + " " + res + " your invitation";
                //JOptionPane.showMessageDialog(rootPane, message , title, JOptionPane.INFORMATION_MESSAGE);
                if(res.equals("accept")) {
                    Match m = (Match) objs[1];
//                    MatchFrm matchDialog = new MatchFrm(this, m);
//                    matchDialog.setVisible(true);
                    HomeFrm home = this;
                    java.awt.EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            //new RegisterFrm().setVisible(true);
                            MatchFrm matchDialog = new MatchFrm(home, m);
                            matchDialog.setVisible(true);
                        }
                    });
                }else {
                    Player pl = (Player) objs[1];
                    String title = player.toString();
                    String message = pl.toString() + " " + res + " your invitation";
                    JOptionPane.showMessageDialog(rootPane, message , title, JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case ObjectWrapper.REPLY_LOGOUT_USER :
                Player logoutedPlayer = (Player) data.getData();
                //System.out.println(logoutedPlayer.getId());
//                DefaultListModel<String> model = (DefaultListModel<String>) jListFriend.getModel();
//                int index = model.indexOf(getPlayerSimpleInformation(logoutedPlayer) + " (online)");
//                model.set(index, getPlayerSimpleInformation(logoutedPlayer));  
                if(logoutedPlayer == null) break;
                for(int i=0; i<player.getListFriend().size(); i++) {
                    Player pl = player.getListFriend().get(i);
                    if(pl.getId() == logoutedPlayer.getId())
                        pl.setStatus("offline");
                }
                ((DefaultTableModel) tblFriend.getModel()).fireTableDataChanged();
                break;
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblWinProb = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblWinTotal = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblFriend = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListClub = new javax.swing.JList<>();
        jLabel5 = new javax.swing.JLabel();
        btnBXH = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jListFriendReq = new javax.swing.JList<>();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblMatchTotal = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Ten");

        lblName.setText("_name_");

        jLabel2.setText("Ti le thang");

        lblWinProb.setText("_winProb_");

        jLabel3.setText("So tran thang");

        lblWinTotal.setText("_winTotal_");

        jLabel4.setText("Danh sach ban be");

        tblFriend.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "name", "status", "duel"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tblFriend);
        if (tblFriend.getColumnModel().getColumnCount() > 0) {
            tblFriend.getColumnModel().getColumn(0).setResizable(false);
            tblFriend.getColumnModel().getColumn(1).setResizable(false);
            tblFriend.getColumnModel().getColumn(2).setResizable(false);
        }

        jListClub.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListClubMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jListClub);

        jLabel5.setText("Danh sach nhom");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 242, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 193, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        btnBXH.setText("Bang xep hang");
        btnBXH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBXHActionPerformed(evt);
            }
        });

        jListFriendReq.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jListFriendReqMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jListFriendReq);

        jLabel6.setText("Yeu cau ket ban");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jLabel7.setText("So tran dau");

        lblMatchTotal.setText("_matchTotal_");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(37, 37, 37)
                                .addComponent(lblName, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblMatchTotal)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblWinProb, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblWinTotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                        .addGap(94, 94, 94)
                        .addComponent(btnBXH))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(lblName)
                            .addComponent(btnBXH))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(lblWinProb))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(lblWinTotal))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(lblMatchTotal))
                        .addGap(47, 47, 47)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(69, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBXHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBXHActionPerformed
        // TODO add your handling code here:
        ObjectWrapper existed = null;
        for (int i=0; i<myControl.getActiveFunction().size(); i++) {
            ObjectWrapper fto = myControl.getActiveFunction().get(i);
            if(fto.getData() instanceof RankFrm){
                existed = fto;
                ((RankFrm) fto.getData()).dispose();
            }
        }
        myControl.getActiveFunction().remove(existed);
        
        RankFrm rankFrm = new RankFrm(this);
        myControl.sendData(new ObjectWrapper(ObjectWrapper.GET_RANK, ""));
        rankFrm.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_btnBXHActionPerformed

    private void jListClubMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListClubMouseClicked
        // TODO add your handling code here:
        JList list = (JList) evt.getSource();
        int index = list.locationToIndex(evt.getPoint());
        Club club = listClub.get(index);
        
        ObjectWrapper existed = null;
        for (int i=0; i<myControl.getActiveFunction().size(); i++) {
            ObjectWrapper fto = myControl.getActiveFunction().get(i);
            if(fto.getData() instanceof ClubFrm){
                existed = fto;
                ((ClubFrm) fto.getData()).dispose();
            }
        }
        myControl.getActiveFunction().remove(existed);
        ClubFrm clubFrm = new ClubFrm(this, club);
        myControl.sendData(new ObjectWrapper(ObjectWrapper.GET_CLUB_INFO, club));
        clubFrm.setVisible(true);
        this.setVisible(false);
        //System.out.println("club " + listClub.get(index).getName() +"#"+  listClub.get(index).getId());
    }//GEN-LAST:event_jListClubMouseClicked

    private void jListFriendReqMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jListFriendReqMouseClicked
        // TODO add your handling code here:
        JList list = (JList) evt.getSource();
        int index = list.locationToIndex(evt.getPoint());
        Player frq = listFriendRequest.get(index);
        String message = "Bạn muốn kết bạn với " + frq.getName() + " không?";
        int dialogResult = JOptionPane.showConfirmDialog(this, message, "", JOptionPane.YES_NO_CANCEL_OPTION);
        
        if(dialogResult == JOptionPane.YES_OPTION) {
            myControl.sendData(new ObjectWrapper(ObjectWrapper.ADD_FRIEND_RESPONSE, new Object[]{"accept", player, frq}));
            listFriendRequest.remove(index);
            ((DefaultListModel) jListFriendReq.getModel()).remove(index);
        }else if (dialogResult == JOptionPane.NO_OPTION) {
            myControl.sendData(new ObjectWrapper(ObjectWrapper.ADD_FRIEND_RESPONSE, new Object[]{"deny", player, frq}));
            listFriendRequest.remove(index);
            ((DefaultListModel) jListFriendReq.getModel()).remove(index);
        }
        
        
    }//GEN-LAST:event_jListFriendReqMouseClicked

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btnClicked = (JButton) e.getSource();
        for (int i = 0; i < listBtnDuel.size(); i++) {
            if (btnClicked.equals(listBtnDuel.get(i))) {
                btnDuelClick(i);
            }

        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void btnDuelClick(int index) {
        System.out.println("home duel" + index);
        Player friend = player.getListFriend().get(index);
        String status = friend.getStatus();
        if (status.equals("offline") || status.equals("ingame")) {
            JOptionPane.showMessageDialog(this, friend.toString() + " is " + status);
        }else {
            myControl.sendData(new ObjectWrapper(ObjectWrapper.INVITE_PLAY, friend));
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates
    }

    class FriendTableModel extends DefaultTableModel {

        private String[] columnNames = {"Name", "Status", "Duel"};
        private final Class<?>[] columnTypes = new Class<?>[]{String.class, String.class, JButton.class};

        @Override
        public int getColumnCount() {
            return 3;
            //return this.columnNames.length;
        }

        @Override
        public int getRowCount() {
            return player.getListFriend().size();
        }

        @Override
        public String getColumnName(int columnIndex) {
            return this.columnNames[columnIndex];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnTypes[columnIndex];
        }

        @Override
        public Object getValueAt(final int rowIndex, final int columnIndex) {
            /*Adding components*/
            Player fr = player.getListFriend().get(rowIndex);
            switch (columnIndex) {
                case 0:                    
                    return fr.getName() + "#" + fr.getId();
                case 1:
                    return fr.getStatus();
                case 2:
                    return listBtnDuel.get(rowIndex);
                default:
                    return "Error";
            }
        }
    }

    class JTableButtonMouseListener extends MouseAdapter {

        private final JTable table;

        public JTableButtonMouseListener(JTable table) {
            this.table = table;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            int column = table.getColumnModel().getColumnIndexAtX(e.getX()); // get the coloum of the button
            int row = e.getY() / table.getRowHeight(); //get the row of the button

            //*Checking the row or column is valid or not
            if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
                Object value = table.getValueAt(row, column);
                if (value instanceof JButton) {
                    //perform a click event
                    ((JButton) value).doClick();
                }
            }
        }
    }

    class JTableButtonRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            JButton button = (JButton) value;
            return button;
        }
    }

    /**
     * @param args the command line arguments
     */

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBXH;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JList<String> jListClub;
    private javax.swing.JList<String> jListFriendReq;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JLabel lblMatchTotal;
    private javax.swing.JLabel lblName;
    private javax.swing.JLabel lblWinProb;
    private javax.swing.JLabel lblWinTotal;
    private javax.swing.JTable tblFriend;
    // End of variables declaration//GEN-END:variables
}
