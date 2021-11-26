package kr.uracle.ums.agent.graal;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JScriptContext implements Closeable {

    private final Map<String, Object> infoMap = new HashMap<String, Object>();
    private final Map<String, Object> sessionMap = new HashMap<String, Object>();
    private final Map<String, Object> stackMap = new HashMap<String, Object>();
    
    private final Map<String, Map<String,String>> codeMap = new HashMap<String, Map<String,String>>();
    
    public void code(String code, Map<String,String> msg) {
    	this.codeMap.put(code, msg);
    }
    
    public Map<String,String> code(String code) {
    	return this.codeMap.get(code);
    }
    
    public Map<String, Map<String,String>> getCodeMap() {
        return this.codeMap;
    }
    
    public void info(String key, Object value) {
        this.infoMap.put(key, value);
    }

    public Object info(String key) {
        return this.infoMap.get(key);
    }

    public Map<String, Object> getInfoMap() {
        return this.infoMap;
    }

    public void session(String key, Object value) {
        this.sessionMap.put(key, value);
    }

    public Object session(String key) {
        return this.sessionMap.get(key);
    }

    public Map<String, Object> getSessionMap() {
        return this.sessionMap;
    }

    public void stack(String key, Object value) {
        this.stackMap.put(key, value);
    }

    public Object stack(String key) {
        return this.stackMap.get(key);
    }

    public Map<String, Object> getStackMap() {
        return this.stackMap;
    }

    public void clearStackMap() {
        this.stackMap.clear();
    }

    @Override
    public void close() throws IOException {
        //this.clearStackMap();
    }
}
