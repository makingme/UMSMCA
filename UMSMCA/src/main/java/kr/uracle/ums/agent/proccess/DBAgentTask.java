package kr.uracle.ums.agent.proccess;

import java.sql.PreparedStatement;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.uracle.ums.agent.core.TaskWorker;
import kr.uracle.ums.agent.exception.NONConfigVariableSetException;
import kr.uracle.ums.agent.util.CommonXMLConfig;

public class DBAgentTask extends TaskWorker{
	
	
	/*
	 *	필수 환경 변수
	 *	1. SCRIPT_HOME	:	JS 파일 홈 디렉토리
	 *  
	 *  설정 파일
	 *  1. {TASKNAME}-enableInclude	:	includes 파일 포함 여부
	 *  
	 *  INIT.js 파일
	 *  1. isSqlTask	:	SQLTASK 여부
	 *  2. sqlInfo		: 	사용 SQL 정보 및 수행 정보
	 *  
	*/	
	Map<String, String> exeOrder=new LinkedHashMap<String, String>();
	PreparedStatement ps;
	
	@Override
	public void extraInit(){
		super.init();
		
		Object sqlObj=infoMap.get("info");
		if(sqlObj==null || !(sqlObj instanceof Map)) {
			throw new NONConfigVariableSetException("Set sqlInfo in init.js >> context.init(\"info\", {});");				
		}
		sqlInfo.setSqlInfoMap((Map<String, String>)sqlObj);
		
		//SQLTASK 설정 재확인
		if(isSqlTask.trim().equalsIgnoreCase("N")) {
			throw new NONConfigVariableSetException("Set \"isSqlTask\" in init.js >> context.init(\"isSqlTask\", \"Y\")");	
		}
		
		//init.js SQL 설정 정보 확인 
		Set<Map.Entry<String,String>>sqlInfoSet = sqlInfo.sqlInfoMap.entrySet();
		for(Map.Entry<String, String> sqlData :sqlInfoSet) {
			String key=sqlData.getKey();
			String val=sqlData.getValue();
			
			if(key.endsWith(".sql")) {
				if(!sqlFileMap.containsKey(key)) {
					throw new NONConfigVariableSetException("There is no sql in "+taskName+" Directory");	
				}
				switch(val.trim().toUpperCase()) {
				case "SELECT": sqlInfo.upSelectSqlCount();
				break;
				case "UPDATE": sqlInfo.upUpdateSqlCount();
				break;
				case "INSERT": sqlInfo.upInsertSqlCount();
				break;
				case "DELETE": sqlInfo.upDeleteSqlCount();
				break;
				default: sqlInfo.upErrorSqlCount();
				break;
				}
			}
		}
	}


	private static final Logger logger = LoggerFactory.getLogger(DBAgentTask.class);
	
	public DBAgentTask(String taskName, CommonXMLConfig config) throws Exception {
		super(taskName, config);
	}
	
	
	@Override
	public int hadleWork() {
		
		return 0;
	}
	
	

}
