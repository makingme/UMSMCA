package kr.uracle.ums.agent.core;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.uracle.ums.agent.exception.NONConfigVariableSetException;
import kr.uracle.ums.agent.graal.GraalScript;
import kr.uracle.ums.agent.graal.JScriptContext;
import kr.uracle.ums.agent.graal.ScriptManager;
import kr.uracle.ums.agent.util.CommonXMLConfig;

public abstract class TaskWorker extends Thread{
	
	/*
	 *	필수 환경 변수
	 *	1. SCRIPT_HOME	:	JS 파일 홈 디렉토리
	 *  
	 *  설정 파일
	 *  1. {TASKNAME}-enableInclude	:	includes 파일 포함 여부
	 *  
	 *  INIT.js 파일
	 *  1. isSqlTask	:	SQLTASK 여부
	 *  2. FETCHCNT		:	SQL SELECT 범위
	 *  2. info			: 	사용 SQL 정보 및 수행 정보
	 *
	 *  
	*/	
	private static final Logger logger = LoggerFactory.getLogger(TaskWorker.class);
	
	private final static int NODATA=0;
	private final static int WAIT=-1;
	private final static int SQL_ERROR=-10;
	private final static int SCRIPT_ERROR=-20;
	
	
	private long maxErrorLoopTime=1*20*1000;
	private long waitOnNoData=3000;
	private long totalCount=0;
	private long sectionCount=0;
	public long standTime=0;
	public long sectionTime=0;
	
	public boolean isWorking=false;
	
	protected SQLListInfo sqlInfo=new SQLListInfo();
	protected String taskName="NONAME";
	protected String isSqlTask="Y";
	
	protected ScriptManager scriptManager=new ScriptManager();;
	protected CommonXMLConfig config=null;
	protected JScriptContext jsContext=null;
	
	protected GraalScript initGraal=null;
	protected GraalScript handleGraal=null;
		
	protected Map<String, File> sqlFileMap = new HashMap<String, File>();
	protected Map<String, String> confMap;
	protected Map<String, Object> infoMap;
	
	public TaskWorker(String taskName, CommonXMLConfig config) throws Exception {
		this.taskName=taskName;
		this.config=config;
	}
	
	public TaskWorker(String taskName) throws Exception {
		this.taskName=taskName;
	}

