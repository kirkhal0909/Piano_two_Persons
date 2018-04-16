/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.*;
import javax.swing.JOptionPane;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author KirKhal
 */
public class Piano extends javax.swing.JFrame {
    static public Ready MainReady = new Ready();
    //static public Ready ForClientReady = new Ready();
    /**
     * Creates new form Piano
     */
    public Piano() {        
        initComponents();
    }

public class Server implements Runnable{
    public Server(int port){
        this.port = port;
    }
    
    private int port;
    
    public void run(){
        ServerSocket s;
        int note = 0,ins = 0,octave = 0; 
        while (true){
        try {
                //Key1.doClick();
                //System.out.println("New thread");
                //Thread.sleep(1000000);
                s = new ServerSocket(port);   
                Synthesizer synth = MidiSystem.getSynthesizer();
                synth.open();
                MidiChannel[] midiChannel = synth.getChannels();
            while(true){
                Socket cS = s.accept();
                BufferedReader msgFrmClient = new BufferedReader(new InputStreamReader(cS.getInputStream()));
                String ClientSentence = msgFrmClient.readLine();
                if(IP != "127.0.0.1"){IP = cS.getInetAddress().getHostAddress(); Piano.Connect = true;}
                //System.out.println("I'm working now suka bleat");
                //ClientIP.setText(Piano.IP);
                //System.out.println(Piano.IP);
                //System.out.println("Message from client: "+ClientSentence);
                try{
                    note = Integer.parseInt(ClientSentence.split(" ")[0]);
                    ins = Integer.parseInt(ClientSentence.split(" ")[1]);
                    //System.out.println(ClientSentence);
                    midiChannel[0].allNotesOff();
                    synth.getChannels()[0].programChange(ins);
                    midiChannel[0].noteOn(note,10000);
                    //octave = (int)note/12;
                    tmp = "   Octave "+ClientSentence.split(" ")[2]+"   Instrument "+String.valueOf(ins+1);
                    ClientIPLabel.setText(Piano.IP+tmp);
                    //InstrumentClientField.setText(String.valueOf(ins+1));
                    //OctaveClientField.setText(ClientSentence.split(" ")[2]);
                    //note = note-octave*12;
                    /*switch(Integer.parseInt(ClientSentence.split(" ")[3])){
                        case 1: ClientKey1.doClick(); break;
                        case 2: ClientKey2.doClick(); break;
                        case 3: ClientKey3.doClick(); break;
                        case 4: ClientKey4.doClick(); break;
                        case 5: ClientKey5.doClick(); break;
                        case 6: ClientKey6.doClick(); break;
                        case 7: ClientKey7.doClick(); break;
                        case 8: ClientKey8.doClick(); break;
                        case 9: ClientKey9.doClick(); break;
                        case 10: ClientKey10.doClick(); break;
                        case 11: ClientKey11.doClick(); break;
                        case 12: ClientKey12.doClick(); break;
                        case 13: ClientKey13.doClick(); break;
                        case 14: ClientKey14.doClick(); break;
                        case 15: ClientKey15.doClick(); break;
                        case 16: ClientKey16.doClick(); break;
                        case 17: ClientKey17.doClick(); break;
                        case 18: ClientKey18.doClick(); break;
                        case 19: ClientKey19.doClick(); break;
                        case 20: ClientKey20.doClick(); break;
                        case 21: ClientKey21.doClick(); break;
                        case 22: ClientKey22.doClick(); break;
                        case 23: ClientKey23.doClick(); break;
                        case 24: ClientKey24.doClick(); break;
                    }*/
                    //Piano.ForClientReady.Start(note, ins);
                    //Piano.DelayedClient = 0;
                }
                catch (Exception e) {
                    System.out.println("Any format of message!!!");
                    String ErrMsg = ClientSentence;
                    if(Integer.parseInt(ErrMsg) == 1000){
                    s.close();
                    midiChannel[0].allNotesOff();                    
                    server = null;                    
                    server.interrupt();                        

                    }
                    
                }
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
                
                if (MainReady.Ready){
                    
                    midiChannel[0].allNotesOff();
                    synth.getChannels()[0].programChange(MainReady.getInstrument());
                    midiChannel[0].noteOn(MainReady.getNote(),100);
                    MainReady.Ready = false;
                    if (Connect){
                        try {
                            //Piano.UpdateStaticLabelIP();
                            //Piano.test();
                            Socket cS = new Socket(IP,port);
                            DataOutputStream outToServer = new DataOutputStream(cS.getOutputStream());
                            //System.out.println("I am connected");
                            //if (Piano.Disconnect) {outToServer.writeBytes("Disconnect");}
                            outToServer.writeBytes(String.valueOf(MainReady.getNote())+' '+String.valueOf(MainReady.getInstrument())+' '+String.valueOf(Octave+1) +'\n');
                            cS.close();
                        } catch (Exception ex) {
                            //Logger.getLogger(MainSounds.class.getName()).log(Level.SEVERE, null, ex);
                            Connect = false;
                            System.out.println("Error connection");
                            if (ConnectButton.getText()=="Disconnect"){ConnectButton.doClick();}
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

    public String tmp = "";
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Keys = new javax.swing.JPanel();
        Key2 = new javax.swing.JButton();
        Key4 = new javax.swing.JButton();
        Key7 = new javax.swing.JButton();
        Key9 = new javax.swing.JButton();
        Key11 = new javax.swing.JButton();
        Key14 = new javax.swing.JButton();
        Key16 = new javax.swing.JButton();
        Key19 = new javax.swing.JButton();
        Key21 = new javax.swing.JButton();
        Key23 = new javax.swing.JButton();
        Key1 = new javax.swing.JButton();
        Key3 = new javax.swing.JButton();
        Key5 = new javax.swing.JButton();
        Key6 = new javax.swing.JButton();
        Key8 = new javax.swing.JButton();
        Key10 = new javax.swing.JButton();
        Key12 = new javax.swing.JButton();
        Key13 = new javax.swing.JButton();
        Key15 = new javax.swing.JButton();
        Key17 = new javax.swing.JButton();
        Key18 = new javax.swing.JButton();
        Key20 = new javax.swing.JButton();
        Key22 = new javax.swing.JButton();
        Key24 = new javax.swing.JButton();
        OctaveLabel = new javax.swing.JLabel();
        OctaveLess = new javax.swing.JButton();
        OctaveField = new javax.swing.JLabel();
        OctaveMore = new javax.swing.JButton();
        ConnectButton = new javax.swing.JButton();
        ClientIPField = new javax.swing.JTextField();
        InstrumentMore = new javax.swing.JButton();
        InstrumentLess = new javax.swing.JButton();
        InstrumentField = new javax.swing.JLabel();
        InstrumentLabel = new javax.swing.JLabel();
        ServerLabel = new javax.swing.JLabel();
        ServerOn = new javax.swing.JButton();
        ServerOff = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        ClientIPLabel = new javax.swing.JLabel();
        SoundKeyStop = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Piano");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(null);
        setResizable(false);
        setSize(new java.awt.Dimension(950, 573));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        Keys.setBackground(new java.awt.Color(51, 51, 51));
        Keys.setLayout(null);

        Key2.setBackground(new java.awt.Color(0, 0, 0));
        Key2.setFocusable(false);
        Key2.setRequestFocusEnabled(false);
        Key2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key2ActionPerformed(evt);
            }
        });
        Keys.add(Key2);
        Key2.setBounds(42, 0, 50, 190);

        Key4.setBackground(new java.awt.Color(0, 0, 0));
        Key4.setFocusable(false);
        Key4.setRequestFocusEnabled(false);
        Key4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key4ActionPerformed(evt);
            }
        });
        Keys.add(Key4);
        Key4.setBounds(106, 0, 50, 190);

        Key7.setBackground(new java.awt.Color(0, 0, 0));
        Key7.setFocusable(false);
        Key7.setRequestFocusEnabled(false);
        Key7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key7ActionPerformed(evt);
            }
        });
        Keys.add(Key7);
        Key7.setBounds(236, 0, 50, 190);

        Key9.setBackground(new java.awt.Color(0, 0, 0));
        Key9.setFocusable(false);
        Key9.setRequestFocusEnabled(false);
        Key9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key9ActionPerformed(evt);
            }
        });
        Keys.add(Key9);
        Key9.setBounds(302, 0, 50, 190);

        Key11.setBackground(new java.awt.Color(0, 0, 0));
        Key11.setFocusable(false);
        Key11.setRequestFocusEnabled(false);
        Key11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key11ActionPerformed(evt);
            }
        });
        Keys.add(Key11);
        Key11.setBounds(368, 0, 50, 190);

        Key14.setBackground(new java.awt.Color(0, 0, 0));
        Key14.setFocusable(false);
        Key14.setRequestFocusEnabled(false);
        Key14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key14ActionPerformed(evt);
            }
        });
        Keys.add(Key14);
        Key14.setBounds(496, 0, 50, 190);

        Key16.setBackground(new java.awt.Color(0, 0, 0));
        Key16.setFocusable(false);
        Key16.setRequestFocusEnabled(false);
        Key16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key16ActionPerformed(evt);
            }
        });
        Keys.add(Key16);
        Key16.setBounds(564, 0, 50, 190);

        Key19.setBackground(new java.awt.Color(0, 0, 0));
        Key19.setFocusable(false);
        Key19.setRequestFocusEnabled(false);
        Key19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key19ActionPerformed(evt);
            }
        });
        Keys.add(Key19);
        Key19.setBounds(693, 0, 50, 190);

        Key21.setBackground(new java.awt.Color(0, 0, 0));
        Key21.setFocusable(false);
        Key21.setRequestFocusEnabled(false);
        Key21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key21ActionPerformed(evt);
            }
        });
        Keys.add(Key21);
        Key21.setBounds(757, 0, 50, 190);

        Key23.setBackground(new java.awt.Color(0, 0, 0));
        Key23.setFocusable(false);
        Key23.setRequestFocusEnabled(false);
        Key23.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key23ActionPerformed(evt);
            }
        });
        Keys.add(Key23);
        Key23.setBounds(821, 0, 50, 190);

        Key1.setFocusable(false);
        Key1.setRequestFocusEnabled(false);
        Key1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key1ActionPerformed(evt);
            }
        });
        Keys.add(Key1);
        Key1.setBounds(0, 0, 70, 320);

        Key3.setFocusable(false);
        Key3.setRequestFocusEnabled(false);
        Key3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key3ActionPerformed(evt);
            }
        });
        Keys.add(Key3);
        Key3.setBounds(65, 0, 70, 320);

        Key5.setFocusable(false);
        Key5.setRequestFocusEnabled(false);
        Key5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key5ActionPerformed(evt);
            }
        });
        Keys.add(Key5);
        Key5.setBounds(130, 0, 70, 320);

        Key6.setFocusable(false);
        Key6.setRequestFocusEnabled(false);
        Key6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key6ActionPerformed(evt);
            }
        });
        Keys.add(Key6);
        Key6.setBounds(195, 0, 70, 320);

        Key8.setFocusable(false);
        Key8.setRequestFocusEnabled(false);
        Key8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key8ActionPerformed(evt);
            }
        });
        Keys.add(Key8);
        Key8.setBounds(260, 0, 70, 320);

        Key10.setFocusable(false);
        Key10.setRequestFocusEnabled(false);
        Key10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key10ActionPerformed(evt);
            }
        });
        Keys.add(Key10);
        Key10.setBounds(325, 0, 70, 320);

        Key12.setFocusable(false);
        Key12.setRequestFocusEnabled(false);
        Key12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key12ActionPerformed(evt);
            }
        });
        Keys.add(Key12);
        Key12.setBounds(390, 0, 70, 320);

        Key13.setFocusable(false);
        Key13.setRequestFocusEnabled(false);
        Key13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key13ActionPerformed(evt);
            }
        });
        Keys.add(Key13);
        Key13.setBounds(455, 0, 70, 320);

        Key15.setFocusable(false);
        Key15.setRequestFocusEnabled(false);
        Key15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key15ActionPerformed(evt);
            }
        });
        Keys.add(Key15);
        Key15.setBounds(520, 0, 70, 320);

        Key17.setFocusable(false);
        Key17.setRequestFocusEnabled(false);
        Key17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key17ActionPerformed(evt);
            }
        });
        Keys.add(Key17);
        Key17.setBounds(585, 0, 70, 320);

        Key18.setFocusable(false);
        Key18.setRequestFocusEnabled(false);
        Key18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key18ActionPerformed(evt);
            }
        });
        Keys.add(Key18);
        Key18.setBounds(650, 0, 70, 320);

        Key20.setFocusable(false);
        Key20.setRequestFocusEnabled(false);
        Key20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key20ActionPerformed(evt);
            }
        });
        Keys.add(Key20);
        Key20.setBounds(715, 0, 70, 320);

        Key22.setFocusable(false);
        Key22.setRequestFocusEnabled(false);
        Key22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key22ActionPerformed(evt);
            }
        });
        Keys.add(Key22);
        Key22.setBounds(780, 0, 70, 320);

        Key24.setFocusable(false);
        Key24.setRequestFocusEnabled(false);
        Key24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Key24ActionPerformed(evt);
            }
        });
        Keys.add(Key24);
        Key24.setBounds(845, 0, 70, 320);

        OctaveLabel.setFont(new java.awt.Font("Arial", 0, 48)); // NOI18N
        OctaveLabel.setText("Octave");

        OctaveLess.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        OctaveLess.setText("<");
        OctaveLess.setFocusable(false);
        OctaveLess.setRequestFocusEnabled(false);
        OctaveLess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OctaveLessActionPerformed(evt);
            }
        });

        OctaveField.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        OctaveField.setText("10");

        OctaveMore.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        OctaveMore.setText(">");
        OctaveMore.setFocusable(false);
        OctaveMore.setRequestFocusEnabled(false);
        OctaveMore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OctaveMoreActionPerformed(evt);
            }
        });

        ConnectButton.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        ConnectButton.setText("Connect");
        ConnectButton.setFocusable(false);
        ConnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConnectButtonActionPerformed(evt);
            }
        });

        ClientIPField.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        ClientIPField.setText("192.168.0.240");
        ClientIPField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                ClientIPFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                ClientIPFieldKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                ClientIPFieldKeyTyped(evt);
            }
        });

        InstrumentMore.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        InstrumentMore.setText(">");
        InstrumentMore.setFocusable(false);
        InstrumentMore.setRequestFocusEnabled(false);
        InstrumentMore.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InstrumentMoreActionPerformed(evt);
            }
        });

        InstrumentLess.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        InstrumentLess.setText("<");
        InstrumentLess.setFocusable(false);
        InstrumentLess.setRequestFocusEnabled(false);
        InstrumentLess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InstrumentLessActionPerformed(evt);
            }
        });

        InstrumentField.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        InstrumentField.setText("127");

        InstrumentLabel.setFont(new java.awt.Font("Arial", 0, 48)); // NOI18N
        InstrumentLabel.setText("Instrument");

        ServerLabel.setFont(new java.awt.Font("Arial", 0, 48)); // NOI18N
        ServerLabel.setText("Server");

        ServerOn.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        ServerOn.setText("On");
        ServerOn.setFocusable(false);
        ServerOn.setRequestFocusEnabled(false);
        ServerOn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ServerOnActionPerformed(evt);
            }
        });

        ServerOff.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        ServerOff.setText("Off");
        ServerOff.setEnabled(false);
        ServerOff.setFocusable(false);
        ServerOff.setRequestFocusEnabled(false);
        ServerOff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ServerOffActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        jLabel1.setText(":676");

        ClientIPLabel.setFont(new java.awt.Font("Tw Cen MT", 0, 36)); // NOI18N
        ClientIPLabel.setText("-");

        SoundKeyStop.setFont(new java.awt.Font("Tahoma", 0, 24)); // NOI18N
        SoundKeyStop.setText("stop");
        SoundKeyStop.setToolTipText("Stop sound(ESC)");
        SoundKeyStop.setFocusable(false);
        SoundKeyStop.setRequestFocusEnabled(false);
        SoundKeyStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SoundKeyStopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(ClientIPField, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(5, 5, 5)
                                .addComponent(jLabel1))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(ConnectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(ServerOn)
                                .addGap(12, 12, 12)
                                .addComponent(ServerOff))
                            .addComponent(ServerLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(OctaveLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(OctaveLess)
                                .addGap(5, 5, 5)
                                .addComponent(OctaveField)
                                .addGap(5, 5, 5)
                                .addComponent(OctaveMore)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addComponent(InstrumentLess)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(InstrumentField)
                                .addGap(12, 12, 12)
                                .addComponent(InstrumentMore)
                                .addGap(7, 7, 7)
                                .addComponent(SoundKeyStop))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(35, 35, 35)
                                .addComponent(InstrumentLabel))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(Keys, javax.swing.GroupLayout.PREFERRED_SIZE, 915, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(ClientIPLabel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(ConnectButton)
                        .addGap(7, 7, 7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(OctaveLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(InstrumentLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ServerLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ClientIPField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(OctaveLess)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(4, 4, 4)
                            .addComponent(OctaveField))
                        .addComponent(OctaveMore))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ServerOn)
                                .addComponent(jLabel1))
                            .addComponent(ServerOff)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(InstrumentField)
                                .addComponent(InstrumentLess))
                            .addComponent(InstrumentMore)
                            .addComponent(SoundKeyStop))))
                .addGap(16, 16, 16)
                .addComponent(Keys, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(ClientIPLabel)
                .addGap(0, 8, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

static public int Octave = 3;
int ins = 0;
    
    private void Key1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key1ActionPerformed
       MainReady.Start(0+Octave*12, ins);
    }//GEN-LAST:event_Key1ActionPerformed

    private void Key5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key5ActionPerformed
      MainReady.Start(4+Octave*12, ins);
    }//GEN-LAST:event_Key5ActionPerformed

    private void Key4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key4ActionPerformed
     MainReady.Start(3+Octave*12, ins);
    }//GEN-LAST:event_Key4ActionPerformed

    public void One(){
        this.setSize(new Dimension(940,494));
    }
    
    public void Two(){
        this.setSize(new Dimension(940,534));
        //ClientIPField.setVisible(false);
        /*ClientIPLabel.setVisible(false);
        InstrumentClientLable.setVisible(false);
        InstrumentClientField.setVisible(false);
        OctaveClientField.setVisible(false);
        OctaveClientLabel.setVisible(false);
        ClientIP.setVisible(false);*/
    }
    
    String appdata = System.getenv("APPDATA")+'\\';
    String folderSave = appdata+"Piano\\";
    String FileSave = folderSave+"last.txt";
    String FileLastIP = folderSave+"lastIP.txt";
    
    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        //this.setVisible(false);
        One();
        //One();
        //Keys1.setVisible(false);
        LoadFromFile();
        MainThread.start();
        //server.start();
        //ForClientThread.start();
        OctaveField.setText(String.valueOf(Octave+1));
        InstrumentField.setText(String.valueOf(ins+1));
        //timer.start();
        //ClientLabelIP.setText(IP);
        this.requestFocus();       
        //this.setVisible(true);// TODO add your handling code here:
    }//GEN-LAST:event_formWindowOpened
    
    public void PlayByButton(int KeyCode){ 
        switch(KeyCode){
            case KeyEvent.VK_Q: Key1.doClick(); Key1.requestFocus(); break;
            case KeyEvent.VK_W: Key2.doClick();Key2.requestFocus(); break;
            case KeyEvent.VK_A: Key3.doClick();Key3.requestFocus(); break;
            case KeyEvent.VK_S: Key4.doClick();Key4.requestFocus(); break;
            case KeyEvent.VK_Z: Key5.doClick();Key5.requestFocus(); break;
            case KeyEvent.VK_X: Key6.doClick();Key6.requestFocus(); break;
            case KeyEvent.VK_D: Key7.doClick();Key7.requestFocus(); break;
            case KeyEvent.VK_C: Key8.doClick();Key8.requestFocus(); break;
            case KeyEvent.VK_F: Key9.doClick();Key9.requestFocus(); break;
            case KeyEvent.VK_V: Key10.doClick();Key10.requestFocus(); break;
            case KeyEvent.VK_G: Key11.doClick();Key11.requestFocus(); break;
            case KeyEvent.VK_B: Key12.doClick();Key12.requestFocus(); break;
            case KeyEvent.VK_N: Key13.doClick();Key13.requestFocus(); break;
            case KeyEvent.VK_J: Key14.doClick();Key14.requestFocus(); break;
            case KeyEvent.VK_M: Key15.doClick();Key15.requestFocus(); break;
            case KeyEvent.VK_K: Key16.doClick();Key16.requestFocus(); break;
            case KeyEvent.VK_LESS: Key17.doClick();Key17.requestFocus(); break;
            case 44: Key17.doClick();Key17.requestFocus(); break;
            case 46: Key18.doClick();Key18.requestFocus(); break;
            case 59: Key19.doClick();Key19.requestFocus(); break;
            case KeyEvent.VK_SLASH: Key20.doClick();Key20.requestFocus(); break;
            //case 46: Key20.doClick();Key.requestFocus(); break;
            case KeyEvent.VK_P: Key20.doClick();Key20.requestFocus(); break;
            case KeyEvent.VK_QUOTE: Key21.doClick();Key21.requestFocus(); break;
            case KeyEvent.VK_NUMPAD1: Key22.doClick();Key22.requestFocus(); break;
            case KeyEvent.VK_NUMPAD5: Key23.doClick();Key23.requestFocus(); break;
            case KeyEvent.VK_NUMPAD3: Key24.doClick();Key24.requestFocus(); break;
            case 91: Octave--; if (Octave < 0) {Octave = 9;} OctaveField.setText(String.valueOf(Octave+1)); SaveToFile(false); break;
            case 93: Octave++; if (Octave > 9) {Octave = 0;} OctaveField.setText(String.valueOf(Octave+1)); SaveToFile(false); break;
            case 45: ins--; if (ins < 0) {ins = maxIns;} InstrumentField.setText(String.valueOf(ins+1)); SaveToFile(false); break;
            case 109: ins--; if (ins < 0) {ins = maxIns;} InstrumentField.setText(String.valueOf(ins+1)); SaveToFile(false); break;
            case 61: ins++; if (ins > maxIns) {ins = 0;} InstrumentField.setText(String.valueOf(ins+1)); SaveToFile(false); break;
            case 107: ins++; if (ins > maxIns) {ins = 0;} InstrumentField.setText(String.valueOf(ins+1));SaveToFile(false); break;
            case KeyEvent.VK_ESCAPE: SoundKeyStop.doClick(); break;
        }
        //System.out.println(KeyCode);
    }
    
    public void SaveToFile(boolean writeIP){
        try{
        File dir = new File(folderSave);
        dir.mkdir();
        File f = new File(FileSave);
        FileOutputStream fStream = new FileOutputStream(f, false);
        String str1 = String.valueOf(Octave)+"\r\n";
        fStream.write((str1).getBytes());
        fStream.write((String.valueOf(ins)).getBytes());
        fStream.close();
        if (writeIP){
            File fIP = new File(FileLastIP);
            FileOutputStream fIPStream = new FileOutputStream(fIP, false);
            fIPStream.write((IP).getBytes());
            fIPStream.close();
        }
        } catch (IOException e){}
    }
    
    public void LoadFromFile(){
        //File f = new File(FileSave);
        
        try {
            BufferedReader br = null;
            FileReader fr = null;
            fr = new FileReader(FileSave);
            br = new BufferedReader(fr);
            Octave = Integer.parseInt(br.readLine());
            ins = Integer.parseInt(br.readLine());
            br.close();
            fr.close();
            
            fr = new FileReader(FileLastIP);
            br = new BufferedReader(fr);
            IP = br.readLine();
            ClientIPField.setText(IP);
            br.close();
            fr.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Piano.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Piano.class.getName()).log(Level.SEVERE, null, ex);
        }
	
        //File fIP = new File(FileLastIP);
    }
    
    private Thread MainThread = new Thread(new MainSounds(true));
    private Thread ForClientThread = new Thread(new MainSounds(false));
    private Thread server = new Thread(new Server(port));
   // static public Thread ClientServer = new Thread(new Server(6666));
    static public boolean Connect = false;
    static public String IP = "";
    static public int port = 676;
    //private Thread t2 = new Thread(new Threads());
    //t2.start();
    private void Key2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key2ActionPerformed
        MainReady.Start(1+Octave*12, ins);
    }//GEN-LAST:event_Key2ActionPerformed

    private void Key24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key24ActionPerformed
        MainReady.Start(23+Octave*12, ins);
    }//GEN-LAST:event_Key24ActionPerformed

    private void Key3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key3ActionPerformed
       MainReady.Start(2+Octave*12, ins);
    }//GEN-LAST:event_Key3ActionPerformed

    private void Key6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key6ActionPerformed
        MainReady.Start(5+Octave*12, ins);
    }//GEN-LAST:event_Key6ActionPerformed

    private void Key7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key7ActionPerformed
        MainReady.Start(6+Octave*12, ins);
    }//GEN-LAST:event_Key7ActionPerformed

    private void Key8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key8ActionPerformed
       MainReady.Start(7+Octave*12, ins);
    }//GEN-LAST:event_Key8ActionPerformed

    private void Key9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key9ActionPerformed
       MainReady.Start(8+Octave*12, ins);
    }//GEN-LAST:event_Key9ActionPerformed

    private void Key10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key10ActionPerformed
        MainReady.Start(9+Octave*12, ins);
    }//GEN-LAST:event_Key10ActionPerformed

    private void Key11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key11ActionPerformed
        MainReady.Start(10+Octave*12, ins);
    }//GEN-LAST:event_Key11ActionPerformed

    private void Key12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key12ActionPerformed
        MainReady.Start(11+Octave*12, ins);
    }//GEN-LAST:event_Key12ActionPerformed

    private void Key13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key13ActionPerformed
        MainReady.Start(12+Octave*12, ins);
    }//GEN-LAST:event_Key13ActionPerformed

    private void Key14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key14ActionPerformed
        MainReady.Start(13+Octave*12, ins);
    }//GEN-LAST:event_Key14ActionPerformed

    private void Key15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key15ActionPerformed
       MainReady.Start(14+Octave*12, ins);
    }//GEN-LAST:event_Key15ActionPerformed

    private void Key16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key16ActionPerformed
        MainReady.Start(15+Octave*12, ins);
    }//GEN-LAST:event_Key16ActionPerformed

    private void Key17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key17ActionPerformed
        MainReady.Start(16+Octave*12, ins);
    }//GEN-LAST:event_Key17ActionPerformed

    private void Key18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key18ActionPerformed
        MainReady.Start(17+Octave*12, ins);
    }//GEN-LAST:event_Key18ActionPerformed

    private void Key19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key19ActionPerformed
        MainReady.Start(18+Octave*12, ins);
    }//GEN-LAST:event_Key19ActionPerformed

    private void Key20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key20ActionPerformed
        MainReady.Start(19+Octave*12, ins);
    }//GEN-LAST:event_Key20ActionPerformed

    private void Key21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key21ActionPerformed
        MainReady.Start(20+Octave*12, ins);
    }//GEN-LAST:event_Key21ActionPerformed

    private void Key22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key22ActionPerformed
        MainReady.Start(21+Octave*12, ins);
    }//GEN-LAST:event_Key22ActionPerformed

    private void Key23ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Key23ActionPerformed
        MainReady.Start(22+Octave*12, ins);
    }//GEN-LAST:event_Key23ActionPerformed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        PlayByButton(evt.getKeyCode());
    }//GEN-LAST:event_formKeyPressed

    private void OctaveMoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OctaveMoreActionPerformed
        Octave++;
        if (Octave > 9){ Octave = 0;}
        OctaveField.setText(String.valueOf(Octave+1));
        SaveToFile(false);
    }//GEN-LAST:event_OctaveMoreActionPerformed

    private void OctaveLessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OctaveLessActionPerformed
        Octave--;
        if (Octave < 0){ Octave = 9;}
        OctaveField.setText(String.valueOf(Octave+1));
        SaveToFile(false);
    }//GEN-LAST:event_OctaveLessActionPerformed

    private void InstrumentMoreActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InstrumentMoreActionPerformed
        ins++;
        if (ins > maxIns){ ins = 0;}
        InstrumentField.setText(String.valueOf(ins+1)); 
        SaveToFile(false);
    }//GEN-LAST:event_InstrumentMoreActionPerformed

    private void InstrumentLessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_InstrumentLessActionPerformed
        ins--;
        if (ins < 0){ ins = maxIns;}
        InstrumentField.setText(String.valueOf(ins+1));        // TODO add your handling code here:
        SaveToFile(false);
    }//GEN-LAST:event_InstrumentLessActionPerformed

    int maxIns = 127;
    static boolean Disconnect = false;
    
    private void ConnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConnectButtonActionPerformed
        if (!(ServerStarted)){startServer();}
        if (ConnectButton.getText() == "Connect"){
            IP = ClientIPField.getText();
            ClientIPLabel.setText(Piano.IP+tmp);
            SaveToFile(true);
            ConnectButton.setText("Disconnect");
            Connect = true;
            Disconnect = false;
        } else {
            Disconnect = true;
            stopServer();
            ConnectButton.setText("Connect");
            //Connect = false;
        }
    }//GEN-LAST:event_ConnectButtonActionPerformed

    private void ClientIPFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ClientIPFieldKeyPressed
        PlayByButton(evt.getKeyCode());        // TODO add your handling code here:
    }//GEN-LAST:event_ClientIPFieldKeyPressed

    private void ClientIPFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ClientIPFieldKeyReleased
     // TODO add your handling code here:
    }//GEN-LAST:event_ClientIPFieldKeyReleased

    private void ClientIPFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_ClientIPFieldKeyTyped
        char c;
        c = evt.getKeyChar();
        //System.out.println(c);        
        
        /*if ((int) c != 17){}
        else if ((int) c == 1){jTextField1.selectAll();}
        if (((int) c != 3) && ((int) c != 22)) {}*/
        if (((int) c == 3)  || ((int) c == 22)  || ((int) c == 17)) {} else
        if (((c > '9')||(c < '0')) && (c != '.')) 
            if ((int) c != 8) {
                 if ((int) c == 1) {ClientIPField.selectAll();}
                 else {evt.consume(); this.requestFocus();}
        }
        if ((int)c != 8) if (ClientIPField.getText().length() > 14){evt.consume();}
        //System.err.println((int)c);
    }//GEN-LAST:event_ClientIPFieldKeyTyped

    private void ServerOnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ServerOnActionPerformed
        //System.out.println(appdata);
        startServer();        // TODO add your handling code here:
    }//GEN-LAST:event_ServerOnActionPerformed

    private void ServerOffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ServerOffActionPerformed
        stopServer();        // TODO add your handling code here:
    }//GEN-LAST:event_ServerOffActionPerformed

    private void SoundKeyStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SoundKeyStopActionPerformed
        MainReady.Start(1, 5);
        if (ServerStarted){
            try {
                            String IPsave = IP,tmpLast = tmp;
                            Socket cS = new Socket("127.0.0.1",Piano.port);
                            DataOutputStream outToServer = new DataOutputStream(cS.getOutputStream());
                            outToServer.writeBytes(String.valueOf(1)+' '+String.valueOf(5)+'\n');
                            cS.close();
                            Thread.sleep(20);
                            IP = IPsave;
                            ClientIPLabel.setText(Piano.IP+tmpLast);
                            /*ClientIP.setText(IP);
                            OctaveClientField.setText(octaveS);
                            InstrumentClientField.setText(instS);*/
                } catch (Exception ex) {
                            Logger.getLogger(MainSounds.class.getName()).log(Level.SEVERE, null, ex);
                        }
        }
    }//GEN-LAST:event_SoundKeyStopActionPerformed

    public boolean ServerStarted = false;
    
    static int DelayedServer=0, DelayedClient=0;
    
    public void startServer(){
        IP = ""; Connect = false;
        ServerStarted = true;
        server = new Thread(new Server(port));
        server.start();
        if (ConnectButton.getText() == "Disconnect"){ConnectButton.doClick();ConnectButton.doClick();}
        
        ServerOn.setEnabled(false);
        ServerOff.setEnabled(true);
        Two();
        //Two();
        //OctaveFieldClient.setText("");
        //InstrumentFieldClient.setText("");
        //ClientLabelIP.setText("");
    }
    
    public void stopServer(){
        ServerStarted = false;
        //MainReady.Start(-1, -1);
        Socket cS;
        try {
            cS = new Socket("127.0.0.1",Piano.port);
            DataOutputStream outToServer = new DataOutputStream(cS.getOutputStream());
            outToServer.writeBytes("1000");
            cS.close();
        } catch (IOException ex) {
            Logger.getLogger(Piano.class.getName()).log(Level.SEVERE, null, ex);
        }
        //server.interrupt();
        //ForClientThread.interrupt();
        //ClientLabelIP.setText("");
        ServerOn.setEnabled(true);
        ServerOff.setEnabled(false);
        One();
        //One();
        //ClientLabelIP.setText("");
    }
    
    public void restartServer(){
        stopServer();
        startServer();
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
            java.util.logging.Logger.getLogger(Piano.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Piano.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Piano.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Piano.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Piano().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField ClientIPField;
    private javax.swing.JLabel ClientIPLabel;
    private javax.swing.JButton ConnectButton;
    private javax.swing.JLabel InstrumentField;
    private javax.swing.JLabel InstrumentLabel;
    private javax.swing.JButton InstrumentLess;
    private javax.swing.JButton InstrumentMore;
    private javax.swing.JButton Key1;
    private javax.swing.JButton Key10;
    private javax.swing.JButton Key11;
    private javax.swing.JButton Key12;
    private javax.swing.JButton Key13;
    private javax.swing.JButton Key14;
    private javax.swing.JButton Key15;
    private javax.swing.JButton Key16;
    private javax.swing.JButton Key17;
    private javax.swing.JButton Key18;
    private javax.swing.JButton Key19;
    private javax.swing.JButton Key2;
    private javax.swing.JButton Key20;
    private javax.swing.JButton Key21;
    private javax.swing.JButton Key22;
    private javax.swing.JButton Key23;
    private javax.swing.JButton Key24;
    private javax.swing.JButton Key3;
    private javax.swing.JButton Key4;
    private javax.swing.JButton Key5;
    private javax.swing.JButton Key6;
    private javax.swing.JButton Key7;
    private javax.swing.JButton Key8;
    private javax.swing.JButton Key9;
    private javax.swing.JPanel Keys;
    private javax.swing.JLabel OctaveField;
    private javax.swing.JLabel OctaveLabel;
    private javax.swing.JButton OctaveLess;
    private javax.swing.JButton OctaveMore;
    private javax.swing.JLabel ServerLabel;
    private javax.swing.JButton ServerOff;
    private javax.swing.JButton ServerOn;
    private javax.swing.JButton SoundKeyStop;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables

}
