/*
 * Copyright (C) Al Ixus
 */

package com.alixus.crawler;


import com.google.gson.*;


public class JsonOps {
	
	public static boolean isValidJson(String json) {

		boolean ret = true;
		
		try {
			JsonElement parser = JsonParser.parseString(json);
		}
		catch (JsonSyntaxException e) {
			return false;
		}

		
		return ret;	
	}
	

	public static JsonObject getJObject(String json) {

		JsonElement ele = null;
		JsonObject ret = null;

		try {
			ele = JsonParser.parseString(json);
			ret = ele.getAsJsonObject();
		}
		catch (JsonSyntaxException e) {}


		return ret;
	}
	
	
	public static String getValueByName(String json, String name) {

		String ret = null;

		
		JsonObject jObj = getJObject(json);

		if(jObj != null)
			try {
				JsonElement r = jObj.get(name);

				if(r != null)
					ret = r.getAsString();
			}
			catch (JsonSyntaxException e) {}
		
		return ret;
	}


	public static String jsonPretty(String str) {

		String ret = "";

		JsonObject json = JsonParser.parseString(str).getAsJsonObject();

		Gson gson = new GsonBuilder().setPrettyPrinting().create();

		ret = gson.toJson(json);

		return ret;
	}

}
