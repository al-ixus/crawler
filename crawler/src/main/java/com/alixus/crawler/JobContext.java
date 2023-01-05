/*
 * Copyright (C) 2022 Al Ixus
 */

package com.alixus.crawler;


import java.util.*;


enum STATUS {
	ACTIVE,
	DONE
}

public class JobContext {

	private String sid;
	private String keyword;
	
	STATUS state;
	private static final Object stateLock = new Object();
	
	private List<Integer> paths;
	private static final Object pathsLock = new Object();

	private long tId;

	
	public JobContext(String searchId, String key) {

		sid = searchId;
		keyword = key;
		state = STATUS.ACTIVE;
		paths = new ArrayList<Integer>();
		tId = 0;
	}

	
	public String getId() {
		return sid;
	}

	
	public String getKeyword() {
		return keyword;
	}


	public synchronized void setStatus(STATUS st) {
		synchronized (stateLock) {
			state = st;
		}
	}

	
	public String getStatus() {
		synchronized (stateLock) {
			if(state == STATUS.ACTIVE)
				return "active";
			else
				return "done";
		}
	}


	public void addPathIndex(Integer idx) {
		synchronized (pathsLock) {
			paths.add(idx);
		}
	}

	
	public int getSize() {
		synchronized (pathsLock) {
			return paths.size();
		}
	}
	
	public String getUrlsString() {

		List<Integer> pathsClone = null;
		
		if(getStatus().equals("done") == true)
			pathsClone = paths;
		else {
			synchronized (pathsLock) {
				pathsClone = new ArrayList<Integer>(paths);
			}
		}
		
		String pt = "";
		String urls = "[";
		String baseUrl = Globals.getBaseUrl().toString();
		
		for(int i = 0; i < pathsClone.size(); i++) {
			pt = WebPageProfile.getPath(pathsClone.get(i));
			urls = urls + "\"" + baseUrl + "/" + pt + "\"";

			if(i+1 < pathsClone.size())
				urls += ",";
		}
		
		urls += "]";
		
		return urls;
	}


	public String getFullJson() {
		
		String ret = "{\"id\":\"" + this.getId() + "\",\"status\":\"" + this.getStatus() + "\",\"urls\":" + this.getUrlsString() + "}";

		return ret;
	}
	
	public synchronized boolean setTID(long id) {

		boolean res = true;
		
		if(this.tId == 0)
			this.tId = id;
		else
			res = false;
			
		return res;
	}

	
	public long getTID() {
		return tId;
	}
}
