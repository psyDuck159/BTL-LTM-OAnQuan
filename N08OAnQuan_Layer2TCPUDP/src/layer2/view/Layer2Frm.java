/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package layer2.view;

import model.IPPortAddress;
import tcp.server.control.TCPServerCtr;
import udp.client.control.UDPClientCtr;

/**
 *
 * @author Admin
 */
public class Layer2Frm extends javax.swing.JFrame {

    private UDPClientCtr udpClientCtr;
    private TCPServerCtr tcpServerCtr;

    /**
     * Creates new form Layer2Frm
     */
    public Layer2Frm() {
        initComponents();
    }

    public void showMessage(String s) {
        mainText.append("\n" + s);
        mainText.setCaretPosition(mainText.getDocument().getLength());
    }

    public void showTCPServerInfo(IPPortAddress serverAddr) {
        txtTCPServerHost.setText(serverAddr.getHost());
        txtTCPServerPort.setText(serverAddr.getPort() + "");
    }

    public void setUDPServerandUDPClientInfo(IPPortAddress serverAddress, IPPortAddress clientAddress) {
        txtUDPServerHost.setText(serverAddress.getHost());
        txtUDPServerPort.setText(serverAddress.getPort() + "");
        txtUDPClientHost.setText(clientAddress.getHost());
        txtUDPClientPort.setText(clientAddress.getPort() + "");
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
        txtTCPServerHost = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtTCPServerPort = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtUDPServerHost = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        txtUDPServerPort = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtUDPClientHost = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        txtUDPClientPort = new javax.swing.JTextField();
        btnStart = new javax.swing.JButton();
        btnStop = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        mainText = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel1.setText("Server TCP/IP - Client UDP");

        jLabel2.setText("TCP Server Host");

        jLabel3.setText("TCP Server Port");

        jLabel4.setText("UDP Server Host");

        jLabel5.setText("UDP Server Port");

        jLabel6.setText("UDP Client Host");

        jLabel7.setText("UDP Client Port");

        btnStart.setText("Start");
        btnStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStartActionPerformed(evt);
            }
        });

        btnStop.setText("Stop");
        btnStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStopActionPerformed(evt);
            }
        });

        mainText.setColumns(20);
        mainText.setRows(5);
        jScrollPane1.setViewportView(mainText);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(176, 176, 176)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 263, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel2)
                                        .addComponent(txtTCPServerHost)
                                        .addComponent(jLabel3)
                                        .addComponent(txtTCPServerPort, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
                                    .addComponent(btnStart))
                                .addGap(81, 81, 81)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel4)
                                            .addComponent(txtUDPServerHost)
                                            .addComponent(jLabel5)
                                            .addComponent(txtUDPServerPort, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE))
                                        .addGap(83, 83, 83)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jLabel6)
                                            .addComponent(txtUDPClientHost)
                                            .addComponent(jLabel7)
                                            .addComponent(txtUDPClientPort, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)))
                                    .addComponent(btnStop))))
                        .addGap(0, 75, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel1)
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTCPServerHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUDPServerHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUDPClientHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTCPServerPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUDPServerPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtUDPClientPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnStart)
                    .addComponent(btnStop))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        // TODO add your handling code here:        
        btnStop.setEnabled(true);
        btnStart.setEnabled(false);

        // connect to udp server
        if (!txtUDPServerHost.getText().isEmpty() && (txtUDPServerHost.getText().trim().length() > 0)
                && !txtUDPServerPort.getText().isEmpty() && (txtUDPServerPort.getText().trim().length() > 0)) {//custom server port
            int serverPort = Integer.parseInt(txtUDPServerPort.getText().trim());
            if (!txtUDPClientPort.getText().isEmpty() && (txtUDPClientPort.getText().trim().length() > 0)) {//custom client port
                int clientPort = Integer.parseInt(txtUDPClientPort.getText().trim());
                udpClientCtr = new UDPClientCtr(this, new IPPortAddress(txtUDPServerHost.getText().trim(), serverPort), clientPort);
            } else {//default client port
                udpClientCtr = new UDPClientCtr(this, new IPPortAddress(txtUDPServerHost.getText().trim(), serverPort));
            }
        } else {//default server host and port
            if (!txtUDPClientPort.getText().isEmpty() && (txtUDPClientPort.getText().trim().length() > 0)) {//custom client port
                int clientPort = Integer.parseInt(txtUDPClientPort.getText().trim());
                udpClientCtr = new UDPClientCtr(this, clientPort);
            } else {//default client port
                udpClientCtr = new UDPClientCtr(this);
            }
        }

        if (udpClientCtr.open()) {
            btnStop.setEnabled(true);
            btnStart.setEnabled(false);

        } else {
            if (udpClientCtr != null) {
                udpClientCtr.close();
                udpClientCtr = null;
            }
            btnStop.setEnabled(false);
            btnStart.setEnabled(true);

        }
        // open tcp server
        if (!txtTCPServerPort.getText().isEmpty() && (txtTCPServerPort.getText().trim().length() > 0)) {//custom port
            int port = Integer.parseInt(txtTCPServerPort.getText().trim());
            tcpServerCtr = new TCPServerCtr(this, port);
        } else {// default port
            tcpServerCtr = new TCPServerCtr(this);
        }
    }//GEN-LAST:event_btnStartActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        // TODO add your handling code here:
        //close tcp server
        if (tcpServerCtr != null) {
            tcpServerCtr.stopServer();
            tcpServerCtr = null;
        }
        btnStop.setEnabled(false);
        btnStart.setEnabled(true);
        txtTCPServerHost.setText("localhost");

        //disconnect to udp server
        if (udpClientCtr != null) {
            udpClientCtr.close();
            udpClientCtr = null;
        }
        btnStop.setEnabled(false);
        btnStart.setEnabled(true);
    }//GEN-LAST:event_btnStopActionPerformed

    public UDPClientCtr getUdpClientCtr() {
        return udpClientCtr;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Layer2Frm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Layer2Frm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Layer2Frm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Layer2Frm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Layer2Frm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnStart;
    private javax.swing.JButton btnStop;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea mainText;
    private javax.swing.JTextField txtTCPServerHost;
    private javax.swing.JTextField txtTCPServerPort;
    private javax.swing.JTextField txtUDPClientHost;
    private javax.swing.JTextField txtUDPClientPort;
    private javax.swing.JTextField txtUDPServerHost;
    private javax.swing.JTextField txtUDPServerPort;
    // End of variables declaration//GEN-END:variables
}
