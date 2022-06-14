package udp.client.control;
 
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
 
import model.IPPortAddress;
import model.ObjectWrapper;
import layer2.view.Layer2Frm;
 
 
public class UDPClientCtr {
    private Layer2Frm view;
    private DatagramSocket myClient;    
    private IPPortAddress serverAddress = new IPPortAddress("localhost", 5555); //default udp server address
    private IPPortAddress myAddress = new IPPortAddress("localhost", 0); //default udp  client address
     
    public UDPClientCtr(Layer2Frm view){
        this.view = view;
    }
     
    public UDPClientCtr(Layer2Frm view, int clientPort){
        this.view = view;
        myAddress.setPort(clientPort);
    }
     
    public UDPClientCtr(Layer2Frm view, IPPortAddress serverAddr){
        this.view = view;
        serverAddress = serverAddr;
    }
     
    public UDPClientCtr(Layer2Frm view, IPPortAddress serverAddr, int clientPort){
        this.view = view;
        serverAddress = serverAddr;
        myAddress.setPort(clientPort);
    }
     
     
    public boolean open(){
        try {
            myClient = new DatagramSocket(myAddress.getPort());
            myAddress.setHost(InetAddress.getLocalHost().getHostAddress());            
            myAddress.setPort(myClient.getLocalPort());
            view.setUDPServerandUDPClientInfo(serverAddress, myAddress);
            view.showMessage("UDP client is running at the host: " + myAddress.getHost() + ", port: " + myAddress.getPort());
        }catch(Exception e) {
            e.printStackTrace();
            view.showMessage("Error to open the datagram socket!");
            return false;
        }
        return true;
    }
     
    public boolean close(){
        try {
            myClient.close();
        }catch(Exception e) {
            e.printStackTrace();
            view.showMessage("Error to close the datagram socket!");
            return false;
        }
        return true;
    }
     
    public boolean sendData(ObjectWrapper data){
        try {
            //prepare the buffer and write the data to send into the buffer
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(data);
            oos.flush();            
             
            //create data package and send
            byte[] sendData = baos.toByteArray();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(serverAddress.getHost()), serverAddress.getPort());
            myClient.send(sendPacket);
             
        } catch (Exception e) {
            e.printStackTrace();
            view.showMessage("Error in sending data package");
            return false;
        }
        return true;
    }
     
    public ObjectWrapper receiveData(){
        ObjectWrapper result = null;
        try {   
            //prepare the buffer and fetch the received data into the buffer
            byte[] receiveData = new byte[10240];
            DatagramPacket receivePacket = new  DatagramPacket(receiveData, receiveData.length);
            myClient.receive(receivePacket);
             
            //read incoming data from the buffer 
            ByteArrayInputStream bais = new ByteArrayInputStream(receiveData);
            ObjectInputStream ois = new ObjectInputStream(bais);
            result = (ObjectWrapper)ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            view.showMessage("Error in receiving data package");
        }
        return result;
    }

    public Layer2Frm getView() {
        return view;
    }
    
    
}