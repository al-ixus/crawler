/*
 * Copyright (C) 2022 Al Ixus
 */

package com.alixus.crawler;


import static spark.Spark.*;
import com.google.gson.*;
import java.util.*;


public class Main {
	
	public static void main(String[] args) {
		
		String bse_url = System.getenv("BASE_URL");
		String max_res = System.getenv("MAX_RESULTS");
		String pty_jay = System.getenv("PRETTY_JAY");
		String max_thr = System.getenv("MAX_THREADS");
		
		Globals.setBaseUrl(bse_url);
		Globals.setMaxResults(max_res);
		Globals.setPrettyJay(pty_jay);
		Globals.setMaxThreads(max_thr);
		Globals.setAppInstanceId("testdomain.com");
				
		ThreadPool tpool = new ThreadPool(Globals.getMaxThreads());
		

		get("/crawl/:id", (req, res) -> {

				String resp = null;
				String sid = req.params("id");
				JobContext job = CrawlJobs.getJob(sid);
				
				if(job == null) {
					res.status(404);
					return "{\"status\":404,\"message\":\"crawl not found: " + sid + "\"}";
				}
				else
					resp = job.getFullJson();

				
				if(Globals.isPrettyJay())
					resp = JsonOps.jsonPretty(resp);

				res.header("Content-Length", Integer.toString(resp.length()));


				return resp + System.lineSeparator();
			});

		
		post("/crawl", (req, res) -> {

				String body = req.body();
				
				String resp = CrawlBodyCheck(body);

				if(resp == null) {
					resp = StartSearch(body);

					if(resp == null) {
						res.status(500);

						resp = "{\"status\":500,\"message\":\"something went wrong\"}";
					}
				}
				else   // we have an error message..
					res.status(400);


				if(Globals.isPrettyJay())
					resp = JsonOps.jsonPretty(resp);
				
				res.header("Content-Length", Integer.toString(resp.length()));
				
				
				return resp + System.lineSeparator();
			});
		
		
		post("/config", (req, res) -> {
				
				String body = req.body();
				
				String resp = ConfigBodyCheck(body);
				
				if(resp != null)  // we have an error message..
					res.status(400);
				else {
					ConfigChangesExecute(body);
					
					resp = "{ \"resp\" : \"well done\" }";

					if(Globals.isPrettyJay())
						resp = JsonOps.jsonPretty(resp);
				}
					
				res.header("Content-Length", Integer.toString(resp.length()));
				
				
				return resp + System.lineSeparator();
			});


		
		
		get("/config", (req, res) -> {
				String resp = "{\"max_results\":\"" + Globals.getMaxResults()
					+ "\",\"max_threads\":\"" + Globals.getMaxThreads()
					+ "\",\"pretty_jay\":\"" + Globals.isPrettyJay()
					+ "\",\"base_url\":\"" + Globals.getBaseUrl().toString()
					+ "\",\"InstanceId\":\"" + Globals.getAppInstanceId() + "\"}";


				if(Globals.isPrettyJay())
					resp = JsonOps.jsonPretty(resp);
				
				res.header("Content-Length", Integer.toString(resp.length()));
				
				
				return resp + System.lineSeparator();
			});


		get("/config/messages", (req, res) -> {
				String resp = "System Messages:\n" + Globals.getMessages();

				res.header("Content-Length", Integer.toString(resp.length()));


				return resp + System.lineSeparator();
			});


		// this is for the health-checks by AWS ELB

		get("/stat", (req, res) -> {
				res.header("Content-Length", Integer.toString("".length()));

				return "";
			});

		post("/stat", (req, res) -> {
				res.header("Content-Length", Integer.toString("".length()));

				return "";
			});
	}


	private static String bodySyntaxCheck(String body) {
		
		String bdy = body;
		String resp = null;
		
		
		if(bdy.length() == 0) {
			resp = "{\"status\":400,\"message\":\"request body is required\"}";
		}

		if(JsonOps.isValidJson(bdy) == false) {
			resp = "{\"status\":400,\"message\":\"request body must be valid json object\"}";
		}


		return resp;
	}

	

	private static String CrawlBodyCheck(String body) {

		String bdy = body;
		String resp = null;
		
		resp = bodySyntaxCheck(bdy);

		if(resp == null) {  // no error, body is compliant
			
			String srchKey = JsonOps.getValueByName(bdy, "keyword");
		
			if(srchKey == null) {  // "keyword" MUST be present..
				resp = "{\"status\":400,\"message\":\"field \"keyword\" is required}";
			}
			else if(srchKey.length() < 4 || srchKey.length() > 32) {  // condition for the size of the keyword
				resp = "{\"status\":400,\"message\":\"field \"keyword\" is required (from 4 up to 32 chars)\"}";
			}
		}


		return resp;
	}



	private static String ConfigBodyCheck(String body) {

		String bdy = body;
		String resp = null;
		String srchKey = null;
		
		resp = bodySyntaxCheck(bdy);

		if(resp == null) {  // no error, body is compliant..

			srchKey = JsonOps.getValueByName(bdy, "target");

			if(srchKey != null) {
				String id = Globals.getAppInstanceId();
				
				if(id.equals(srchKey) == false)
					resp = "{\"status\":400,\"message\":\"trying to address a wrong target\"}";
			}
			else {
				resp = "{\"status\":400,\"message\":\"field \"target\" is required}";
			}
		}


		return resp;
	}


	private static void ConfigChangesExecute(String body) {

		String bdy = body;
		String pjay = null;


		// make changes to json formating setting
		
		pjay = JsonOps.getValueByName(bdy, "pjay");

		if(pjay != null) {
			Globals.setPrettyJay(pjay);
		}

	}
	
	
	private static String StartSearch(String bdy) {

		String body = bdy;
		String resp = null;
		
		String sKey = JsonOps.getValueByName(body, "keyword");
		String sid = RandomString.generateString();
		
		JobContext jctx = new JobContext(sid, sKey);
		boolean r = CrawlJobs.addJob(sid, jctx);
		
		if(r == true) {
			
			// boom! here we go..
			
			ThreadPool.dispatch(jctx);
			
			resp =  "{\"id\":\"" + sid + "\"}";
		}

		return resp;
	}

}
