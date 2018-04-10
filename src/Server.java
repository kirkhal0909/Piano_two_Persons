
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author KirKhal
 */
public class Server implements Runnable{
    public Server(int port){
        this.port = port;
    }
    
    private int port;
    
    public void run(){
        ServerSocket s;
        int note = 0,ins = 0; 
        while (true){
        try {
                s = new ServerSocket(port);            
                Synthesizer synth = MidiSystem.getSynthesizer();
                synth.open();
                MidiChannel[] midiChannel = synth.getChannels();
            while(true){
                Socket cS = s.accept();
                BufferedReader msgFrmClient = new BufferedReader(new InputStreamReader(cS.getInputStream()));
                String ClientSentence = msgFrmClient.readLine();
                Piano.IP = cS.getInetAddress().getHostAddress(); Piano.Connect = true;
                //System.out.println(Piano.IP);
                //System.out.println("Message from client: "+ClientSentence);
                try{
                    note = Integer.parseInt(ClientSentence.split(" ")[0]);
                    ins = Integer.parseInt(ClientSentence.split(" ")[1]);
                    //Piano.ForClientReady.Start(note, ins);
                    midiChannel[0].allNotesOff();
                    synth.getChannels()[0].programChange(ins);
                    midiChannel[0].noteOn(note,10000);
                    //Piano.DelayedClient = 0;
                }
                catch (Exception e) {System.out.println("Any format of message!!!");if(ClientSentence == "Disconnect"){Piano.IP = "127.0.0.1";}}
            }
        }catch (IOException ex) {
            //Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }   catch (MidiUnavailableException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            //Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);

       }
    }   
}
