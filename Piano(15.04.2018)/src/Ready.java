/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author KirKhal
 */
public class Ready {
    
    public Ready(){
        
    }
    private int note = 1;
    private int instrumentN = 1;
    public boolean Ready = false;
    public boolean Stop = false;
    
    /*public void setNote(byte note){
        this.note = note;
    }
    
    public void setInstrument(byte instrumentN){
        this.instrumentN = instrumentN;
    }*/
    
    public int getNote(){
        return this.note;
    }
    
    public int getInstrument(){
        return this.instrumentN;
    }
    
    public void Start(int note,int instrumentN){
        this.note = note;
        this.instrumentN = instrumentN;
        Ready = true;
    }
}
