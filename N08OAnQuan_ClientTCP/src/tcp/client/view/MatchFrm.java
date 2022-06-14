/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tcp.client.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import model.Board;
import model.Capture;
import model.Match;
import model.ObjectWrapper;
import model.Player;
import model.PlayingPlayer;
import model.Square;
import tcp.client.control.TCPClientCtr;

/**
 *
 * @author Admin
 */
public class MatchFrm extends javax.swing.JFrame implements ActionListener {

    private JButton[] btnO;
    private Match match;
    private Timer timer;
    private int selectedBtnRuong = -1;
    private int turn = 1;
    private boolean myTurn = true;
    private HomeFrm home;
    private Player competitor;
    private int delay = 555;
    private int waitTime = 10;
    private JLabel lblScore1, lblScore2;

    /**
     * Creates new form MatchDialog
     */
    public MatchFrm(java.awt.Frame parent, Match m) {
        initComponents();
        this.match = m;
        home = (HomeFrm) parent;
        Board board = m.getBoard();
        btnO = new JButton[12];

        Player player = home.getPlayer();
        this.setTitle(player.toString());
        TCPClientCtr myControl = home.getMyControl();
        myControl.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.GAME_PLAY, this));

        Player p1 = m.getPlayers()[0].getPlayer();
        Player p2 = m.getPlayers()[1].getPlayer();

        lblTurnNumber.setText("" + turn);
        if (player.equals(p1)) {
            lblScore1 = lblMyScore;
            competitor = p2;
            lblScore2 = lblCompetitorScore;

            lblTurn.setText(player.toString());

            btnO[0] = btnO6;
            btnO[1] = btnO7;
            btnO[2] = btnO8;
            btnO[3] = btnO9;
            btnO[4] = btnO10;
            btnO[5] = btnO11;
            btnO[6] = btnO0;
            btnO[7] = btnO1;
            btnO[8] = btnO2;
            btnO[9] = btnO3;
            btnO[10] = btnO4;
            btnO[11] = btnO5;
            /* this player is botton, competitor is top
                 0 1 2 3 4        6 7 8 9 10 
             11___________5 --> 5____________11
                10 9 8 7 6        4 3 2 1 0
             */
            for (int i = 0; i <= 4; i++) {
                btnO[i].addActionListener(this);
            }
        } else {

            lblScore2 = lblMyScore;
            competitor = p1;
            lblScore1 = lblCompetitorScore;

            myTurn = false;
            lblTurn.setText(competitor.toString());

            btnO[0] = btnO0;
            btnO[1] = btnO1;
            btnO[2] = btnO2;
            btnO[3] = btnO3;
            btnO[4] = btnO4;
            btnO[5] = btnO5;
            btnO[6] = btnO6;
            btnO[7] = btnO7;
            btnO[8] = btnO8;
            btnO[9] = btnO9;
            btnO[10] = btnO10;
            btnO[11] = btnO11;
            /*
                
             */
            for (int i = 6; i <= 10; i++) {
                btnO[i].addActionListener(this);
            }
        }
        for (int i = 0; i <= 11; i++) {
            btnO[i].setText("" + board.getO()[i].getCount());
            btnO[i].setBackground(new Color(240, 240, 240, 255));
        }
        lblCompetitorName.setText(competitor.toString());
        lblCompetitorScore.setText("0");
        lblMyName.setText(player.toString());
        lblMyScore.setText("0");

        lblTime.setText("" + waitTime);
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lblTime.setText(next(lblTime));
                if (lblTime.getText().equals("0")) {
                    timer.stop();
                }
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            private String next(JLabel lblTime) {
                int curTime = Integer.parseInt(lblTime.getText());
                return (curTime - 1) + "";
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        timer.start();
    }

    public void receivedDataProcessing(ObjectWrapper data) {
        switch (data.getPerformative()) {
            case ObjectWrapper.RECEIVE_MOVE:
                Object[] objs = (Object[]) data.getData();
                Player playerTurn = (Player) objs[0];
                int selected = (int) objs[1];
                String direction = (String) objs[2];
                PlayingPlayer pp1 = match.getPlayers()[0];
                Player p1 = pp1.getPlayer();
                PlayingPlayer pp2 = match.getPlayers()[1];
                Player p2 = pp2.getPlayer();
                try {
                    timer.stop();
                    int diem = boc(selected, direction);

                    turn++;
                    lblTurnNumber.setText(turn + "");
                    if (home.getPlayer().equals(playerTurn)) {
                        myTurn = true;
                    } else {
                        myTurn = false;
                    }

                    lblTime.setText("" + waitTime);
                    timer.start();
                    if (myTurn) {
                        lblTurn.setText(home.getPlayer().toString());
                    } else {
                        lblTurn.setText(competitor.toString());
                    }
                    Board board = match.getBoard();
                    Square[] o = board.getO();

                    if (diem > 0) {
                        // set lại điểm đã ăn
                        if (playerTurn.equals(p1)) {
                            int total = diem + pp2.getTotalScore();

                            pp2.setTotalScore(total);
                            Capture capture = new Capture();
                            capture.setCount(diem);
                            pp2.getListCapture().add(capture);
                            lblScore2.setText("" + total);
                            System.out.println("cap of " + playerTurn.toString() + ":" + pp2.getListCapture());
                        } else {
                            int total = diem + pp1.getTotalScore();

                            pp1.setTotalScore(total);
                            Capture capture = new Capture();
                            capture.setCount(diem);
                            pp1.getListCapture().add(capture);
                            lblScore1.setText("" + total);
                            System.out.println("cap of " + playerTurn.toString() + ":" + pp1.getListCapture());
                        }

                        if (isEnd()) {
                            gameover();
                        }
                    }

                    if (isAllEmpty(playerTurn)) {// check xem ô của người tới lượt có bị trống hết ko
                        if (playerTurn.equals(p1)) {
                            for (int i = 0; i <= 4; i++) {
                                o[i].setCount(1);
                                btnO[i].setBackground(Color.MAGENTA);
                                btnO[i].setText("" + o[i].getCount());
                                Thread.sleep(delay);
                            }
                            for (int i = 0; i <= 4; i++) {
                                btnO[i].setBackground(new Color(240, 240, 240));
                            }
                            Capture cap = new Capture();
                            cap.setCount(-5);
                            pp1.getListCapture().add(cap);
                            pp1.setTotalScore(pp1.getTotalScore() - 5);
                            lblScore1.setText(pp1.getTotalScore() + "");
                        } else {
                            for (int i = 6; i <= 10; i++) {
                                o[i].setCount(1);
                                btnO[i].setBackground(Color.MAGENTA);
                                btnO[i].setText("" + o[i].getCount());
                                Thread.sleep(delay);
                            }
                            for (int i = 6; i <= 10; i++) {
                                btnO[i].setBackground(new Color(240, 240, 240));
                            }
                            Capture cap = new Capture();
                            cap.setCount(-5);
                            pp2.getListCapture().add(cap);
                            pp2.setTotalScore(pp2.getTotalScore() - 5);
                            lblScore2.setText(pp2.getTotalScore() + "");
                        }
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(MatchFrm.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;

        }
    }

    private void gameover() {
        PlayingPlayer pp1 = match.getPlayers()[0];
        Player p1 = pp1.getPlayer();
        PlayingPlayer pp2 = match.getPlayers()[1];
        Player p2 = pp2.getPlayer();

        Square[] o = match.getBoard().getO();
        int count1 = 0;
        for (int i = 0; i <= 4; i++) {// gom dân của người 1
            count1 += o[i].getCount();
            o[i].setCount(0);
            btnO[i].setText("0");
        }
        int count2 = 0;
        for (int i = 6; i <= 10; i++) {//gom dân của người 2
            count2 += o[i].getCount();
            o[i].setCount(0);
            btnO[i].setText("0");
        }
        Capture cap1 = new Capture();
        cap1.setCount(count1);
        pp1.getListCapture().add(cap1);
        pp1.setTotalScore(count1 + pp1.getTotalScore());
        lblScore1.setText(pp1.getTotalScore() + "");

        Capture cap2 = new Capture();
        cap2.setCount(count2);
        pp2.getListCapture().add(cap2);
        pp2.setTotalScore(count2 + pp2.getTotalScore());
        lblScore2.setText(pp2.getTotalScore() + "");

        if (home.getPlayer().equals(p1)) {// lưu trận đấu vào csdl  
            ArrayList<Capture> fakeLc1 = new ArrayList<>();
            for (int i = 0; i < pp1.getListCapture().size(); i++) {
                Capture fakeCap = new Capture();
                fakeCap.setCount(pp1.getListCapture().get(i).getCount());
                fakeLc1.add(fakeCap);
            }

            ArrayList<Capture> fakeLc2 = new ArrayList<>();
            for (int i = 0; i < pp2.getListCapture().size(); i++) {
                Capture fakeCap = new Capture();
                fakeCap.setCount(pp2.getListCapture().get(i).getCount());
                fakeLc2.add(fakeCap);
            }

            home.getMyControl().sendData(new ObjectWrapper(ObjectWrapper.CREATE_MATCH, new Object[]{match, fakeLc1, fakeLc2}));
            System.out.println(pp1.getListCapture().size());
            System.out.println(pp2.getListCapture().size());
        }
        if (pp1.getTotalScore() > pp2.getTotalScore()) {
            if (home.getPlayer().equals(p1)) {
                JOptionPane.showMessageDialog(rootPane, "YOU WIN");
            } else {
                JOptionPane.showMessageDialog(rootPane, "YOU LOSE");
            }
        } else if (pp1.getTotalScore() < pp2.getTotalScore()) {
            if (home.getPlayer().equals(p1)) {
                JOptionPane.showMessageDialog(rootPane, "YOU LOSE");
            } else {
                JOptionPane.showMessageDialog(rootPane, "YOU WIN");
            }
        } else {
            JOptionPane.showMessageDialog(rootPane, "DRAW");
        }
        this.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnO0 = new javax.swing.JButton();
        btnO1 = new javax.swing.JButton();
        btnO2 = new javax.swing.JButton();
        btnO3 = new javax.swing.JButton();
        btnO4 = new javax.swing.JButton();
        btnO10 = new javax.swing.JButton();
        btnO9 = new javax.swing.JButton();
        btnO8 = new javax.swing.JButton();
        btnO7 = new javax.swing.JButton();
        btnO6 = new javax.swing.JButton();
        btnO11 = new javax.swing.JButton();
        btnO5 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lblCompetitorName = new javax.swing.JLabel();
        lblCompetitorScore = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblMyName = new javax.swing.JLabel();
        lblMyScore = new javax.swing.JLabel();
        btnLeft = new javax.swing.JButton();
        btnRight = new javax.swing.JButton();
        lblAmount = new javax.swing.JLabel();
        lblAnDiem = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lblTurn = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblTime = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        lblTurnNumber = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        btnO0.setText("0");

        btnO1.setText("1");

        btnO2.setText("2");

        btnO3.setText("3");

        btnO4.setText("4");

        btnO10.setText("10");

        btnO9.setText("9");

        btnO8.setText("8");

        btnO7.setText("7");

        btnO6.setText("6");

        btnO11.setText("11");
        btnO11.setFocusable(false);

        btnO5.setText("5");
        btnO5.setFocusable(false);

        jLabel1.setText("Conpetitor");

        jLabel2.setText("Điểm:");

        lblCompetitorName.setText("_name1_");

        lblCompetitorScore.setText("_score1_");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblCompetitorScore)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(lblCompetitorName, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblCompetitorName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(lblCompetitorScore))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        jLabel4.setText("Player:");

        jLabel5.setText("Điểm: ");

        lblMyName.setText("_name2_");

        lblMyScore.setText("_score2_");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblMyName, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(lblMyScore)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblMyName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblMyScore))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        btnLeft.setText("<--");
        btnLeft.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLeftActionPerformed(evt);
            }
        });

        btnRight.setText("-->");
        btnRight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRightActionPerformed(evt);
            }
        });

        lblAmount.setText("_amount_");

        lblAnDiem.setText("_anDiem_");

        jLabel3.setText("Turn:");

        lblTurn.setText("_player_");

        jLabel7.setText("Time:");

        lblTime.setText("_time_");

        jLabel6.setText("Amount:");

        jLabel8.setText("Capture:");

        jLabel9.setText("Lượt:");

        lblTurnNumber.setText("_turn_");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(91, 91, 91)
                                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTurnNumber, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(112, 112, 112)
                        .addComponent(btnO11, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnO10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnO0, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnO1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnO9, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnO2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnO8, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnO3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnO7, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnO4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnO6, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnO5, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(600, 600, 600)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTime, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblAmount, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblAnDiem, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel8)))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTurn, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(288, 288, 288)
                .addComponent(btnLeft)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnRight)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(lblTurn)
                            .addComponent(jLabel9)
                            .addComponent(lblTurnNumber))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(lblTime))))
                .addGap(75, 75, 75)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblAmount)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblAnDiem))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(btnO1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnO2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnO3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnO4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(6, 6, 6)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(btnO6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnO7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnO8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnO9, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addComponent(btnO0, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnO10, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(btnO5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnO11, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnLeft)
                    .addComponent(btnRight))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLeftActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLeftActionPerformed
        // TODO add your handling code here:
        Board board = match.getBoard();
        if (selectedBtnRuong < 0) {
            JOptionPane.showMessageDialog(rootPane, "chọn ô trước!!!");
            return;
        }
        int amount = board.getO()[selectedBtnRuong].getCount();
        if (amount == 0) {
            return;
        }
        if (myTurn) {
            try {
                TCPClientCtr myControl = home.getMyControl();
                myControl.sendData(new ObjectWrapper(ObjectWrapper.SEND_MOVE, new Object[]{competitor, selectedBtnRuong, "clockwise"}));
                timer.stop();

                myTurn = false;
            } catch (Exception ex) {
                Logger.getLogger(MatchFrm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnLeftActionPerformed

    private void btnRightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRightActionPerformed
        // TODO add your handling code here:
        Board board = match.getBoard();
        if (selectedBtnRuong < 0) {
            JOptionPane.showMessageDialog(rootPane, "chọn ô trước!!!");
            return;
        }
        int amount = board.getO()[selectedBtnRuong].getCount();

        if (amount == 0) {
            return;
        }

        if (myTurn) {
            try {
                TCPClientCtr myControl = home.getMyControl();
                myControl.sendData(new ObjectWrapper(ObjectWrapper.SEND_MOVE, new Object[]{competitor, selectedBtnRuong, "counter-clockwise"}));
                timer.stop();

                myTurn = false;
            } catch (Exception ex) {
                Logger.getLogger(MatchFrm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_btnRightActionPerformed

    private int boc(int selectedBbox, String direction) throws InterruptedException {
        Board board = match.getBoard();
        int diem = 0;
        int amount = board.getO()[selectedBbox].getCount();

        btnO[selectedBbox].setText("0");
        board.getO()[selectedBbox].setCount(0);
        lblAmount.setText("" + amount);
        Thread.sleep(delay);

        boolean stop = false;
        int curPlace = selectedBbox;
        while (!stop) {
            for (int i = amount; i > 0; i--) {// rải hết đá của ô vừa bốc

                lblAmount.setText("" + (i - 1));
                btnO[curPlace].setBackground(new Color(240, 240, 240));
                curPlace = nextPlace(curPlace, direction);

                Square curO = board.getO()[curPlace];

                curO.setCount(curO.getCount() + 1);
                btnO[curPlace].setText("" + curO.getCount());
                btnO[curPlace].setBackground(Color.CYAN);
                Thread.sleep(delay);
            }
            btnO[curPlace].setBackground(new Color(240, 240, 240));
            curPlace = nextPlace(curPlace, direction);// xét ô kế tiếp
            amount = board.getO()[curPlace].getCount();
            btnO[curPlace].setBackground(Color.CYAN);
            if (amount == 0) {
                diem = an(curPlace, direction);
                stop = true;
            } else {
                if (curPlace == 5 || curPlace == 11) {//vào ô quan
                    btnO[curPlace].setBackground(new Color(240, 240, 240));
                    stop = true;
                } else {//bốc và rải tiếp
                    btnO[curPlace].setText("" + 0);
                    board.getO()[curPlace].setCount(0);
                    lblAmount.setText("" + amount);
                }
            }
        }
        lblAmount.setText("0");
        lblAnDiem.setText("0");
        selectedBtnRuong = -1;
        return diem;
    }

    private int an(int curPlace, String direction) throws InterruptedException {
        Board board = match.getBoard();
        int diem = 0;
        lblAnDiem.setText("" + diem);
        boolean stop = false;
        while (!stop) {
            btnO[curPlace].setBackground(new Color(240, 240, 240));
            curPlace = nextPlace(curPlace, direction);
            Square curO = board.getO()[curPlace];
            btnO[curPlace].setBackground(Color.CYAN);

            if (curO.getCount() > 0) {
                diem += curO.getCount();
                lblAnDiem.setText("" + diem);
                curO.setCount(0);
                btnO[curPlace].setText("0");
                btnO[curPlace].setBackground(new Color(240, 240, 240));

                curPlace = nextPlace(curPlace, direction);
                curO = board.getO()[curPlace];
                btnO[curPlace].setBackground(Color.CYAN);
                if (curO.getCount() != 0) {
                    stop = true;
                }
            } else// 2 ô 0 liên tiếp
            {
                stop = true;
            }
            Thread.sleep(delay);
        }
        btnO[curPlace].setBackground(new Color(240, 240, 240));

        return diem;
    }

    private int nextPlace(int curPlace, String direction) {
        if (direction.equals("clockwise")) {
            return curPlace == 11 ? 0 : curPlace + 1;
        } else {
            return curPlace == 0 ? 11 : curPlace - 1;
        }
    }

    private boolean isAllEmpty(Player playerTurn) {
        Player p1 = match.getPlayers()[0].getPlayer();
        Player player = home.getPlayer();

        Square[] o = match.getBoard().getO();
        if (playerTurn.equals(p1)) {
            for (int i = 0; i <= 4; i++) {
                if (o[i].getCount() > 0) {
                    return false;
                }
            }
        } else {
            for (int i = 6; i <= 10; i++) {
                if (o[i].getCount() > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isEnd() {
        Board board = this.match.getBoard();
        return (board.getO()[5].getCount() <= 0
                && board.getO()[11].getCount() <= 0);
    }
    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) {
//        Player p1 = new Player();
//        Player p2 = new Player();
//        Match match = new Match(p1, p2);
//        ClientCtr clientCtr = new ClientCtr
//        HomeFrm  home= new HomeFrm(clientCtr, p2);
//        MatchDialog md = new MatchDialog(home, false, match);
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLeft;
    private javax.swing.JButton btnO0;
    private javax.swing.JButton btnO1;
    private javax.swing.JButton btnO10;
    private javax.swing.JButton btnO11;
    private javax.swing.JButton btnO2;
    private javax.swing.JButton btnO3;
    private javax.swing.JButton btnO4;
    private javax.swing.JButton btnO5;
    private javax.swing.JButton btnO6;
    private javax.swing.JButton btnO7;
    private javax.swing.JButton btnO8;
    private javax.swing.JButton btnO9;
    private javax.swing.JButton btnRight;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lblAmount;
    private javax.swing.JLabel lblAnDiem;
    private javax.swing.JLabel lblCompetitorName;
    private javax.swing.JLabel lblCompetitorScore;
    private javax.swing.JLabel lblMyName;
    private javax.swing.JLabel lblMyScore;
    private javax.swing.JLabel lblTime;
    private javax.swing.JLabel lblTurn;
    private javax.swing.JLabel lblTurnNumber;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        if (myTurn) {
            Player player = home.getPlayer();
            Player p1 = match.getPlayers()[0].getPlayer();
            JButton clickedBtn = (JButton) e.getSource();
            if (selectedBtnRuong >= 0) {
                btnO[selectedBtnRuong].setBackground(new Color(240, 240, 240));
            }
            if (player.equals(p1)) {
                for (int i = 0; i <= 4; i++) {
                    if (clickedBtn.equals(btnO[i])) {
                        selectedBtnRuong = i;
                        System.out.println("ô " + i);
                        clickedBtn.setBackground(Color.CYAN);
                    }
                }
            } else {
                for (int i = 6; i <= 10; i++) {
                    if (clickedBtn.equals(btnO[i])) {
                        selectedBtnRuong = i;
                        System.out.println("ô " + i);
                        clickedBtn.setBackground(Color.CYAN);
                    }
                }
            }
        }

        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
