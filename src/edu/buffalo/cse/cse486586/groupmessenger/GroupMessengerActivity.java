package edu.buffalo.cse.cse486586.groupmessenger;

import java.net.*;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.*;
import android.widget.EditText;
import android.widget.TextView;

public class GroupMessengerActivity extends Activity {

	public static String avd_name;
	public static int avd_number;
	public static boolean isSequencer=false;
	public static int recvPort = 10000;
	public static String ipAddr = "10.0.2.2";
	public static final String TAG="adil activity";
	public static int[] vector= new int[3];
	public static int[] soc= {11108,11112,11116};
	static Handler uiHandle= new Handler();
	static int mCount=0, tmCount=0;
	static int d_num=1, seq_num=1;
	public static Map<Integer, Message> toDeliver = new ConcurrentHashMap<Integer, Message>();
	public static ConcurrentLinkedQueue<Message> holdBack= new ConcurrentLinkedQueue<Message>();
	static ContentResolver cv;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);

        TextView tv = (TextView) findViewById(R.id.textView1);
        Order.mTextView =tv;
        tv.setMovementMethod(new ScrollingMovementMethod());
        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));
        cv= getContentResolver();
        new upTask().execute(0);
        
        
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
        		ObjectInputStream in =null;
        		ExecutorService te= Executors.newSingleThreadExecutor();
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
        				try {
							obj = (Message) in.readObject();
							te.execute(new ThreadExecute(obj));
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
    }



    public static void multicast(Message o) {
    	Log.i(TAG, "multicasting "+o.msg);
    	ExecutorService ex= Executors.newSingleThreadExecutor();
    	for(int i: soc) {
    		ex.execute(new MulticastExecute(i,o));
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
    
    
    public void test1(View view) {
    	new Thread(new Runnable() {
			public void run() {
				for (int i=0;i<=4; i++) {
					String str= avd_name+":"+Integer.toString(mCount++);
					Message o= new Message("msg",str,avd_number,Order.getVector(),0);
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
    
    public void test2(View view) {
    	new Thread(new Runnable() {
			public void run() {
				String str= avd_name+":"+Integer.toString(tmCount++);
				Message o= new Message("test",str,avd_number,Order.getVector(),0);
				multicast(o);
			}
    	}).start();
    }
    
    public void sendMessage(View view) {
    	EditText et= (EditText) findViewById(R.id.editText1);
    	final String msg= et.getText().toString();
    	et.setText("");
    	new Thread(new Runnable() {
    		public void run() {
    			Message o= new Message("msg",msg,avd_number,Order.getVector(),0);
				multicast(o);
    		}
    	}).start();
    }
    
    private class upTask extends AsyncTask<Integer, String, Void> {

		protected Void doInBackground(Integer... arg0) {
			while (true) {
				if(isSequencer)
					Order.Casual();
				Order.deliver();
			}
		}	
    }
}


class Message implements Serializable {

	private static final long serialVersionUID = 1L;
	String msg_id;
	String msg;
	int avd_number;
	int vector[];
	int seq_no;
	
	Message(String msg_id, String msg, int avd_number, int[] v, int s) {
		this.msg_id= msg_id;
		this.msg= msg;
		this.avd_number= avd_number;
		this.vector= Arrays.copyOf(v, v.length);
		this.seq_no= s;
	}
	
	Message(String msg_id, String msg, int s) {
		this.msg_id= msg_id;
		this.msg= msg;
		this.seq_no=s;
	}
}

class ThreadExecute implements Runnable {

	Socket sock= null;
	Message obj;
	ExecutorService e= Executors.newSingleThreadExecutor();
	ThreadExecute(Message s) {
		this.obj= s;
	}
	public void run() {
		if(obj.msg_id.equals("seq"))
			GroupMessengerActivity.toDeliver.put(obj.seq_no, obj);
		else if(obj.msg_id.equals("msg"))
			GroupMessengerActivity.holdBack.add(obj);
		else if(obj.msg_id.equals("test")) {
			GroupMessengerActivity.holdBack.add(obj);
			e.execute(new duoMulticast());
		}
		Log.i("adil executor", "recvd msg: "+obj.msg);
	}
}

class MulticastExecute implements Runnable {
	Socket sock=null;
	Message o;
	static final String TAG= "adil";
	MulticastExecute(int s, Message m) {
		try {
			this.sock= new Socket("10.0.2.2",s);
		} catch (UnknownHostException e) {
				Log.e(TAG, "socket "+e.getMessage());
				e.printStackTrace();
		} catch (IOException e) {
				Log.e(TAG, "socket "+e.getMessage());
				e.printStackTrace();
		}
		
		this.o=m;
	}
	
	public void run() {
		try {
			ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());
			out.writeObject(o);
			out.flush();
			out.close();
			sock.close();
		} catch (IOException e) {
			Log.e(TAG, "Multicast fail "+e.getMessage());
		}	
	}
}

class duoMulticast implements Runnable {
	public void run() {
		for (int i=0;i<=1; i++) {
			String str= GroupMessengerActivity.avd_name+":"+Integer.toString(GroupMessengerActivity.tmCount++);
			Message o= new Message("msg",str,GroupMessengerActivity.avd_number,Order.getVector(),0);
			GroupMessengerActivity.multicast(o);
		}
	}
}