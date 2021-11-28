package kr.uracle.ums.agent.graal;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Objects;

import javax.script.Bindings;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;

import kr.uracle.ums.agent.core.GraalScriptEngineFactory;

public class GraalScript {

	private static final Logger logger = LoggerFactory.getLogger(GraalScript.class);

	private ScriptEngine engine;
	private Compilable compilingEngine;
	private CompiledScript compiledScript;
	private Bindings bindings;

	private final JScriptContext context;
	
	private final File script;
	private final File[] includes;

	public GraalScript(JScriptContext context,File script, File ... includes) throws Exception {
		this.context = context;
		this.script = script;
		this.includes = includes;
		loadScript();
	}

	public void loadScript() throws Exception {
		try {
			logger.info("Loading Script [{}]", this.script.getAbsolutePath());
			
			this.engine = GraalScriptEngineFactory.getInstance().createEngine();

			for(int i=0;i<includes.length;i++) {
				logger.info("Including Script [{}]", this.includes[i].getAbsolutePath());
				engine.eval(new FileReader(this.includes[i]));
			}
			
			this.compilingEngine = (Compilable) engine;
			this.compiledScript = compilingEngine.compile(new FileReader(this.script));

			this.bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
			this.bindings.put("logger", logger);
			this.bindings.put("context", context);
			
		} catch (Exception e) {
			throw e;
		}
	}

	public Bindings getBindings() {
		return this.bindings;
	}
	
	public void putBinding(String key, Object value) {
		this.bindings.put(key, value);
	}

	public ScriptEngine getEngine() {
		return engine;
	}

	public Compilable getCompilingEngine() {
		return compilingEngine;
	}

	public CompiledScript getCompiledScript() {
		return compiledScript;
	}

	public JScriptContext getContext() {
		return context;
	}

	public void executeScript() throws Exception {	
		compiledScript.eval(bindings);
	}
}
