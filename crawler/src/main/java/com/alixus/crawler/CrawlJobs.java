/*
 * Copyright (C) 2022 Al Ixus
 */

package com.alixus.crawler;


import java.util.*;


public class CrawlJobs {

	private static final Object jobsLock = new Object();
	private static Map<String,JobContext> jqs = new HashMap<String,JobContext>();
	

	public static boolean addJob(String id, JobContext searchCtx) {

		boolean ret = false;
		JobContext res = null;

		synchronized (jobsLock) {
			res = jqs.put(id, searchCtx);
		}
		
		if(res == null)  // adding successful
			ret = true;

		
		return ret;
	}

	public static JobContext deleteJob(String id) {

		JobContext ret = null;
		
		synchronized (jobsLock) {
			ret = jqs.remove(id);
		}

		
		return ret;
	}


	public static JobContext getJob(String id) {

		JobContext ret = null;
		
		synchronized (jobsLock) {
			ret = jqs.get(id);
		}

		
		return ret;
	}


	public static int getSize() {


		int ret = 0;
		
		synchronized (jobsLock) {
			ret = jqs.size();
		}

		
		return ret;
	}


	public static String getFullString() {

		String ret = "";

		synchronized (jobsLock) {
			ret = jqs.toString();
		}

		
		return ret;
	}
}
