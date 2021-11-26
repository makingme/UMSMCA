package kr.uracle.ums.agent.config;

import java.util.Properties;

public class UProperties extends Properties{
	
	public boolean getProperty(String key, boolean defaultValue) {
		String val =super.getProperty(key);
		boolean returnVal=defaultValue;
		if(val!=null) {
			returnVal=val.trim().toUpperCase().equals("TRUE");
		}
		return returnVal;
	}
	
	public int getProperty(String key, int defaultValue) {
		String val =super.getProperty(key);
		int returnVal=defaultValue;
		if(val!=null) {
			val=val.replaceAll("\\D", "");
			returnVal=Integer.parseInt(val.equals("")?"0":val);
		}
		return returnVal;
	}
	
	public long getProperty(String key, long defaultValue) {
		String val =super.getProperty(key);
		long returnVal=defaultValue;
		if(val!=null) {
			val=val.replaceAll("\\D", "");
			returnVal=Long.parseLong(val.equals("")?"0":val);
		}
		return returnVal;
	}
	
	
}
