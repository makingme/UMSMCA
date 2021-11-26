package kr.uracle.ums.agent.graal;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ScriptManager {

	private static final Logger logger = LoggerFactory.getLogger(ScriptManager.class);

	private final Map<String, GraalScript> scriptMap = new HashMap<>();
	private final JScriptContext context = new JScriptContext();

	public boolean loadScript(File script, File ... includes) {
		try {
			scriptMap.put(script.getName(), new GraalScript(context, script, includes));
			
			return true;
		} catch (Exception e) {
			logger.error("", e);
		}
		return false;
	}

	public GraalScript getScript(String script) {
		return scriptMap.get(script);
	}

	public void executeScript(String script) throws Exception {
		this.getScript(script).executeScript();
	}
	
	public JScriptContext getJSContext() {
		return context;
	}


}
