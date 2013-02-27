package edu.buffalo.cse.cse486586.groupmessenger;

import java.net.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class GroupMessengerActivity extends Activity {

	public static String avd_name;
	public static int avd_number;
	public static boolean isSequencer=false;
	public static Socket socket[]= new Socket[3];
	public static int recvPort = 10000;
	public static String ipAddr = "10.0.2.2";
	public static final String TAG="adil activity";
	public static int[] vector= new int[3];
	Handler uiHandle= new Handler();
	public static Map<Message, Integer> holdBack= new ConcurrentHashMap<Message,Integer>();
	
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
					if(isSequencer) {
			        	Thread t1= new Thread((Runnable)new Order());
			        	t1.start();
			        }
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
        		ObjectInputStream in =null;
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
        				in =new ObjectInputStream(sock1.getInputStream());
        				Message obj;
        				
        				//check for msg type msg,syn,test
        				
        				try {
							obj = (Message) in.readObject();
							if(obj.msg_id.equalsIgnoreCase("seq"))
								updateTextView(obj.msg);
							if(isSequencer && obj.msg_id.equalsIgnoreCase("msg"))
								Order.addtoList(obj);
							else if(obj.msg_id.equalsIgnoreCase("test")) {
								Order.addtoList(obj);
								//do some multicasting associated with test 2
							}
							else
								Order.updateMap(obj.msg);
							Log.i(TAG, "recvd msg: "+obj.msg);
						} catch (ClassNotFoundException e) {
							Log.e(TAG, e.getMessage());
						}
       				} 
        			
        			catch (IOException e) {
        				Log.e(TAG, ""+e.getMessage());
        				e.printStackTrace();
        			}
        			finally {
        				if (in!= null)
        					try {
        						in.close();
        					} catch (IOException e) {
        						Log.e(TAG, ""+e.getMessage());
        					}
        				if(sock1!=null)
        					try {
        						sock1.close();
        					} catch (IOException e) {
        						Log.e(TAG, ""+e.getMessage());
        					}	
        			}
        		}
        	}
        }).start();

        //start sequencer thread
    }

    public static void clientSockets() {
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


    public static void multicast(Message o) {
    //write down your multiocast function here	INCREMENT VECTOR BEFORE MULTICAST
    	//this method shall receive object as parameters not a String
    	Log.i(TAG, "multicasting "+o.msg);
    	clientSockets();
    	for(Socket soc: socket) {
    		try {
				ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());
				out.writeObject(o);
				out.close();
			} catch (IOException e) {
				Log.e(TAG, "Unable to multicast");
			}
    	}
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
    
    
    public void updateTextView(String message) {
    	final String msg= message;
    	uiHandle.post(new Runnable() {
    		public void run() {
    			TextView textView = (TextView)findViewById(R.id.textView1);
    			textView.setMovementMethod(new ScrollingMovementMethod());
    	    	Log.v(TAG, "updating textview");
    	    	//textView.append(msg+"\n");
    	    	textView.append(msg);
    	    	textView.append("\n");
    	    	Log.v(TAG, "updated textview");
    	    	/*ScrollView sc= (ScrollView)findViewById(R.id.scrollView1);
    	    	sc.fullScroll(View.FOCUS_DOWN);*/
    		}
    	});
    }
    
    public void test1(View view) {
    	new Thread(new Runnable() {
			public void run() {
				for (int i=0;i<=4; i++) {
					String str= avd_name+":"+Integer.toString(i);
					Message o= new Message("msg",str,avd_number,vector);
					multicast(o);
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
    	}).start();
    }
    
    
//    private class upTask extends AsyncTask<Integer, String, Void> {
//
//		protected Void doInBackground(Integer... arg0) {
//			while (true) {
//				if (Order.ls < Order.Ordered.size()) {
//					Order.ls++;
//					publishProgress(Order.Ordered.get(Order.ls));
//				}
//				else {
//					try {
//						Thread.sleep(200);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}
//		
//		protected void onProgressUpdate(String... s) {
//			TextView textView = (TextView)findViewById(R.id.textView1);
//			textView.setMovementMethod(new ScrollingMovementMethod());
//	    	Log.v(TAG, "updating textview");
//	    	//textView.append(msg+"\n");
//	    	textView.append(s[0]);
//	    	textView.append("\n");
//	    	Log.v(TAG, "updated textview");
//		}	
//    }
}


class Message implements Serializable {

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