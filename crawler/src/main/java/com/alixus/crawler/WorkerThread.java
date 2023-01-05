/*
 * Copyright (C) 2022 Al Ixus
 */

package com.alixus.crawler;


import java.net.*;
import java.io.*;
import java.util.*;


public class WorkerThread implements Runnable {

	private JobContext job;

	private Set<String> visitedPages;
	private int maxResults = 0;
	private int matchedPages = 0;
	
	public WorkerThread(JobContext jCtx) {
		this.job = jCtx;

		visitedPages = new HashSet<String>();
		maxResults = Globals.getMaxResults();
		matchedPages = 0;

		if(maxResults == -1) // no limit
			maxResults = 0;
	}


	public void run() {
		if(job.getStatus().equals("active"))  // if NOT already done..
			if(job.setTID(Thread.currentThread().getId()) == false)
				return;
		

		processJob();

		
		job.setStatus(STATUS.DONE);
		job.setTID(0);
	}


	private void processJob() {

		String path = "/";
		Stack<String> pages = new Stack<String>();

		pages.push(path);  // start from "/"

		do {			
			pages = doCrawling(pages);

			if(maxResults > 0) {
				if(maxResults == matchedPages)
					break;
			}
		} while(pages.size() !=0);
	}


	private Stack<String> doCrawling(Stack<String> pg) {
		
		String i = null;
		String fullPage = null;
		int idx = 0;
		URL url = null;
		URL uri = null;
		String path = null;
		Stack<String> pages = pg;
		Stack<String> emptyPages = new Stack<String>();
		Stack<String> linksFound = new Stack<String>();

		path = pages.pop();

		if(visitedPages.contains(path) == true)
			return pages;

		String host = Globals.getBaseUrl().toString();
		

		// first: see if keyword is found in this page.
		
		try {
			uri = new URL(host);
			url = new URL(uri, path);
			
			URLConnection urlc = url.openConnection();
			urlc.setConnectTimeout(5000);
			urlc.setReadTimeout(5000);
			
			InputStream ins = urlc.getInputStream();

			BufferedReader read = new BufferedReader(new InputStreamReader(ins));
		
			while((i = read.readLine()) != null) {
				fullPage += i.toLowerCase();
			}
			
			read.close();
		} catch (IOException e) {

			// this is just for testing purposes..

			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionAsString = sw.toString();
			Globals.addMessage(exceptionAsString);


			// remove current path from the further attempts in this crawl..
			
			visitedPages.add(path);

			return pages;
		}


		String k = job.getKeyword().toLowerCase();
		idx = fullPage.indexOf(k);
		
		if(idx != -1) {
			idx = WebPageProfile.addPath(path);
			job.addPathIndex(idx);
			matchedPages++;
		}

		visitedPages.add(path);
			


		// second: build a list of links 
		
		String sstr = null;
		String pt = fullPage;
		boolean keepIterating = true;

		String href = "href=\"";
		int refsz = href.length();
		
		
		do {
			idx = pt.indexOf("href=\"");
			if(idx != -1) {
				pt = pt.substring(idx + refsz, pt.length());

				idx = pt.indexOf("\"");
				if(idx != -1) {
					sstr = pt.substring(0, idx);

					if(isValidPath(sstr) == true) {
						linksFound.push(sstr);
					}

					pt = pt.substring(idx, pt.length());
				}
				else
					keepIterating = false;
			}
			else
				keepIterating = false;
		} while (keepIterating);
		
		while(linksFound.size() > 0)
			pages.push((String)linksFound.pop());

		return pages;
	}


	private boolean isValidPath(String str) {

		int idx = str.indexOf("..");  // do NOT allow any manipulated path..
		
		if(idx != -1)
			return false;

		idx = str.indexOf("http:");

		if(idx != -1)
			return false;

		idx = str.indexOf("https:");

		if(idx != -1)
			return false;
		
		idx = str.indexOf(".html");

		if(idx == -1)
			return false;

		
		return true;
	}
		
}
