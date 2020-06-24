package com.emotiv.epoc;

import com.emotiv.epoc.EmoState.EE_ExpressivAlgo_t;//////////？
import com.emotiv.ui.Gui;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.*;


//import comm.ContinueRead;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import java.awt.*;

/**
 * Simple example of JNA interface mapping and usage.
 */
public class EmoStateLog extends JFrame implements ActionListener,Runnable
{
	static MyPanel mb=null;  GuankaPanel guankaPanel=null;  //自建两个面板用于切换
	JPanel mb1,mb2,mb3;
    JButton[] an={null,null};
    JLabel bq1,bq2;
    JTextField wbk;
    JPasswordField mmk;
    static volatile boolean flag;
    static int keyColornum=1;
    static Point mousepoint = MouseInfo.getPointerInfo().getLocation();
    static int location_a,location_b;
    static boolean mbgo=true;
    public static void main(String[] args) throws InterruptedException 
    {
    	
    	EmoStateLog bb=new EmoStateLog();
    	new Thread(bb).start();
    	try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	MyMouseController mmc=new MyMouseController();
        boolean COMPOSER = false;
        boolean EPOC = true;
        /**
        //double[] buffer = new double[23];
         **/
        //////////// MODE
        boolean mode = COMPOSER;
        Pointer eEvent = Edk.INSTANCE.EE_EmoEngineEventCreate();
        Pointer eState = Edk.INSTANCE.EE_EmoStateCreate();
        IntByReference userID = null;
        short composerPort = (mode ? (short) 3008 : (short) 1726);
        int option = (mode ? 2 : 1);
        int state = 0;
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (Exception e) {
            System.out.println("Robot failure");
        }
        location_a=mousepoint.x;
        location_b=mousepoint.y;
        userID = new IntByReference(0);

        switch (option) {
            case 1: {
                if (Edk.INSTANCE.EE_EngineConnect("Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) {
                    System.out.println("Emotiv Engine start up failed.");
                    return;
                }
                break;
            }
            case 2: {
                System.out.println("Target IP of EmoComposer: [127.0.0.1] ");

                if (Edk.INSTANCE.EE_EngineRemoteConnect("127.0.0.1", composerPort, "Emotiv Systems-5") != EdkErrorCode.EDK_OK.ToInt()) {
                    System.out.println("Cannot connect to EmoComposer on [127.0.0.1]");
                    return;
                }
                System.out.println("Connected to EmoComposer on [127.0.0.1]");
                break;
            }
            default:
                System.out.println("Invalid option...");
                return;
        }
//        System.out.println(GP);
//        while(EmoStateLog.GP){
//        	if(GP==false){
//        		break;
//        	}
//        }
       
        while (true) {
//        	System.out.println(1);
            state = Edk.INSTANCE.EE_EngineGetNextEvent(eEvent);
            // 输出各通道的信号值
//            System.out.println(Edk.INSTANCE.EE_DataGet(eState, 0, buffer, 10));buffer
//            Edk.INSTANCE.EE_DataGet(hData, channel, buffer, bufferSizeInSample);//////////////得到通道的信号
            //New event needs to be handled
            if (state == EdkErrorCode.EDK_OK.ToInt()) {
                int eventType = Edk.INSTANCE.EE_EmoEngineEventGetType(eEvent);
                Edk.INSTANCE.EE_EmoEngineEventGetUserId(eEvent, userID);

                // Log the com.emotiv.epoc.EmoState if it has been updated
                if (eventType == Edk.EE_Event_t.EE_EmoStateUpdated.ToInt()) {
                    Edk.INSTANCE.EE_EmoEngineEventGetEmoState(eEvent, eState);
//                	EE_ExpressivAlgo_t upperFaceType = ES_ExpressivGetUpperFaceAction(eState);
                    float timestamp = EmoState.INSTANCE.ES_GetTimeFromStart(eState);
                    //System.out.println(timestamp + " : New com.emotiv.epoc.EmoState from user " + userID.getValue());
                    //System.out.print("WirelessSignalStatus: ");
                    //System.out.println(com.emotiv.epoc.EmoState.INSTANCE.ES_GetWirelessSignalStatus(eState));

                    int action = EmoState.INSTANCE.ES_CognitivGetCurrentAction(eState);
                    double power = EmoState.INSTANCE.ES_CognitivGetCurrentActionPower(eState);
                    if (power != 0) {
                        if (action == EmoState.EE_CognitivAction_t.COG_LEFT.ToInt()) {
                            //System.out.println("Left. Power: " + power);
                            robot.keyPress(KeyEvent.VK_UP);
                            robot.keyRelease(KeyEvent.VK_UP);
                        }
                        if (action == EmoState.EE_CognitivAction_t.COG_RIGHT.ToInt()) {
                            //System.out.println("Right. Power: " + power);
                            robot.keyPress(KeyEvent.VK_DOWN);
                            robot.keyRelease(KeyEvent.VK_DOWN);
                        }
                    }
                    if(flag){
                    	if (EmoState.INSTANCE.ES_ExpressivGetSmileExtent(eState) == 1){
                        	if(location_b!=(mousepoint.y+145)){
                    		location_b+=145;
                        	mmc.Move(0, 145);                    	
                        	keyColornum-=10;
//                        	System.out.print("111");
                        	Thread.sleep(10);
                       	}
                    }                                  	
             	 
                  
//                	    if(EmoState.INSTANCE.ES_ExpressivGetClenchExtent(eState)==1){
//                            robot.mousePress(KeyEvent.BUTTON1_MASK);//鼠标单击
//                            try {
//								Thread.sleep(10);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//                            robot.mouseRelease(KeyEvent.BUTTON1_MASK);
//                            Thread.sleep(10);                   	                   	
//                    	System.out.println("按下按钮");                    	
//                    }
                   
                  
                   		if (EmoState.INSTANCE.ES_ExpressivGetEyebrowExtent(eState) == 1) {
                    	if(location_b!=(mousepoint.y-145)){
                    		location_b-=145;
                        	mmc.Move(0, -145);
                        	keyColornum+=10;
                        	Thread.sleep(10);
                    	}
                   	
                   		}
                    		
                        
                    if (EmoState.INSTANCE.ES_ExpressivIsLookingLeft(eState) == 1) {
//                        System.out.println("左...");
                    	if(location_a!=mousepoint.x){
                    		location_a-=400;
	                        mmc.Move(-400, 0);
	                        keyColornum-=1;
	                        Thread.sleep(10);
//	                        oneTouch=true;  
                    	}
                    	                      
                    }

                    if (EmoState.INSTANCE.ES_ExpressivIsLookingRight(eState) == 1) {
//                        System.out.println("右...");
                    	if(location_a!=(mousepoint.x+1200)){
                    		location_a+=400;
	                        mmc.Move(400, 0);
	                        keyColornum+=1;
	                        Thread.sleep(10);
                    	}                                
                    }
                        if(EmoState.INSTANCE.ES_ExpressivGetClenchExtent(eState)==1){
                            robot.mousePress(KeyEvent.BUTTON1_MASK);//鼠标单击
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            robot.mouseRelease(KeyEvent.BUTTON1_MASK);
                            Thread.sleep(10);
                            System.out.println("按下按钮");
                        }
             }

                  
        
            if(keyColornum==1){
            	mb.an[0].setBackground(Color.YELLOW);
            	for(int i=0;i<4;i++){            	
            		mb.anJ[i].setBackground(Color.WHITE);
            	}
            	for(int i=1;i<=7;i++){
            		mb.an[i].setBackground(Color.WHITE);            
            	}
            }
            if(keyColornum==2){
            	mb.an[1].setBackground(Color.yellow);
            	for(int i=0;i<4;i++){            	
            		mb.anJ[i].setBackground(Color.WHITE);
            	}
            	mb.an[0].setBackground(Color.white);
            	for(int i=2;i<=7;i++){
            		mb.an[i].setBackground(Color.white);
            	}
            }
            if(keyColornum==3){
            	mb.an[2].setBackground(Color.yellow);
            	for(int i=0;i<4;i++){            	
            		mb.anJ[i].setBackground(Color.WHITE);
            	}
            	mb.an[0].setBackground(Color.white);
            	mb.an[1].setBackground(Color.white);
            	for(int i=3;i<=7;i++){
            		mb.an[i].setBackground(Color.white);
            	}
            }
            if(keyColornum==4){
            	mb.an[3].setBackground(Color.yellow);
            	for(int i=0;i<4;i++){            	
            		mb.anJ[i].setBackground(Color.WHITE);
            	}
            	mb.an[0].setBackground(Color.white);
            	mb.an[1].setBackground(Color.white);
            	mb.an[2].setBackground(Color.white);
            	for(int i=4;i<=7;i++){
            		mb.an[i].setBackground(Color.white);
            	}
            }
            if(keyColornum==-9){
            	mb.an[4].setBackground(Color.yellow);
            	for(int i=0;i<4;i++){            	
            		mb.anJ[i].setBackground(Color.WHITE);
            	}
            	for(int i=0;i<=3;i++){
            		mb.an[i].setBackground(Color.white);
            	}
            	for(int i=5;i<=7;i++){
            		mb.an[i].setBackground(Color.white);
            	}
            }
            if(keyColornum==-8){
            	mb.an[5].setBackground(Color.yellow);
            	for(int i=0;i<4;i++){            	
            		mb.anJ[i].setBackground(Color.WHITE);
            	}
            	for(int i=0;i<=4;i++){
            		mb.an[i].setBackground(Color.white);
            	}
            	for(int i=6;i<=7;i++){
            		mb.an[i].setBackground(Color.white);
            	}
            }
            if(keyColornum==-7){
            	mb.an[6].setBackground(Color.yellow);
            	for(int i=0;i<4;i++){            	
            		mb.anJ[i].setBackground(Color.WHITE);
            	}
            	for(int i=0;i<=5;i++){
            		mb.an[i].setBackground(Color.white);
            	}
            	mb.an[7].setBackground(Color.white);
            }
            if(keyColornum==-6){
            	mb.an[7].setBackground(Color.yellow);
            	for(int i=0;i<4;i++){            	
            		mb.anJ[i].setBackground(Color.WHITE);
            	}
            	for(int i=0;i<=6;i++){            	
            		mb.an[i].setBackground(Color.WHITE);
            	}
            }
            if(keyColornum==11){
            	mb.anJ[0].setBackground(Color.yellow);
            	for(int i=1;i<4;i++){            	
            		mb.anJ[i].setBackground(Color.WHITE);
            	}
            	for(int i=0;i<=7;i++){            	
            		mb.an[i].setBackground(Color.WHITE);
            	}
            }
            if(keyColornum==12){
            	mb.anJ[1].setBackground(Color.yellow);
            	mb.anJ[0].setBackground(Color.white);
            	mb.anJ[2].setBackground(Color.white);
            	mb.anJ[3].setBackground(Color.white);
            	for(int i=0;i<=7;i++){            	
            		mb.an[i].setBackground(Color.WHITE);
            	}
            }
            if(keyColornum==13){
            	mb.anJ[1].setBackground(Color.white);
            	mb.anJ[0].setBackground(Color.white);
            	mb.anJ[2].setBackground(Color.yellow);
            	mb.anJ[3].setBackground(Color.white);
            	for(int i=0;i<=7;i++){            	
            		mb.an[i].setBackground(Color.WHITE);
            	}
            }
            if(keyColornum==14){
            	mb.anJ[1].setBackground(Color.white);
            	mb.anJ[0].setBackground(Color.white);
            	mb.anJ[2].setBackground(Color.white);
            	mb.anJ[3].setBackground(Color.yellow);
            	for(int i=0;i<=7;i++){            	
            		mb.an[i].setBackground(Color.WHITE);
            	}
            }
        }
            } else if (state != EdkErrorCode.EDK_NO_EVENT.ToInt()) {
                System.out.println("Internal error in Emotiv Engine!");
                break;
            }       
        }
        Edk.INSTANCE.EE_EngineDisconnect();
        System.out.println("Disconnected!");
    }
    public EmoStateLog(){
    	mb1=new JPanel();
        mb2=new JPanel();
        mb3=new JPanel();

        an[0]=new JButton("登录");
        an[1]=new JButton("退出");
        an[0].addActionListener(this);
        an[0].setActionCommand("start");
        an[1].addActionListener(this);
        an[1].setActionCommand("exit");

        bq1=new JLabel("用户名");
        bq2=new JLabel("密    码");
        wbk=new JTextField(10);
        mmk=new JPasswordField(10);
        guankaPanel=new GuankaPanel(mb1,mb2,mb3,an,bq1,bq2,wbk,mmk);
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getActionCommand().equals("start")){
            try {
                mb=new MyPanel();
                mbgo=false;
            } catch (AWTException ex) {
                ex.printStackTrace();
            }
//            mb.setLayout(null);
            this.remove(guankaPanel);
            guankaPanel.setVisible(false);
//            this.addKeyListener(mb);
//            Thread t=new Thread(mb);
//            t.start();
        }else if(e.getActionCommand().equals("exit")){
            System.exit(0);
        }
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			flag=true;
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			flag=false;
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}		             
}
class GuankaPanel extends JFrame {
    JPanel mb1,mb2,mb3;
    JButton[] an={null,null};
    JLabel bq1,bq2;
    JTextField wbk;
    JPasswordField mmk;

