package edu.buffalo.cse.cse486586.groupmessenger;

import java.util.*;

public class Order implements Runnable {
	
	static int seq_no;
	public static HashMap<String, Integer> deliver= new HashMap<String, Integer>();
	
	
	public static synchronized void updateMap(String msg) {
		//map updater
		//if msg already exists here than deirectly update content provider <msg,seq_no>
			//seq_no++;
		//else add msg to map
	}
	
	public static void addtoList(Message m) {
		GroupMessengerActivity.holdBack.add(m);
	}

	public static void removeList(Message m) {
		GroupMessengerActivity.holdBack.remove(m);
	}
	
	
	public static synchronized void updateVector(int a, int b, int c) {
    	GroupMessengerActivity.vector[0]=a;
    	GroupMessengerActivity.vector[1]=b;
    	GroupMessengerActivity.vector[2]=c;
    }

	public boolean vectorCheck(int[] v) {
		//do a vector check
		return false;
	}
	@Override
	public void run() {
		//also consider isSequencer flag while accepting
		
	}
	

}
