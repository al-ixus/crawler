/*
 * Copyright (C) 2022 Al Ixus
 */

package com.alixus.crawler;


import java.util.*;
import java.util.concurrent.*;


public class ThreadPool {

	private static ExecutorService executor;


	public ThreadPool(int n) {
		executor = Executors.newFixedThreadPool(n);
	}


	public static void dispatch(JobContext jobCtx) {

	      	Runnable worker = new WorkerThread(jobCtx);
		
		executor.execute(worker);
	}

}
