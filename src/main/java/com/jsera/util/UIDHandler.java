package com.jsera.util;

import java.util.Vector;

public class UIDHandler {

	private final Vector<Integer> openSlots = new Vector<Integer>();
	private int cID = 0;

	public synchronized int getUID() {
		int nId;
		if (this.openSlots.size() == 0)
			nId = this.cID++;
		else {
			nId = this.openSlots.remove(this.openSlots.size() - 1).intValue();
		}
		return nId;
	}

	public synchronized void releaseUID(int nID) {
		if (nID < this.cID)
			this.openSlots.add(new Integer(nID));
		else
			System.out
					.println("UIDHandler tried to release a UID that's higher than cID");
	}
}
