package edu.buffalo.cse.cse486586.groupmessenger;

import java.util.*;

import android.view.View;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

public class Order {
	
	static TextView mTextView;
	
	
	public static void updateMap(Message o) {
		GroupMessengerActivity.toDeliver.put(o,o.seq_no);
	}
	
	public static void deliver() {
		for (Message m: GroupMessengerActivity.toDeliver.keySet()) {
			if (m.equals(null))
				break;
			if (m.seq_no == GroupMessengerActivity.d_num) {
				final String str= m.msg;
				GroupMessengerActivity.uiHandle.post(new Runnable() {
					public void run() {
						TextView textView = mTextView;
						textView.setMovementMethod(new ScrollingMovementMethod());
						textView.append(str);
						textView.append("\n");
					}
	    		});
				GroupMessengerActivity.d_num++;
				GroupMessengerActivity.toDeliver.remove(m);
			}
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
	
	public static void causal() {
		for(Message o: GroupMessengerActivity.holdBack.keySet()) {
			if(o.equals(null)) {
				break;
			}
			Log.w("adil", "sequecer at work");
			//updateMap(o);
			Message s= new Message("seq",o.msg, GroupMessengerActivity.avd_number,GroupMessengerActivity.vector,GroupMessengerActivity.seq_num);
			GroupMessengerActivity.seq_num++;
			GroupMessengerActivity.multicast(s);
			GroupMessengerActivity.holdBack.remove(o);
			//updateMap(s);
			// later for causal
			if(vectorCheck(o.vector,o.avd_number)) {
				//accept, update current vector, update map, remove from holdBack
			}
		}
	}
	
//	public void run() {
//		//also consider isSequencer flag while accepting
//		
//		while(true) {
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			for(Message o: GroupMessengerActivity.holdBack.keySet()) {
//				if(o.equals(null)) {
//					break;
//				}
//				Log.w("adil", "sequecer at work");
//				Order.updateMap(o.msg);
//				Message s= new Message("seq",o.msg, GroupMessengerActivity.avd_number,GroupMessengerActivity.vector);
//				GroupMessengerActivity.multicast(s);
//				GroupMessengerActivity.holdBack.remove(o);
//				// later for causal
//				if(vectorCheck(o.vector,o.avd_number)) {
//					//accept, update current vector, update map, remove from holdBack
//				}
//			}
//		}
//		
//	}
	

}
