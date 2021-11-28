package kr.uracle.ums.agent.proccess;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.uracle.ums.agent.core.DBCPDataSourceFactory;
import kr.uracle.ums.agent.core.TaskWorker;
import kr.uracle.ums.agent.exception.NONConfigVariableSetException;
import kr.uracle.ums.agent.util.CommonXMLConfig;
import kr.uracle.ums.agent.util.SQLConverter;

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
 *  
 */	
/*
 * 고정 수행 플로우 
 * 채널 결과테이블 SELECT: selectResult
 * SELECT 결과 MAP 객체화 후 hadle.js에 바인딩
 * handle.js 파일 실행
 * 성공 시
 * 	결과테이블 결과처리: updateDetail 수행
 * 실패 시
 * 	전환발송 O
 * 		채널테이블 전환 발송 요청 데이터 INSERT: insertResend
 * 		결과테이블 전환 발송 로그 데이터 INSERT: insertResendDetail
 * 		결과테이블 결과처리: updateDetail 수행
 * 	전환발송 X
 * 		결과테이블 결과처리: updateDetail 수행
 * 	공통
 * 		원장테이블 UPDATE: updateLedger
 * 		통계테이블 UPDATE: updateStatistics
 * 전체
 * 	채널 결과 테이블 DELETE: 		
 * 
 */
public class ResultWorker extends TaskWorker{
	
	private static final Logger logger = LoggerFactory.getLogger(ResultWorker.class);
	
	final static int SUCCESS=1;
	final static int FAILNORESEND=2;
	final static int FAILRESEND=3;
	
	int fetchCount=50;
	
	boolean isExtraInsertResend=false;
	boolean isExtraUpdateResult=false;
	
	private Map<String, Integer> SQLS=new HashMap<String, Integer>();{
		SQLS.put("selectResult", 10);
		SQLS.put("insertResend", 20);
		SQLS.put("insertResendDetail", 30);
		SQLS.put("updateDetail", 40);
		SQLS.put("updateLedger", 50);
		SQLS.put("upsertStatistics", 60);
		SQLS.put("deleteResult", 70);
	}

	private SQLConverter selectResultSQL;
	private SQLConverter insertResendSQL;
	private SQLConverter insertResendDetailSQL;
	private SQLConverter updateDetailSQL;
	private SQLConverter updateLedgerSQL;
	private	SQLConverter upsertStatisticsSQL;
	private SQLConverter deleteResultSQL;
	private SQLConverter extraInsertResendSQL;
	private SQLConverter extraUpdateResultSQL;
	
