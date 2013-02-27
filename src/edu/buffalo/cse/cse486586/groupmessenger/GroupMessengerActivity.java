package edu.buffalo.cse.cse486586.groupmessenger;

import java.net.*;
import java.util.ArrayList;
import java.io.*;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class GroupMessengerActivity extends Activity {

	public static String avd_name;
	public static int avd_number;
	public static boolean isSequencer=false;
	public static Socket socket[];
	public static int recvPort = 10000;
	public static String ipAddr = "10.0.2.2";
	public static final String TAG="adil activity";
	public static int[] vector= new int[3];
	public static ArrayList<Message> holdBack = new ArrayList<Message>();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setMovementMethod(new ScrollingMovementMethod());
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        
        //get avd name
        new Thread(new Runnable() {
			public void run() {
		    	String portStr = get_portStr();
		    	Log.v(TAG, portStr);
		    	
				if(portStr.equals("5554")) {
					avd_name= "avd0";
					avd_number= 0;
				}
				else if(portStr.equals("5556")) {
					avd_name= "avd1";
					avd_number=1;
					isSequencer=true;
				}
				else if(portStr.equals("5558")) {
					avd_name= "avd2";
					avd_number= 2;
				}
				else
					Log.d(TAG, "AVD portStr is neither 5554 nor 5556");
			}        	
        }).start();
        
        //server socket in a new thread and new class
        new Thread(new Runnable() {
        	public void run() {
        		Socket sock1= null;
        		DataInputStream din= null;
        		ServerSocket servSocket= null;
        		try {
					servSocket= new ServerSocket(recvPort);
					Log.v(TAG, "Server Socket port: "+Integer.toString(servSocket.getLocalPort()));
				} catch (IOException e) {
					Log.e(TAG, ""+e.getMessage());
					e.printStackTrace();
				}
        		
        		while(true) {
        			try {
        				sock1= servSocket.accept();
        				BufferedReader br= new BufferedReader(new InputStreamReader(sock1.getInputStream()));
        				String str= br.readLine();
        				//check for msg type msg,syn,test
        				Log.i(TAG, "recvd msg: "+str);
        				} 
        			
        			catch (IOException e) {
        				Log.e(TAG, ""+e.getMessage());
        				e.printStackTrace();
        			}
        			finally {
        				if (din!= null)
        					try {
        						din.close();
        					} catch (IOException e) {
        						Log.e(TAG, ""+e.getMessage());
        						e.printStackTrace();
        					}
        				if(sock1!=null)
        					try {
        						sock1.close();
        					} catch (IOException e) {
        						Log.e(TAG, ""+e.getMessage());
        						e.printStackTrace();
        					}	
        			}
        		}
        	}
        }).start();
        
        //client socket[3]
        new Thread(new Runnable() {

			public void run() {
				try {
    				socket[0]= new Socket(ipAddr, 11108);
    				socket[1]= new Socket(ipAddr, 11112);
    				socket[2]= new Socket(ipAddr, 11116);
    				Log.v(TAG, "send socket[3] created ");
    			} catch (UnknownHostException e) {
    				Log.e(TAG, ""+e.getMessage());
    				e.printStackTrace();
    			} catch (IOException e) {
    				Log.e(TAG, ""+e.getMessage());
    				e.printStackTrace();
    			}
			}
        }).start();
    }

    public void multicast(Object o) {
    //write down your multiocast function here	
    	//this method shall receive object as parameters not a String
    }
    
    
    public String get_portStr() {
    	TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
    	String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
    	return portStr;
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_group_messenger, menu);
        return true;
    }
    
}


class Message implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String msg_id;
	String msg;
	int avd_number;
	int vector[];
	
	Message(String msg_id, String msg, int avd_number, int[] v) {
		this.msg_id= msg_id;
		this.msg= msg;
		this.avd_number= avd_number;
		this.vector= v;
	}
}