	public void init() {
		this.setName(taskName);
		logger.info("#################################################");
		logger.info("##"+taskName+": Initialize Start~~.##");
		
		//설정 Loading....
		if(config ==null) {
			try {
				confMap= CommonXMLConfig.load();
			} catch (ConfigurationException e1) {
				e1.printStackTrace();
			}			
		}
		
		//ScriptHome 환경변수 가져오기
		String scriptHome=System.getenv("SCRIPT_HOME");
		if(scriptHome == null || scriptHome.trim().equals("")) {
			throw new NONConfigVariableSetException("Not Export Environment Variable \"SCRIPT_HOME\"");
		}
		
		String loadFilePath=scriptHome+File.separator+taskName+File.separator;
		String includeFilePath=scriptHome+File.separator+"includes"+File.separator;
		
		File[] includes=null;
		// include 사용 여부 확인 후 맵 초기화
		if(confMap.get(taskName+".enableInclude").equalsIgnoreCase("Y")) {
			try {
				includes=getFileList(includeFilePath,"js");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(includes==null) includes=new File[0];
		
		String waitTimeSec=confMap.get(taskName+".waitOnNoData").replaceAll("\\D", "");
		if(!waitTimeSec.equals("")) {
			waitOnNoData=Long.parseLong(waitTimeSec)*1000;
		}
		
		// 스크립트 매니저 초기화 init.js, handle.js 스크립트 엔진 인스턴스 생성 및 초기화
		if (!scriptManager.loadScript(new File(loadFilePath + "init.js"), includes)) {
			throw new NONConfigVariableSetException("Cannot load init.js. Plz, check out init.js file in ["+loadFilePath+"].");
		}else {
			initGraal=scriptManager.getScript("init.js");			
		}
		if (!scriptManager.loadScript(new File(loadFilePath + "handle.js"), includes)) {
			throw new NONConfigVariableSetException("Cannot load handle.js. Plz, check out handle.js file in ["+loadFilePath+"].");
		}else {
			handleGraal=scriptManager.getScript("handle.js");	
		}
		// 스크립트간 공유 Context Get
		jsContext = scriptManager.getJSContext();
		jsContext.session("TASKNAME", taskName);
		
		// Task init.js 실행
		try {
			logger.info(taskName+": Execute Script [{}]", "init.js");
			initGraal.executeScript();
		} catch (Exception e) {
			logger.error("Exception occured while executing init.js", e);
		}
		
		// init.js 수행시 스크립트단 전달 데이터 Map
		infoMap=jsContext.getInfoMap();
		
		// SQL TASK 여부 확인 Default N
		isSqlTask=(String)infoMap.get("isSqlTask");
		if(isSqlTask==null)isSqlTask="N";
		// SQL TASK 일시, SQL 파일 맵 초기화
		if(isSqlTask.trim().equalsIgnoreCase("Y")) {
			try {
				initMap(sqlFileMap , loadFilePath, "sql");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(sqlFileMap.size()<=0) {
				throw new NONConfigVariableSetException("There is no sql in "+loadFilePath);	
			}
		}
		
		extraInit();
	}
	
	abstract public void extraInit();
	abstract public int hadleWork();	
	
	@Override
	public void run() {
		init();
		isWorking=true;
		long errorSleep=1000;
		while(isWorking) {
			markTime();
			int rslt=hadleWork();
			long requiredTime=markTime();
			markTime();
			if(rslt>TaskWorker.NODATA) {
				sectionCount+=rslt;
				totalCount+=rslt;
				printTPS(requiredTime, rslt);
			}else if(rslt==TaskWorker.NODATA) {
				logger.info("DATA 없음으로 "+waitOnNoData/1000+"초 동안 대기");
				long wait = 0 ;
				synchronized(this)
				{
					while ( wait < waitOnNoData ){
						try {
							this.wait(waitOnNoData < 1000 ? waitOnNoData : 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
							break;
						}
						wait += 1000 ;
					}
				}				
			}else if(rslt==-1) {
				long wait = 0 ;
				synchronized(this)
				{
					if(maxErrorLoopTime>errorSleep) {
						logger.error("수행 중 에러로 인한 "+errorSleep/1000+"초 동안 IDLE 상태로 전환");
						while ( wait < errorSleep ){
							try {
								this.wait(errorSleep);
							} catch (InterruptedException e) {
								e.printStackTrace();
								break;
							}
							wait += 1000 ;
						}
						errorSleep+=errorSleep;						
					}else {
						logger.error("에러 로그 출력 제한 시간("+maxErrorLoopTime/1000+"초) 초과로 인한 프로세스 중지");
						isWorking=false;
					}
				}
			}
			requiredTime=markTime();
			sectionTime+=requiredTime;
		}//loop 종료 
		
	}
	
	public File[] getFileList(String path, String fileHint) throws Exception{
		File[] files= new File(path).listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File f) {
				if(f.isFile() && f.getName().endsWith(fileHint)) {
					return true;
				}
				return false;
			}
		});
		
		if(files==null||files.length == 0) {
			throw new Exception(path + ", There is no "+fileHint+" files..");
		}
		return files;
	}
	
	
	public void initMap(Map<String, File> targetMap, String path, String fileHint) throws Exception{
		File[] files= getFileList(path, fileHint);
		
		if(files.length == 0) {
			throw new Exception(path + ", There is no "+fileHint+" files..");
		}
		
		for(File f: files) {
			String fname = f.getName();
			targetMap.put(fname, f);
		}
	}
	
	long markTime() {
		long t = System.currentTimeMillis() ;
		long itv = t-standTime ;
		standTime = t ;
		return itv ;
	}
	
	public void printTPS(long time, long count) {
		if(time==0)time=1;
		sectionTime+=time;
		sectionCount+=count;
		logger.info("1 Cycle TPS, Lead Time:"+(time/1000)+"Sec, Proccessed Count:"+count+", TPS:"+(count/(time/1000)));
		if(sectionTime>30000) {
			logger.info("Section TPS(During 30s), Lead Time:"+(sectionTime/1000)+"Sec, Proccessed Count:"+sectionCount+", TPS:"+(sectionCount/(sectionTime/1000)));
			sectionTime=0;
			sectionCount=0;
		}
	}

	public class SQLListInfo{
		int selectSqlCount=0;
		int updateSqlCount=0;
		int insertSqlCount=0;
		int deleteSqlCount=0;
		int errorSqlCount=0;
		
		public Map<String, String> sqlInfoMap;
		
		public int upSelectSqlCount() {
			return ++selectSqlCount;
		}
		public int downSelectSqlCount() {
			return --selectSqlCount;
		}
		
		public int upUpdateSqlCount() {
			return ++updateSqlCount;
		}
		public int downUpdateSqlCount() {
			return --updateSqlCount;
		}
		
		public int upInsertSqlCount() {
			return ++insertSqlCount;
		}
		public int downInsertSqlCount() {
			return --insertSqlCount;
		}
		
		public int upDeleteSqlCount() {
			return ++deleteSqlCount;
		}
		public int downDeleteSqlCount() {
			return --deleteSqlCount;
		}
		
		public int upErrorSqlCount() {
			return ++errorSqlCount;
		}
		public int downErrorSqlCount() {
			return --errorSqlCount;
		}
		
		public void putSqlInfo(String kye, String value) {
			sqlInfoMap.put(kye, value);
		}
		
		public String getSqlInfo(String key) {
			return sqlInfoMap.get(key);
		}
		
		public void setSqlInfoMap(Map<String, String> map) {
			sqlInfoMap=map;
		}
		public Map<String,String> getSqlInfoMap(){
			return sqlInfoMap;
		}
	}

}
