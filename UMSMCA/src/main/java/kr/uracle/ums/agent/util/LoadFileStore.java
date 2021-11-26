package kr.uracle.ums.agent.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class LoadFileStore {
	private Map<String, File> jsFileMap = new HashMap<String, File>(10);
	private Map<String, File> sqlFileMap = new HashMap<String, File>(10);
	private Map<String, File> extraFileMap = new HashMap<String, File>(10);
	
	
	public LoadFileStore() {
		jsFileMap = new HashMap<String, File>(10);
		sqlFileMap = new HashMap<String, File>(10);
		extraFileMap = new HashMap<String, File>(10);
	}
	
	public LoadFileStore(int mapSize) {
		jsFileMap = new HashMap<String, File>(mapSize);
		sqlFileMap = new HashMap<String, File>(mapSize);
		extraFileMap = new HashMap<String, File>(mapSize);
	}
	
	public int putJS(String key, File value) {
		jsFileMap.put(key, value);
		return jsFileMap.size();
	}
	
	public File getJS(String key) {
		return jsFileMap.get(key);
	}
	
	public Map<String, File> getJSMap() {
		return jsFileMap;
	}
	
	public int putSQL(String key, File value) {
		sqlFileMap.put(key, value);
		return sqlFileMap.size();
	}
	
	public File getSQL(String key) {
		return sqlFileMap.get(key);
	}
	
	public Map<String, File> getSQLMap() {
		return sqlFileMap;
	}
	
	public int putExtra(String key, File value) {
		extraFileMap.put(key, value);
		return extraFileMap.size();
	}
	
	public File getExtra(String key) {
		return extraFileMap.get(key);
	}
	
	public Map<String, File> getExtraMap() {
		return extraFileMap;
	}
}
