package kr.uracle.ums.agent.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public class CommonXMLConfig {
	public static Map<String, String> confMap=new HashMap<String, String>();
	public static String configPath="./conf/config.xml";
	
	public static Map<String, String> load() throws ConfigurationException {
		String path =System.getenv("MCA_CONFING");
		if(path != null) {
			configPath=path;
		}
		XMLConfiguration.setDefaultListDelimiter((char) (0));
		XMLConfiguration xmlConfig = new XMLConfiguration(configPath);
		Iterator<String> it=xmlConfig.getKeys();
		while(it.hasNext()) {
			String key=it.next();
			confMap.put(key, xmlConfig.getString(key));
		}
		

		return confMap;
	}
}
