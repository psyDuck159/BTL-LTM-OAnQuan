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
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import model.Club;
import model.JoinedPlayer;
import model.ObjectWrapper;
import model.Player;
import tcp.client.control.TCPClientCtr;

/**
 *
 * @author Admin
 */
public class ClubFrm extends javax.swing.JFrame implements ActionListener{

    private final HomeFrm home;
    private Club club;
    private ArrayList<JButton> listFriendReq, listBtnDuel;
    private InvitePlayDialog inviteDialog;

    /**
     * Creates new form ClubFrm
     */
    public ClubFrm(HomeFrm home, Club club) {
        super("Club view");
        this.club = club;
        this.home = home;
        initComponents();
        listFriendReq = new ArrayList<>();
        listBtnDuel = new ArrayList<>();
        inviteDialog = new InvitePlayDialog(home, false);
        
        lblName.setText(club.getName());
        lblDesc.setText(club.getDescription());
        
        tblMember.setModel(new ClubTableModel());
        TableCellRenderer buttonRenderer = new JTableButtonRenderer();
        tblMember.getColumn("Duel").setCellRenderer(buttonRenderer);
        tblMember.getColumn("Friend Request").setCellRenderer(buttonRenderer);
        tblMember.addMouseListener(new JTableButtonMouseListener(tblMember));
        
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                home.setVisible(true);
                
                ArrayList<ObjectWrapper> activeFunction = home.getMyControl().getActiveFunction();
                ObjectWrapper existed = null;
                for(ObjectWrapper ow: activeFunction) {
                    if(ow.getData() instanceof ClubFrm) {
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
                .add(new ObjectWrapper(ObjectWrapper.RETURN_CLUB_INFO, this));
    }

    public void receivedDataProcessing(ObjectWrapper data) {
        switch (data.getPerformative()) {
            case ObjectWrapper.RETURN_CLUB_INFO:
                this.club = (Club) data.getData();

                for (int i = 0; i < club.getListMember().size(); i++) {
                    Player member = club.getListMember().get(i).getPlayer();
                    
                    JButton btnDuel = new JButton("Duel");
                    btnDuel.addActionListener(this);
                    listBtnDuel.add(btnDuel);
                    JButton btnAddFr = new JButton("Add Friend");
                    btnAddFr.addActionListener(this);
                    listFriendReq.add(btnAddFr);
                    if(member.getId() == home.getPlayer().getId()){                        
                        btnDuel.setEnabled(false);
                        btnAddFr.setEnabled(false);
                    }
                    if(home.getPlayer().getListFriend().indexOf(member) >= 0) {
                        btnAddFr.setEnabled(false);
                    }
                        
                }
                ((DefaultTableModel) tblMember.getModel()).fireTableDataChanged();
                break;
            case ObjectWrapper.SERVER_INFORM_PLAYER_LOGIN:
                Player member = (Player) data.getData();
//                DefaultListModel<String> model = (DefaultListModel<String>) jListFriend.getModel();
//                int index = model.indexOf(getPlayerSimpleInformation(friend));
//                model.set(index, getPlayerSimpleInformation(friend) + " (online)");
                for (int i = 0; i < club.getListMember().size(); i++) {
                    JoinedPlayer jp = club.getListMember().get(i);
                    Player pl = jp.getPlayer();
                    if (member.getId() == pl.getId()) {
                        pl.setStatus("online");
                    }
                }
                ((DefaultTableModel) tblMember.getModel()).fireTableDataChanged();
                break;
            case ObjectWrapper.SERVER_INFROM_ONLINE_PLAYERS:
                ArrayList<Player> onlineMembers = (ArrayList<Player>) data.getData();
                if (onlineMembers.isEmpty()) {
                    break;
                }

                for (int i = 0; i < club.getListMember().size(); i++) {
                    Player pl = club.getListMember().get(i).getPlayer();
                    for(int j=0; j<onlineMembers.size(); j++){
                        Player mem = onlineMembers.get(j);
                        if(pl.getId() == mem.getId()){
                            pl.setStatus("online");
                        }
                    }
                }
                ((DefaultTableModel) tblMember.getModel()).fireTableDataChanged();
                break;
            case ObjectWrapper.INFORM_FRIEND_RESPONSE:
                Player newFriend = (Player) data.getData();
                int index = club.getListMember().indexOf(newFriend);
                if(index >= 0){
                    listFriendReq.get(index).setEnabled(false);
                }
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
                Player player = home.getPlayer();
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
                for(int i=0; i<club.getListMember().size(); i++) {
                    pl = club.getListMember().get(i).getPlayer();
                    if(pl.getId() == logoutedPlayer.getId())
                        pl.setStatus("offline");
                }
                ((DefaultTableModel) tblMember.getModel()).fireTableDataChanged();
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
        jLabel2 = new javax.swing.JLabel();
        lblName = new javax.swing.JLabel();
        lblDesc = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblMember = new javax.swing.JTable();
        btnBackHome = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Name:");

        jLabel2.setText("Description:");

        lblName.setText("_name_");

        lblDesc.setText("_moTa_");

        tblMember.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "id", "ten", "nickname", "trang thai", "ket ban", "thach dau"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblMember);
        if (tblMember.getColumnModel().getColumnCount() > 0) {
            tblMember.getColumnModel().getColumn(0).setResizable(false);
            tblMember.getColumnModel().getColumn(1).setResizable(false);
            tblMember.getColumnModel().getColumn(2).setResizable(false);
            tblMember.getColumnModel().getColumn(3).setResizable(false);
            tblMember.getColumnModel().getColumn(4).setResizable(false);
            tblMember.getColumnModel().getColumn(5).setResizable(false);
        }

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblDesc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(228, 228, 228))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblName, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(btnBackHome))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 705, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(lblName))
                    .addComponent(btnBackHome))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(lblDesc))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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
    
    private String getPlayerSimpleInformation(Player pl) {
        return pl.getName() + "#" + pl.getId();
    }
    
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
    
    private void btnDuelClick(int i) {
        System.out.println("club duel" + i);
        
        Player player = home.getPlayer();
        TCPClientCtr myControl = home.getMyControl();
        Player competitor = club.getListMember().get(i).getPlayer();
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
        Player p2 = club.getListMember().get(i).getPlayer();
        home.getMyControl()
                .sendData(new ObjectWrapper(ObjectWrapper.ADD_FRIEND_REQUEST,new Player[]{p1, p2}));
    }
    class ClubTableModel extends DefaultTableModel {

        private String[] columnNames = {"id", "name", "username", "Status", "Friend Request", "Duel"};
        private final Class<?>[] columnTypes = new Class<?>[]{Integer.class,
            String.class, String.class, String.class, JButton.class, JButton.class};

        @Override
        public int getColumnCount() {
            return 6;
            //return this.columnNames.length;
        }

        @Override
        public int getRowCount() {
            return club.getListMember().size();
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
            Player member = club.getListMember().get(rowIndex).getPlayer();
            switch (columnIndex) {
                case 0:
                    return member.getId();
                case 1:
                    if(member.getId() == home.getPlayer().getId())
                        return member.getName() + " (Me)";
                    return member.getName();
                case 2:
                    return member.getUsername();
                case 3:
                    if(member.getId() == home.getPlayer().getId())
                        return "online";
                    return member.getStatus();
                case 4:
                    return listFriendReq.get(rowIndex);
                case 5:
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
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDesc;
    private javax.swing.JLabel lblName;
    private javax.swing.JTable tblMember;
    // End of variables declaration//GEN-END:variables
}