    public GuankaPanel(JPanel mb1, JPanel mb2, JPanel mb3, JButton[] an, JLabel bq1, JLabel bq2, JTextField wbk, JPasswordField mmk) throws HeadlessException {
        this.mb1 = mb1;
        this.mb2 = mb2;
        this.mb3 = mb3;
        this.an = an;
        this.bq1 = bq1;
        this.bq2 = bq2;
        this.wbk = wbk;
        this.mmk = mmk;
        this.setLayout(new GridLayout(3,1));

        mb1.add(bq1); mb1.add(wbk);
        mb2.add(bq2); mb2.add(mmk);
        mb3.add(an[0]); mb3.add(an[1]);

        this.add(mb1);
        this.add(mb2);
        this.add(mb3);
        ImageIcon title = new ImageIcon("C:/IDEAproject/JUC/src/keyboard/timg.jpg");
        this.setIconImage(title.getImage());
        this.setTitle("吉林大学陈老师课题组");
        this.setSize(500,200);
        this.setLocation(600,500);
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}
class  MyPanel extends JFrame implements ActionListener {
    JButton[] an={null,null,null,null,null,null,null,null,null};//闪烁按键
    JButton[] anJ={null,null,null,null};
//    JPanel panel=null;
    JPanel panel2=null;
    JTextArea textArea=null;
    JScrollPane scrollPane=null;
    int s=8;
    int color=0;
    private int flag;


