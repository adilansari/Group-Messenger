package edu.buffalo.cse.cse486586.groupmessenger;

import java.util.*;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class Order {
	
	static TextView mTextView;
	static int t=0;
	static int a[]= new int[3];
	
	public static void updateMap(Message o) {
		GroupMessengerActivity.toDeliver.put(o.seq_no,o);
	}
	
	public static void deliver() {
			if (GroupMessengerActivity.toDeliver.containsKey(GroupMessengerActivity.d_num)) {
				Message m= (Message) GroupMessengerActivity.toDeliver.get(GroupMessengerActivity.d_num);
				final String str= m.msg;
				new Thread(new Runnable() {
					public void run() {
						myHelper.insertPair(Integer.toString(GroupMessengerActivity.d_num), str, GroupMessengerActivity.cv);
					}
				}).start();
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
	
	
	public static synchronized void setVector(int v[],int avd) { //avd is incoming avd_number
    	GroupMessengerActivity.vector[avd]= GroupMessengerActivity.vector[avd]+1;
    	GroupMessengerActivity.vector[(avd+1)%3]= Math.max(GroupMessengerActivity.vector[(avd+1)%3], v[(avd+1)%3]);
    	GroupMessengerActivity.vector[(avd+2)%3]= Math.max(GroupMessengerActivity.vector[(avd+2)%3], v[(avd+2)%3]);
    }
	
	public static synchronized int[] getVector() {
		int[] p = Arrays.copyOf(GroupMessengerActivity.vector, GroupMessengerActivity.vector.length);
		++p[GroupMessengerActivity.avd_number];
		return p;
	}

	public static boolean vectorCheck(int[] v, int a_num) {//do a vector check on rcvd vector and recvd avd number
		boolean b= false;
		if(v[a_num] == GroupMessengerActivity.vector[a_num]+1)
			if(v[(a_num+1)%3] <= GroupMessengerActivity.vector[(a_num+1)%3])
				if(v[(a_num+2)%3] <= GroupMessengerActivity.vector[(a_num+2)%3])
					b= true;
		
		return b;
	}

	public static void causal() {
		Iterator<Message> itr = GroupMessengerActivity.holdBack.iterator();
		while(itr.hasNext()) {
			Message m= (Message) itr.next();
			if(vectorCheck(m.vector,m.avd_number)) {
				setVector(m.vector,m.avd_number);
				if (GroupMessengerActivity.isSequencer) {
					Message s= new Message("seq",m.msg,GroupMessengerActivity.seq_num);
					GroupMessengerActivity.seq_num++;
					GroupMessengerActivity.multicast(s);
				}
				GroupMessengerActivity.holdBack.remove();
			}
		}
	}

	
	public static void Casual() {
		if(!GroupMessengerActivity.holdBack.isEmpty()) {
			Message o= GroupMessengerActivity.holdBack.poll();
			if(o.equals(null))
				return;
			Message s= new Message("seq",o.msg,GroupMessengerActivity.seq_num);
			GroupMessengerActivity.seq_num++;
			GroupMessengerActivity.multicast(s);
		}
	}
	
	public static void Cascast( Message o) {
		Message s= new Message("seq",o.msg,GroupMessengerActivity.seq_num);
		GroupMessengerActivity.seq_num++;
		GroupMessengerActivity.multicast(s);
	}
}
