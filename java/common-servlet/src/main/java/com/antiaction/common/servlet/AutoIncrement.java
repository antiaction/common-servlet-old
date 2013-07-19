/*
 * Created on 10/09/2011
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.mayhem.mailmanager;

public class AutoIncrement {

	protected int id = 0;

	public AutoIncrement() {
	}

	public synchronized int getId() {
		return ++id;
	}

}
