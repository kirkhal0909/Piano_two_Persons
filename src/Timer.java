
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
public class Timer implements Runnable{
    public Timer(){
        
    }
    
    public void run(){
        while(true){
        try {
        
            Thread.sleep(1000);
            Piano.DelayedClient++;
            Piano.DelayedServer++;
            if(Piano.DelayedClient>10000) Piano.DelayedClient = 0;
            if(Piano.DelayedServer>10000) Piano.DelayedServer = 0;
            //System.out.println(Piano.DelayedClient);
        } catch (InterruptedException ex) {
            Logger.getLogger(Timer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
       
    }
    
}
