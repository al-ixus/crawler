/*
 * Copyright (C) 2022 Al Ixus
 */

package com.alixus.crawler;


import java.util.*;


/*
 * This class stores all the paths of a BASE_URL in a string format.
 * Instances of trawlers (JobContext) store only integer indexes into this map.
 * This looks like a bottleneck and should/could be reviewed.
 */

public class WebPageProfile {

	private static int counter = 1;

	private static final Object indexLock = new Object();
	private static final Map<Integer,String> WebPageIndex = new HashMap<Integer,String>();
	
	
	public WebPageProfile() {
	}


	public static int addPath(String path) {

		int id = 0;

		
		synchronized (indexLock) {

			int success = 0;
			
			// Do not create a new index for a path that is already added
			// just return index to that path..
			
			if(WebPageIndex.containsValue(path) == true) {
				for(Map.Entry<Integer,String> e: WebPageIndex.entrySet()) {
					if(path.equals(e.getValue()))
						return e.getKey();
				}
			}
			
			
			do {			
				id = counter;
				String res = WebPageIndex.put(id, path);
				
				if(res == null) {
					success = 1;
					counter++;
				}
				else {  // there was a value at this index.
					// add it back. and iterate/try with
					// a new index..
					
					WebPageIndex.put(id, res);
					counter++;
				}			
			} while (success == 0);
		}
		
		
		return id;  // index of a path
	}


	public static String getPath(int idx) {
		String ret = null;

		synchronized (indexLock) {
			ret = WebPageIndex.get(idx);
		}

		if(ret == null)
			ret = new String("");
		
		return ret;
	}
}	
	