	@Override
	public void extraInit() {

		//SQLTASK 설정 재확인
		if(isSqlTask.trim().equalsIgnoreCase("N")) {
			throw new NONConfigVariableSetException("Set \"isSqlTask\" in init.js >> context.init(\"isSqlTask\", \"Y\")");	
		}
		//추가 INSERT 사용 여부 확인
		if(infoMap.get("extraInsertResend").toString().equals("Y")) {
			isExtraInsertResend=true;
			SQLS.put("extraInsertResend", 31);
		}
		//추가 UPDATE 사용 여부 확인
		if(infoMap.get("extraUpdateResult").toString().equals("Y")) {
			isExtraUpdateResult=true;
			SQLS.put("extraUpdateResult", 41);
		}
		
		// 필수 SQL 확인 및 로딩, TaskWorker의 SqlFileMap과 SQLListInfo 필요성 재검토
		Set<Map.Entry<String, Integer>> sqlSet=SQLS.entrySet();

		for(Map.Entry<String, Integer> sql: sqlSet) {
			String dumpSQL=null;
			String key=sql.getKey();
			if(!infoMap.containsKey(key)) {
				throw new NONConfigVariableSetException("\nCan't Find  Matched Value in SQL DATA, Check the ["+key+"] keyword in init.js FILE");
			}
			String value=infoMap.get(key).toString();
			if(value.trim().equals(""))	throw new NONConfigVariableSetException("\nCan't Find  Matched Value in SQL DATA, Check the ["+key+"] Data Empty in init.js FILE");
			SQLConverter tempSQL=new SQLConverter(value);
			//System.out.println(tempSQL.getExecuteSQL());
			switch(key.toUpperCase()) {
			case "SELECTRESULT":	selectResultSQL = tempSQL;
				break;
			case "INSERTRESEND": insertResendSQL= tempSQL;
				break;
			case "INSERTRESENDDETAIL" : insertResendDetailSQL = tempSQL;
				break;
			case "UPDATEDETAIL" : updateDetailSQL = tempSQL;
				break;
			case "UPDATELEDGER" : updateLedgerSQL = tempSQL;			
				break;
			case "UPSERTSTATISTICS" : upsertStatisticsSQL = tempSQL;
				break;
			case "DELETERESULT" : deleteResultSQL = tempSQL;
				break;
			case "EXTRAINSERTRESEND": extraInsertResendSQL = tempSQL;
				break;
			case "EXTRAUPDATERESULT": extraUpdateResultSQL = tempSQL;
				break;
			default : dumpSQL =value;
				break;
			}
			if(dumpSQL == null) {
				logger.info(key+" SQL, Convert Complete!!");
				logger.info("["+tempSQL.getExecuteSQL()+"]");
			}else {
				logger.info("Throw away :"+dumpSQL);
			}
			
		}
		//init.js 수행 후 전달 PARAMS중 FETCHCNT 설정 확인
		Object fObj=infoMap.get("FETCHCNT");
		String fcn=fObj==null?"":fObj.toString();
		if(!fcn.equals("")) {
			fetchCount=Integer.parseInt(fcn);
		}
		logger.info("#################################################");
	}
	
	public ResultWorker(String taskName, CommonXMLConfig config) throws Exception {
		super(taskName, config);
	}
	
	public ResultWorker(String taskName) throws Exception {
		super(taskName);
	}
	
