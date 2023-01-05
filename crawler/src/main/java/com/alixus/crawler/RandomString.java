/*
 * Copyright (c) 2022 Al Ixus
 */

package com.alixus.crawler;


import java.util.*;


public class RandomString {

	public static String generateString() {
		return generateString(8);  // default strings are 8 in length..
	}


	public static String generateString(int length) {
		
		int len = length;
                char[] res = new char[len];

		for (int i = 0; i < len; i++) {
			res[i] = '\0';
		}

		
                Random rand = new Random();

                String text = "abcdefghijklmnopqrstuvwxyz" +
                              "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                              "1234567890";
		
                int tlen = text.length();
		
                for (int i = 0; i < len; i++) {
                        int nchar = Math.abs((rand.nextInt() % tlen));
                        res[i] = text.charAt(nchar);
                }

		
		return new String(res);
	}

	
	public static String stringToNumerical(String str) {

		int slen = str.length();
                char[] res = new char[slen];
		
		for (int i = 0; i < slen; i++) {
			res[i] = '\0';
		}


		String text = "1234567890" +
			      "2345678901" +
			      "5432167890" +
			      "0987512436";

		int tlen = text.length();

				
		for (int i = 0; i < slen; i++) {
			int c = str.charAt(i);
			int idx = (c % tlen);
			res[i] = text.charAt(idx);
		}

		
		return new String(res);
	}
	
}
