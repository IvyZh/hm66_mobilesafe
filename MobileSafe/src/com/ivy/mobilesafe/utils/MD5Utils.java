package com.ivy.mobilesafe.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
	
	public static String encode(String text){
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bs = digest.digest(text.getBytes());
			
			StringBuffer sb = new StringBuffer();
			for (byte b : bs) {
				int i  = b & 0xff;
				String hexString = Integer.toHexString(i);
				if(hexString.length()==1){
					hexString = "0"+hexString;
				}
				sb.append(hexString);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}
