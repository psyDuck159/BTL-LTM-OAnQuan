package model;
 
import java.io.Serializable;
 
public class IPPortAddress implements Serializable{
    private static final long serialVersionUID = 20210811012L;
    private String host;
    private int port;
     
    public IPPortAddress() {
        super();
    }
 
    public IPPortAddress(String host, int port) {
        super();
        this.host = host;
        this.port = port;
    }
 
    public String getHost() {
        return host;
    }
 
    public void setHost(String host) {
        this.host = host;
    }
 
    public int getPort() {
        return port;
    }
 
    public void setPort(int port) {
        this.port = port;
    }
}