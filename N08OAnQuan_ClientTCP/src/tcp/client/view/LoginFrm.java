package tcp.client.view;
 
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
 
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import model.ObjectWrapper;
import model.Player;
import tcp.client.control.TCPClientCtr;
 
public class LoginFrm extends JFrame implements ActionListener{
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private TCPClientCtr mySocket;
     
    public LoginFrm(TCPClientCtr socket){
        super("TCP Login MVC");     
        mySocket = socket;
         
        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        txtPassword.setEchoChar('*');
        btnLogin = new JButton("Login");
         
        JPanel content = new JPanel();
        content.setLayout(new FlowLayout());
        content.add(new JLabel("Username:"));
        content.add(txtUsername);
        content.add(new JLabel("Password:"));
        content.add(txtPassword);
        content.add(btnLogin);
        btnLogin.addActionListener(this);
                 
        this.setContentPane(content);
        this.pack();        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mySocket.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_LOGIN_USER, this));
    }
     
 
    public void actionPerformed(ActionEvent e) {
        if((e.getSource() instanceof JButton) && (((JButton)e.getSource()).equals(btnLogin))) {
            //pack the entity
            Player player = new Player();
            player.setUsername(txtUsername.getText());
            player.setPassword(txtPassword.getText());
             
            //sending data
            mySocket.sendData(new ObjectWrapper(ObjectWrapper.LOGIN_USER, player));
             
            //receive data
//            Object obj = mySocket.receiveData();
//            if(obj instanceof String) {
//                String result = (String)obj;
//                if(result.equals("ok")) {
//                    JOptionPane.showMessageDialog(this, "Login succesfully!");
//                    this.dispose();
//                }
//                else {
//                    JOptionPane.showMessageDialog(this, "Incorrect username and/or password!");
//                }
//            }
        }
    }
    
    public void receivedDataProcessing(ObjectWrapper data) {        
        if (data.getData().equals("false")) {
            JOptionPane.showMessageDialog(this, "Incorrect username and/or password!");
        }else if(data.getData() instanceof Player) {
            JOptionPane.showMessageDialog(this, "Login succesfully!");
            /*for(DataTO func: mySocket.getActiveFunction())
                if(func.getData().equals(this))
                    mySocket.getActiveFunction().remove(func);*/
            ObjectWrapper exist = null;
            for (ObjectWrapper fto: mySocket.getActiveFunction()) {
            	if(fto.getData() instanceof HomeFrm) {
            		((HomeFrm) fto.getData()).dispose();
            		exist = fto;
            	}
            }
            if(exist != null) {
            	mySocket.getActiveFunction().remove(exist);
            }
            
            (new HomeFrm(mySocket, (Player) data.getData())).setVisible(true);
            //System.out.println(data.getData());
            this.dispose();
        }
    }
}