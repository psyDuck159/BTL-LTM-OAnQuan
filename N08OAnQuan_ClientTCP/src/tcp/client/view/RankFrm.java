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
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import model.ObjectWrapper;
import model.Player;
import model.PlayerStat;
import tcp.client.control.TCPClientCtr;

/**
 *
 * @author Admin
 */
public class RankFrm extends javax.swing.JFrame implements ActionListener{
    private final HomeFrm home;
    private ArrayList<PlayerStat> listPlayer;
    private ArrayList<JButton> listFriendReq, listBtnDuel;
    private InvitePlayDialog inviteDialog;
    /**
     * Creates new form RankFrm
     */
    public RankFrm(HomeFrm home) {
        super("Rank view");
        initComponents();
        this.home = home;
        listPlayer = new ArrayList<>();
        listFriendReq = new ArrayList<>();
        listBtnDuel = new ArrayList<>();
        inviteDialog = new InvitePlayDialog(home, false);
        
        tblRank.setModel(new RankTableModel());
        TableCellRenderer buttonRenderer = new JTableButtonRenderer();
        tblRank.getColumn("Duel").setCellRenderer(buttonRenderer);
        tblRank.getColumn("Friend Request").setCellRenderer(buttonRenderer);
        tblRank.addMouseListener(new JTableButtonMouseListener(tblRank));
        
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                home.setVisible(true);
                
                ArrayList<ObjectWrapper> activeFunction = home.getMyControl().getActiveFunction();
                ObjectWrapper existed = null;
                for(ObjectWrapper ow: activeFunction) {
                    if(ow.getData() instanceof RankFrm) {
                        existed = ow;
                    }
                }
                activeFunction.remove(existed);
                dispose();
                //To change body of generated methods, choose Tools | Templates.
            }

        });
        ((TCPClientCtr) home.getMyControl())
                .getActiveFunction()
                .add(new ObjectWrapper(ObjectWrapper.REPLY_GET_RANK, this));
    }
    
    public void receivedDataProcessing(ObjectWrapper data) {
        switch(data.getPerformative()) {
            case ObjectWrapper.REPLY_GET_RANK:
                this.listPlayer = (ArrayList<PlayerStat>) data.getData();
                System.out.println(listPlayer.size());
                for (int i = 0; i < listPlayer.size(); i++) {
                    Player player = listPlayer.get(i);
                    
                    JButton btnDuel = new JButton("Duel");
                    btnDuel.addActionListener(this);
                    listBtnDuel.add(btnDuel);
                    JButton btnAddFr = new JButton("Add Friend");
                    btnAddFr.addActionListener(this);
                    listFriendReq.add(btnAddFr);
                    if(player.getId() == home.getPlayer().getId()){                        
                        btnDuel.setEnabled(false);
                        btnAddFr.setEnabled(false);
                    }
                    if(home.getPlayer().getListFriend().indexOf(player) >= 0) {
                        btnAddFr.setEnabled(false);
                    }
                        
                }
                ((DefaultTableModel) tblRank.getModel()).fireTableDataChanged();
                break;
            case ObjectWrapper.SERVER_INFORM_PLAYER_LOGIN:
                Player player = (Player) data.getData();
//                DefaultListModel<String> model = (DefaultListModel<String>) jListFriend.getModel();
//                int index = model.indexOf(getPlayerSimpleInformation(friend));
//                model.set(index, getPlayerSimpleInformation(friend) + " (online)");
                for (int i = 0; i < listPlayer.size(); i++) {
                    PlayerStat pl = listPlayer.get(i);
                    if (player.getId() == pl.getId()) {
                        pl.setStatus("online");
                    }
                }
                ((DefaultTableModel) tblRank.getModel()).fireTableDataChanged();
                break;
            case ObjectWrapper.SERVER_INFROM_ONLINE_PLAYERS:
                ArrayList<Player> onlinePlayers = (ArrayList<Player>) data.getData();
                if (onlinePlayers.isEmpty()) {
                    break;
                }

                for (int i = 0; i < listPlayer.size(); i++) {
                    PlayerStat pl = listPlayer.get(i);
                    for(int j=0; j<onlinePlayers.size(); j++){
                        Player mem = onlinePlayers.get(j);
                        if(pl.getId() == mem.getId()){
                            pl.setStatus("online");
                        }
                    }
                }
                ((DefaultTableModel) tblRank.getModel()).fireTableDataChanged();
                break;
            case ObjectWrapper.INFORM_FRIEND_RESPONSE:
                Player newFriend = (Player) data.getData();

                for (int i=0; i<listPlayer.size(); i++) {
                    PlayerStat pl = listPlayer.get(i);
                    if(pl.getId() == newFriend.getId() )
                        listFriendReq.get(i).setEnabled(false);
                }     
                ((DefaultTableModel) tblRank.getModel()).fireTableDataChanged();
                break;
            case ObjectWrapper.INVITE_PLAY:
                Player invitedPlayer = (Player) data.getData();  
                inviteDialog.setVisible(true);
                inviteDialog.addInvite(invitedPlayer);
                //inviteDialog.setVisible(true);
                break;
            case ObjectWrapper.REPLY_INVITE_PLAY:
                Object[] objs = (Object[]) data.getData();
                String res = (String) objs[0];
                Player pl = (Player) objs[1];
                player = home.getPlayer();
                String title = getPlayerSimpleInformation(player);
                String message = getPlayerSimpleInformation(pl) + " " + res + " your invitation";
                JOptionPane.showMessageDialog(rootPane, message , title, JOptionPane.INFORMATION_MESSAGE);
                break;
            case ObjectWrapper.REPLY_LOGOUT_USER:
                Player logoutedPlayer = (Player) data.getData();
                //System.out.println(logoutedPlayer.getId());
//                DefaultListModel<String> model = (DefaultListModel<String>) jListFriend.getModel();
//                int index = model.indexOf(getPlayerSimpleInformation(logoutedPlayer) + " (online)");
//                model.set(index, getPlayerSimpleInformation(logoutedPlayer));                
                for(int i=0; i<listPlayer.size(); i++) {
                    pl = listPlayer.get(i);
                    if(pl.getId() == logoutedPlayer.getId())
                        pl.setStatus("offline");
                }
                
                ((DefaultTableModel) tblRank.getModel()).fireTableDataChanged();
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

        jScrollPane1 = new javax.swing.JScrollPane();
        tblRank = new javax.swing.JTable();
        txtNameKey = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        btnBackHome = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tblRank.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "No.", "ID", "Name", "Username", "Status", "Win Rate", "Win Total", "Draw Total", "Match Total", "Score Total", "Point", "Add friend", "Duel"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, true, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblRank.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tblRank);
        if (tblRank.getColumnModel().getColumnCount() > 0) {
            tblRank.getColumnModel().getColumn(9).setResizable(false);
        }

        btnSearch.setText("Search");

        btnBackHome.setText("Back Home");
        btnBackHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackHomeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addComponent(txtNameKey, javax.swing.GroupLayout.PREFERRED_SIZE, 432, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 425, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnBackHome)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnBackHome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSearch)
                    .addComponent(txtNameKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 399, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackHomeActionPerformed
        // TODO add your handling code here:
        home.setVisible(true);
        this.dispose();
        ArrayList<ObjectWrapper> activeFunction = home.getMyControl().getActiveFunction();
        ObjectWrapper existed = null;
        for(ObjectWrapper ow: activeFunction) {
            if(ow.getData() instanceof ClubFrm) {
                existed = ow;
            }
        }
        activeFunction.remove(existed);
    }//GEN-LAST:event_btnBackHomeActionPerformed

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btnClicked = (JButton)e.getSource();

        for(int i=0; i<listBtnDuel.size(); i++)
            if(btnClicked.equals(listBtnDuel.get(i))){
                btnDuelClick(i);
                return;
            }
        for(int i=0; i<listFriendReq.size(); i++)
            if(btnClicked.equals(listFriendReq.get(i))){
                btnAddFriendClick(i);
                return;
            }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private String getPlayerSimpleInformation(Player pl) {
        return pl.getName() + "#" + pl.getId();
    }
    
    private void btnDuelClick(int i) {
        System.out.println("rank duel" + i);
        
        Player player = home.getPlayer();
        TCPClientCtr myControl = home.getMyControl();
        Player competitor = listPlayer.get(i);
        String status = competitor.getStatus();
        if (status.equals("offline") || status.equals("ingame")) {
            JOptionPane.showMessageDialog(this, getPlayerSimpleInformation(competitor) + " is " + status);
        }else {
            myControl.sendData(new ObjectWrapper(ObjectWrapper.INVITE_PLAY, competitor));
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void btnAddFriendClick(int i) {
        Player p1 = home.getPlayer();
        Player p2 = listPlayer.get(i);
        home.getMyControl()
                .sendData(new ObjectWrapper(ObjectWrapper.ADD_FRIEND_REQUEST,new Player[]{p1, p2}));
    }
    
    class RankTableModel extends DefaultTableModel {

        private String[] columnNames = {"NO.", "id",
            "name", "username", "Status",
            "Win Rate", "Win Total", "Draw Total", "Match Total",
            "Score Total", "Point", "Friend Request", "Duel"};
        private final Class<?>[] columnTypes = new Class<?>[]{Integer.class, Integer.class,
            String.class, String.class, String.class,
            Float.class, Integer.class, Integer.class, Integer.class,
            Integer.class, Integer.class, JButton.class, JButton.class};

        @Override
        public int getColumnCount() {
            return columnNames.length;
            //return this.columnNames.length;
        }

        @Override
        public int getRowCount() {
            return listPlayer.size();
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
            PlayerStat player = listPlayer.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return rowIndex + 1;
                case 1:
                    return player.getId();
                case 2:
                    if(player.getId() == home.getPlayer().getId())
                        return player.getName() + " (Me)";
                    return player.getName();
                case 3:
                    return player.getUsername();
                case 4:
                    if(player.getId() == home.getPlayer().getId())
                        return "online";
                    return player.getStatus();
                case 5:    
                    return player.getWinProb();
                case 6:
                    return player.getWinTotal();
                case 7:
                    return player.getDrawTotal();
                case 8:
                    return player.getMatchTotal();
                case 9:
                    return player.getTotalScore();
                case 10:
                    return player.getTotalPoint();
                case 11:
                    return listFriendReq.get(rowIndex);
                case 12:
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
    private javax.swing.JButton btnBackHome;
    private javax.swing.JButton btnSearch;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblRank;
    private javax.swing.JTextField txtNameKey;
    // End of variables declaration//GEN-END:variables
}