	@Override
	public int hadleWork() {
		int rslt=-1;
		Connection conn = null;
		PreparedStatement select = null;
		PreparedStatement succUpdateDetail = null;
		PreparedStatement failUpdateDetailNoResend = null;
		PreparedStatement failUpdateDetailResend = null;
		PreparedStatement insertResend = null;
		PreparedStatement insertReDetail = null;
		PreparedStatement updateLedger = null;
		PreparedStatement upsertStatistics = null;
		PreparedStatement commonDeleteResult = null;
		
		try {
			int succCnt=0;
			int failNoResendCnt=0;
			int failResendCnt=0;
			//Connection 인스턴스 가져오기- AutoCommit=False Default
			conn=DBCPDataSourceFactory.getConnection();
			select=conn.prepareStatement(selectResultSQL.getExecuteSQL());
			
			//Select SQL 조립 및 수행
			selectResultSQL.setSQLVar(select, Collections.singletonMap("FETCHCNT", String.valueOf(fetchCount)));
			ResultSet selectRS=select.executeQuery();

			ResultSetMetaData rsmd = selectRS.getMetaData();
			int colCount=rsmd.getColumnCount();
			int rowcount = 0;
			StringBuilder sb=null;
			Map<String, String> rsMap=null;
			while(selectRS.next()) {
				//스크립트 수행결과 변수
				rslt=0;
				//예외 SQL 출력 변수
				sb=new StringBuilder();
				//ResultSet Map 컨버트 변수
				rsMap=new HashMap<String, String>();
				// 스크립트 조립 맵핑 테이블
				for(int i=1; i<=colCount;i++) {
					String key=rsmd.getColumnName(i);
					String value=selectRS.getString(i);
					rsMap.put(key, value);
					sb.append(key+":"+value+", ");
				}
				logger.debug(sb.substring(0, sb.length()-2));
				
				//hadle.js 스크립트 수행
				handleGraal.putBinding("RSMAP", rsMap);
				try {
					jsContext.stack("TASKNAME", taskName);
					handleGraal.executeScript();
				} catch (Exception e) {
					logger.error("handl.js 실행 중 에러 발생.....");
					e.printStackTrace();
				}
				//hadle.js 수행 후 전달 PARAMS중 rslt 값 확인
				Object rsObj=jsContext.stack("rslt");
				if(rsObj==null)throw new NONConfigVariableSetException("\nCan't Find  Matched Value in stack,Put rslt in handle.js >> context.stack(\"rslt\",interger value) ");
								
				//rslt 1이면 성공, 2이면 전환발송X 실패, 3이면 전환 발송 실패, 4이면 데이터 이상
				//3이면 선처리 전환발송 INSERT 수행
				rslt=Integer.parseInt(rsObj.toString());
				
				//preparedstatement 초기화
				succUpdateDetail = conn.prepareStatement(updateDetailSQL.getExecuteSQL());
				failUpdateDetailNoResend = conn.prepareStatement(updateDetailSQL.getExecuteSQL());
				failUpdateDetailResend = conn.prepareStatement(updateDetailSQL.getExecuteSQL());
				insertResend = conn.prepareStatement(insertResendSQL.getExecuteSQL());
				insertReDetail = conn.prepareStatement(insertResendDetailSQL.getExecuteSQL());
				updateLedger = conn.prepareStatement(updateLedgerSQL.getExecuteSQL());
				upsertStatistics = conn.prepareStatement(upsertStatisticsSQL.getExecuteSQL());
				commonDeleteResult= conn.prepareStatement(deleteResultSQL.getExecuteSQL());
				
				if(rslt==1) {
					updateDetailSQL.setSQLVar(succUpdateDetail, rsMap);
					succUpdateDetail.addBatch();
					succCnt++;
				}else if(rslt==2){
					updateDetailSQL.setSQLVar(failUpdateDetailNoResend, rsMap);
					failUpdateDetailNoResend.addBatch();
					failNoResendCnt++;
				}else if(rslt==3) {
					updateDetailSQL.setSQLVar(failUpdateDetailResend, rsMap);
					failUpdateDetailResend.addBatch();
					insertResendSQL.setSQLVar(insertResend, rsMap);
					insertResend.addBatch();
					insertResendDetailSQL.setSQLVar(insertReDetail, rsMap);
					insertReDetail.addBatch();
					failResendCnt++;
				}else {
					rsMap.clear();
					logger.warn("Check the hadle.js's rslt setting value...rslt is not supported value ["+rslt+"]");
					continue;
				}
				//실패 시 원장 통계 처리  
				if(rslt!=1) {				
					//원장 통계 ADD
					updateLedgerSQL.setSQLVar(updateLedger, rsMap);
					updateLedger.addBatch();
				}
				//전체 통계 ADD
				upsertStatisticsSQL.setSQLVar(upsertStatistics, rsMap);
				upsertStatistics.addBatch();
				//삭제 ADD
				deleteResultSQL.setSQLVar(commonDeleteResult, rsMap);
				commonDeleteResult.addBatch();
				rowcount++;
				rsMap.clear();
				jsContext.clearStackMap();
			}//while문 종료 지점
//			if(rowcount !=0 && rowcount !=fetchCount) {
//				logger.error("SELECT ROW 갯수와 처리 갯수 불일치, SELECT COUNT:"+fetchCount+", 처리 COUNT:"+rowcount);
//			}
			
			if(succCnt >0) {
				succUpdateDetail.executeBatch();
			}
			if(failNoResendCnt>0) {
				failUpdateDetailNoResend.executeBatch();
			}
			if(failResendCnt>0) {
				failUpdateDetailResend.addBatch();
				insertResend.addBatch();
				insertReDetail.addBatch();
			}
			if(failNoResendCnt+failResendCnt>0)updateLedger.executeBatch();
			
			if(rowcount>0) {
				upsertStatistics.executeBatch();
				commonDeleteResult.executeBatch();
				conn.commit();				
			}
			rslt=rowcount;
			conn.close();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {			
			jsContext.clearStackMap();
			try {
				if(conn!=null)conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}		
		return rslt;
	}
	
	public static void main(String[] args) throws Exception {
		TaskWorker tw=new ResultWorker("ALTResultHandler");
		tw.start();
	}
}