    public MyPanel() throws AWTException {
        textArea=new JTextArea();
        textArea.setFont(new Font("黑体",Font.BOLD,128));
        scrollPane= new JScrollPane(textArea);

        anJ[0]= new JButton("1");
        anJ[1]= new JButton("2");
        anJ[2]= new JButton("3");
        anJ[3]= new JButton("4");
        anJ[0].addActionListener(this);
        anJ[0].setActionCommand("1");
        anJ[1].addActionListener(this);
        anJ[1].setActionCommand("2");
        anJ[2].addActionListener(this);
        anJ[2].setActionCommand("3");
        anJ[3].addActionListener(this);
        anJ[3].setActionCommand("4");
        for (int i = 0; i < 4; i++) {
            anJ[i].setFont(new Font("宋体",Font.PLAIN,32));
        }

        an[0]=new JButton("，。？");
        an[1]=new JButton("ABCD");
        an[2]=new JButton("EFGH");
        an[3]=new JButton("IJKL");
        an[4]=new JButton("MNOP");
        an[5]=new JButton("QRST");
        an[6]=new JButton("UVW");
        an[7]=new JButton("XYZ");
        an[0].addActionListener(this);
        an[0].setActionCommand(",");
        color=1;
        an[1].addActionListener(this);
        an[1].setActionCommand("A");
        an[2].addActionListener(this);
        an[2].setActionCommand("E");
        an[3].addActionListener(this);
        an[3].setActionCommand("I");
        an[4].addActionListener(this);
        an[4].setActionCommand("M");
        an[5].addActionListener(this);
        an[5].setActionCommand("Q");
        an[6].addActionListener(this);
        an[6].setActionCommand("U");
        an[7].addActionListener(this);
        an[7].setActionCommand("X");
//        an[8].addActionListener(this);
//        an[8].setActionCommand("W");

        for (int i = 0; i < s; i++) {
            an[i].setFont(new Font("宋体",Font.PLAIN,30));
            an[i].setSize(200,400);
        }

        panel2=new JPanel();

        for (int i = 0; i < 4; i++) {
            panel2.add(anJ[i]);
        }
        for(int i=0;i<s;i++)
        {
            panel2.add(an[i]);
        }
//        panel.setLayout(new GridLayout(1,4,14,14));
        panel2.setLayout(new GridLayout(3,4,14 ,14));//网格布局，3*3个   14*14 按钮之间的空隙大小
//        this.setTitle("网格布局");
        this.setSize(2000, 1000);
        this.setLayout(new GridLayout(2,1));
        this.add(scrollPane);
//        this.add(panel);
        this.add(panel2);
        this.setLocation(-10, 20);
        this.setResizable(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        if(e.getActionCommand().equals(",")){
            flag=1;

        }
        if(flag==1){
            if (e.getActionCommand().equals("1")) {
                textArea.setText(textArea.getText() + ",");
            }
            if (e.getActionCommand().equals("2")) {
                textArea.setText(textArea.getText() + "。");
            }

            if (e.getActionCommand().equals("3")) {
                textArea.setText(textArea.getText() + "?");
            }
        }
        if(e.getActionCommand().equals("A")){
            flag=2;
        }
        if(flag==2){
            if (e.getActionCommand().equals("1")) {
                textArea.setText(textArea.getText() + "A");
            }
            if (e.getActionCommand().equals("2")) {
                textArea.setText(textArea.getText() + "B");
            }

            if (e.getActionCommand().equals("3")) {
                textArea.setText(textArea.getText() + "C");
            }
            if (e.getActionCommand().equals("4")) {
                textArea.setText(textArea.getText() + "D");
            }
        }
        if(e.getActionCommand().equals("E")){
            flag=3;
        }
        if(flag==3){
            if (e.getActionCommand().equals("1")) {
                textArea.setText(textArea.getText() + "E");
            }
            if (e.getActionCommand().equals("2")) {
                textArea.setText(textArea.getText() + "F");
            }

            if (e.getActionCommand().equals("3")) {
                textArea.setText(textArea.getText() + "G");
            }
            if (e.getActionCommand().equals("4")) {
                textArea.setText(textArea.getText() + "H");
            }
        }
        if(e.getActionCommand().equals("I")){
            flag=4;
        }
        if(flag==4){
            if (e.getActionCommand().equals("1")) {
                textArea.setText(textArea.getText() + "I");
            }
            if (e.getActionCommand().equals("2")) {
                textArea.setText(textArea.getText() + "J");
            }

            if (e.getActionCommand().equals("3")) {
                textArea.setText(textArea.getText() + "K");
            }
            if (e.getActionCommand().equals("4")) {
                textArea.setText(textArea.getText() + "L");
            }
        }
        if(e.getActionCommand().equals("M")){
            flag=5;
        }
        if(flag==5){
            if (e.getActionCommand().equals("1")) {
                textArea.setText(textArea.getText() + "M");
            }
            if (e.getActionCommand().equals("2")) {
                textArea.setText(textArea.getText() + "N");
            }

            if (e.getActionCommand().equals("3")) {
                textArea.setText(textArea.getText() + "O");
            }
            if (e.getActionCommand().equals("4")) {
                textArea.setText(textArea.getText() + "P");
            }
        }
        if(e.getActionCommand().equals("Q")){
            flag=6;
        }
        if(flag==6){
            if (e.getActionCommand().equals("1")) {
                textArea.setText(textArea.getText() + "Q");
            }
            if (e.getActionCommand().equals("2")) {
                textArea.setText(textArea.getText() + "R");
            }

            if (e.getActionCommand().equals("3")) {
                textArea.setText(textArea.getText() + "S");
            }
            if (e.getActionCommand().equals("4")) {
                textArea.setText(textArea.getText() + "T");
            }
        }
        if(e.getActionCommand().equals("U")){
            flag=7;
        }
        if(flag==7){
            if (e.getActionCommand().equals("1")) {
                textArea.setText(textArea.getText() + "U");
            }
            if (e.getActionCommand().equals("2")) {
                textArea.setText(textArea.getText() + "V");
            }

            if (e.getActionCommand().equals("3")) {
                textArea.setText(textArea.getText() + "W");
            }
        }
        if(e.getActionCommand().equals("X")){
            flag=8;
        }
        if(flag==8){
            if (e.getActionCommand().equals("1")) {
                textArea.setText(textArea.getText() + "X");
            }
            if (e.getActionCommand().equals("2")) {
                textArea.setText(textArea.getText() + "Y");
            }

            if (e.getActionCommand().equals("3")) {
                textArea.setText(textArea.getText() + "Z");
            }
        }
    }
}
class MyMouseController{
	private Robot robot;
	public MyMouseController(){
		try {
			robot=new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void Move(int width,int heigh){
			Point mousepoint=MouseInfo.getPointerInfo().getLocation();
			width+=mousepoint.x;
			heigh+=mousepoint.y;
			try
			{
			robot.delay(300);
			robot.mouseMove(width, heigh);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}			
    }
}
