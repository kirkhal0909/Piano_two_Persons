
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author KirKhal
 */
public class Connection {
    
    private static Connection instance = new Connection();
    
    private int connections = 0;
    
    private Connection(){
        
    }
    public static Connection getInstance(){
        return instance;
    }
    
    public void connect(){
        synchronized(this){
            connections++;
            System.out.println("Connections: "+connections);
        }
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        synchronized (this){
            connections--;
        }
    }
}
