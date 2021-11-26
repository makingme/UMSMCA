package kr.uracle.ums.agent.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class FileLoader {
	
	static String fileHome="";
	
	static {
		fileHome=System.getenv("SCRIPT_HOME");
	}
	
	public FileLoader() {
		fileHome=System.getenv("SCRIPT_HOME");
	}
	
	public FileLoader(String home) {
		fileHome=home;
	}
	
	public static String loadTextToString(String fname, String targetPath) throws FileNotFoundException   {
		String path=fileHome+File.separator+targetPath+File.separator+fname;
		File f=new File(path);
		if(!f.exists()) throw new FileNotFoundException("["+path+"] 파일을 찾을수 없음.");
		BufferedReader br=new BufferedReader(new FileReader(f));
		String val=null;
		StringBuilder sb=new StringBuilder();
		try {
			while((val=br.readLine()) !=null) {
				sb.append(val);
				val=null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(br!=null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return sb.toString();
	}
	
	public static Map<String,String> loadJsonToMap(String fname, String targetPath) throws FileNotFoundException   {
		String jsonString=loadTextToString(fname,targetPath);
		Gson gson=new Gson();
		Type mapType = new TypeToken<Map<String, String>>(){private static final long serialVersionUID = 1L;}.getType();
		Map<String,String> retrunMap=gson.fromJson(jsonString, mapType);
		return retrunMap;
	}
}
