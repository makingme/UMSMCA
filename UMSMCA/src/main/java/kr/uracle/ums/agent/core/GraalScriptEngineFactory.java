package kr.uracle.ums.agent.core;

import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Objects;

import javax.script.ScriptEngine;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;

public class GraalScriptEngineFactory {
	
	public enum InatanceType{
		DEFAULT, BUILDERCUSTOM, MULTICUSTOM
	}
	
	static InatanceType instanceType;
	Context.Builder cBuilder=null;
	
	public GraalScriptEngineFactory() {
		this.cBuilder=getDefaultBuilder();
		instanceType=InatanceType.DEFAULT;
	}
	

	public GraalScriptEngineFactory(Context.Builder cBuilder) {
		this.cBuilder=cBuilder;
		instanceType=InatanceType.BUILDERCUSTOM;
	}
	
	
	public static GraalScriptEngineFactory scriptEngineFactory = null;
	
	
	public static GraalScriptEngineFactory getInstance() {
		if(scriptEngineFactory == null|| !(instanceType==InatanceType.DEFAULT)) {
			scriptEngineFactory = new GraalScriptEngineFactory();
		}
		return scriptEngineFactory;
	}
	
	
	public static GraalScriptEngineFactory getInstance(Context.Builder cBuilder) {
		if(scriptEngineFactory == null|| !(instanceType==InatanceType.BUILDERCUSTOM)) {
			scriptEngineFactory = new GraalScriptEngineFactory(cBuilder);
		}
		return scriptEngineFactory;
	}
	
	public ScriptEngine createEngine(){
		ScriptEngine eng=GraalJSScriptEngine.create(null, cBuilder);
		
		return eng;
		
	}
	
	public ScriptEngine createEngine(File[] files) throws Exception{
		ScriptEngine engine=GraalJSScriptEngine.create(null, cBuilder);
		for(File f : files) {
			engine.eval(new FileReader(f));
		}
		return engine;
		
	}
	
	public HostAccess getDefaultHostAccess() {
		return HostAccess.newBuilder(HostAccess.ALL).targetTypeMapping(
				List.class,
				Object.class,
				Objects::nonNull,
				v -> v,
				HostAccess.TargetMappingPrecedence.HIGHEST).build();
	}
	
	public Context.Builder getDefaultBuilder(){
		return Context.newBuilder("js")
				.allowHostAccess(getDefaultHostAccess())
				.allowHostClassLookup(s -> true);
	}
}


