/*
 * Copyright (C) 2022 Al Ixus
 */

package com.alixus.crawler;


import java.net.*;
import java.util.*;


public class Globals  {

	private static URL base_url = null;         // -e Docker command
	private static int max_results = -1;        // -e Docker command
	private static int max_threads = 10;        // -e Docker command
	private static boolean pretty_jay = false;  // -e Docker command
	private static String messages = "";
	private static String appInstanceId = "";


	public static void setBaseUrl(String b) {

		String h = b;
		
		try {
			if(h == null) {
				h = new String("http://nobaseurl");
			}
			else {

				// check for http/https prefix..
				
				if(h.indexOf("http://") == -1) {
					if(h.indexOf("https://") == -1) 
						h = "http://" + h;  // FIX: use https by default instead?
				}

				
				// clean-off ANY/ALL trailing slashes '/'
				
				char a = h.charAt(h.length()-1);
				char c = '/';
				
				do {
					
					a = h.charAt(h.length()-1);
					
					if((a == c) == true) 
						h = h.substring(0, h.length()-1);
					
					if(h.length() < 9) {  // string cannot be less than "http://
						h = new String("http://badbaseurl");
						
						break;
					}
				} while (a == c);
			}
			
			
			base_url = new URL(h);
			
		} catch (MalformedURLException e) {}
		
	}

	public static URL getBaseUrl() {
		return base_url;
	}


	public static int setMaxResults(String s) {

		int res = 0;

		
		if(s == null)
			res = -1;  // default value..
		else {
			try {
				int z = Integer.parseInt(s);

				if(z < 1)
					z = -1;
				
				res = z;
			} catch (NumberFormatException e) {
				res = -1;
			}
		}

		
		max_results = res;

		
		return res;
	}

	public static int getMaxResults() {
		return max_results;
	}

	
	public static int setMaxThreads(String s) {

		int res = 0;


		// Default MAX_THREADS=10, if not specified at the start..
		
		if(s == null)
			res = 10;
		else {
			try {
				int z = Integer.parseInt(s);

				if(z < 1)
					z = 10;

				// FIX: what's the max we can allow?
				
				if(z > 50)
					z = 25;
				
				res = z;
			} catch (NumberFormatException e) {
				res = 10;
			}
		}

		
		max_threads = res;

		
		return res;
	}

	
	public static int getMaxThreads() {
		return max_threads;
	}


	public static void setPrettyJay(String s) {

		boolean res = false;

		if(s != null) {

			try {
				int z = Integer.parseInt(s);

				if(z > 0)
					res = true;
			} catch (NumberFormatException e) {}
		}


		pretty_jay = res;
	}


	public static boolean isPrettyJay() {
		return pretty_jay;
	}
	

	public static void setAppInstanceId(String dom) {
		String userPart = null;
		String domPart = dom;

		if(domPart == null)
			domPart = "autonomous";

		userPart = "agent-" + RandomString.generateString(4) + "-" + RandomString.generateString(4) + "-" + RandomString.generateString(4);
		userPart = userPart.toUpperCase();

		appInstanceId = userPart + "@" + domPart;

	}


	public static String getAppInstanceId() {
		return appInstanceId;
	}
	
	
	public static void addMessage(String str) {
		messages += "\n\n" + str;
	}
	
	
	public static String getMessages() {
		return messages;
	}
}
