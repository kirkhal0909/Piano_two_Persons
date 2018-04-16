/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.*;
import javax.sound.midi.*;
/**
 *
 * @author KirKhal
 */
public class MainSounds implements Runnable{
    public MainSounds(boolean isServer){
        this.isServer = isServer;
    }
    
    private boolean isServer = false;
    
    public void run(){
        try {
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            MidiChannel[] midiChannel = synth.getChannels();
            /*boolean Ready = false;
            int Note = 0,Inst = 0;*/
            while(true)
            {
                //System.out.println(123);
                /*if(isServer){Ready = Piano.MainReady.Ready; Note = Piano.MainReady.getNote(); Inst = Piano.MainReady.getInstrument();}
                else {Ready = Piano.ForClientReady.Ready; Note = Piano.ForClientReady.getNote(); Inst = Piano.ForClientReady.getInstrument();}*/
                Thread.sleep(10);
                
                if (Piano.MainReady.Ready){
                    
                    midiChannel[0].allNotesOff();
                    synth.getChannels()[0].programChange(Piano.MainReady.getInstrument());
                    midiChannel[0].noteOn(Piano.MainReady.getNote(),100);
                    Piano.DelayedServer = 0; Piano.MainReady.Ready = false;
                    if (Piano.Connect){
                        try {
                            //Piano.UpdateStaticLabelIP();
                            //Piano.test();
                            Socket cS = new Socket(Piano.IP,Piano.port);
                            DataOutputStream outToServer = new DataOutputStream(cS.getOutputStream());
                            //System.out.println("I am connected");
                            //if (Piano.Disconnect) {outToServer.writeBytes("Disconnect");}
                            outToServer.writeBytes(String.valueOf(Piano.MainReady.getNote())+' '+String.valueOf(Piano.MainReady.getInstrument())+' '+String.valueOf(Piano.Octave+1) +'\n');
                            cS.close();
                        } catch (Exception ex) {
                            //Logger.getLogger(MainSounds.class.getName()).log(Level.SEVERE, null, ex);
                            Piano.Connect = false;
                            System.out.println("Error connection");
                        }
                    }
                }/* else {Piano.DelayedClient = 0; Piano.ForClientReady.Ready = false;}*/
                    //Thread.sleep(60);
                    //midiChannel[0].noteOff(Piano.MainReady.getNote());
                   //if(Piano.DelayedServer == 7) {midiChannel[0].allNotesOff();}
                }

                /*if (isServer) {if (Piano.DelayedServer == 5) {midiChannel[0].allNotesOff();}}
                else if (Piano.DelayedClient == 5) {midiChannel[0].allNotesOff();}*/
            } catch (InterruptedException ex) {
            Logger.getLogger(MainSounds.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MidiUnavailableException ex) {
            Logger.getLogger(MainSounds.class.getName()).log(Level.SEVERE, null, ex);
        }
   }

}
