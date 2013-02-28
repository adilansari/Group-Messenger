package edu.buffalo.cse.cse486586.groupmessenger;

import java.util.*;

import android.view.View;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

public class Order implements Runnable {
	static TextView mTextView;
	
	static int seq_no = 1;
	public static HashMap<String, Integer> deliver= new HashMap<String, Integer>();
	
	public static synchronized void updateMap(String msg) {
		//map updater
		//if msg already exists here than deirectly update content provider <msg,seq_no>
			//seq_no++;
		//else add msg to map
		
		if (deliver.containsKey(msg)) {
			//add to content provider
			//myHelper.insertPair(msg, Integer.toString(seq_no));
			//new GroupMessengerActivity().updateTextView(msg);
			//GroupMessengerActivity.Ordered.add(msg);
			final String str=msg;
			GroupMessengerActivity.uiHandle.post(new Runnable() {
	    		public void run() {
	    			TextView textView = mTextView;
	    			textView.setMovementMethod(new ScrollingMovementMethod());
	    	    	//Log.v(TAG, "updating textview");
	    	    	//textView.append(msg+"\n");
	    	    	textView.append(str);
	    	    	textView.append("\n");
	    	    	//Log.v(TAG, "updated textview");
	    	    	/*ScrollView sc= (ScrollView)findViewById(R.id.scrollView1);
	    	    	sc.fullScroll(View.FOCUS_DOWN);*/
	    		}
	    	});
			
			Log.d("adil", "FINAL" + msg);
			seq_no++;
			deliver.remove(msg);
		}
		else {
			deliver.put(msg, 1);
		}
	}
	
	public static void addtoList(Message m) {
		GroupMessengerActivity.holdBack.put(m, 1);
	}

	public static void removeList(Message m) {
		GroupMessengerActivity.holdBack.remove(m);
	}
	
	
	public static synchronized void updateVector(int a, int b, int c) {
    	GroupMessengerActivity.vector[0]=a;
    	GroupMessengerActivity.vector[1]=b;
    	GroupMessengerActivity.vector[2]=c;
    }

	public static boolean vectorCheck(int[] v, int a_num) {
		//do a vector check on rcvd vector and avd number
		if(v[a_num] == GroupMessengerActivity.vector[a_num]+1)
			if(v[(a_num+1)%3] <= GroupMessengerActivity.vector[(a_num+1)%3])
				if(v[(a_num+2)%3] <= GroupMessengerActivity.vector[(a_num+2)%3])
					return true;
		
		return false;
	}
	
	public static void alternate() {
		for(Message o: GroupMessengerActivity.holdBack.keySet()) {
			if(o.equals(null)) {
				break;
			}
			Log.w("adil", "sequecer at work");
			Order.updateMap(o.msg);
			Message s= new Message("seq",o.msg, GroupMessengerActivity.avd_number,GroupMessengerActivity.vector);
			GroupMessengerActivity.multicast(s);
			GroupMessengerActivity.holdBack.remove(o);
			// later for causal
			if(vectorCheck(o.vector,o.avd_number)) {
				//accept, update current vector, update map, remove from holdBack
			}
		}
	}
	@Override
	public void run() {
		//also consider isSequencer flag while accepting
		
		while(true) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			for(Message o: GroupMessengerActivity.holdBack.keySet()) {
				if(o.equals(null)) {
					break;
				}
				Log.w("adil", "sequecer at work");
				Order.updateMap(o.msg);
				Message s= new Message("seq",o.msg, GroupMessengerActivity.avd_number,GroupMessengerActivity.vector);
				GroupMessengerActivity.multicast(s);
				GroupMessengerActivity.holdBack.remove(o);
				// later for causal
				if(vectorCheck(o.vector,o.avd_number)) {
					//accept, update current vector, update map, remove from holdBack
				}
			}
		}
		
	}
	

}
