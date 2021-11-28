/*
 * SELECT 한 1개 ROW결과가 RSMAP에 담겨 넘어옴 RSMAP.get("컬럼명")
 * init.js에서 로딩한 Json 알림톡 결과코드 맵 사용가녕 context.code("저장키명")
 * 1. 발송 성공 시
 * 	- context.stack에 1로 저장 : context.stack("rslt",1); 
 * 2. 일반 발송 실패 시
 * 	- context.stack에 2로 저장 : context.stack("rslt",2); 
 * 3. 전환 발송 실패 시
 * 	- context.stack에 3로 저장 : context.stack("rslt",3);
 * 4. 공통
 * 	- SELECT 쿼리 이후 지정한 SQL에 사용한 변수 값들을 RSMPA에 지정한다.
 *  - RSMAP에 존재 하지 않는 updateDetail.sql 의 ${RESULTMSG} 변수 값 지정 : RSMAP.put("RESULTMSG","결과코드맵 매핑 값");
 * 
 * const = final variable, let = general variable
 * 
*/
// 발송 키
var msgKey=RSMAP.get("MESSAGE_SEQNO");
// 로그 초기 설정
var fname="handle.js";
var logMessage="["+msgKey+"] 발송\t-\t";
logger.info("[{}]\t {}", fname, fname+"가 실행 되었습니다!!!");
//전환발송여부 
var isResend=true;
//시간 값
var nowDate=new Date().toISOString();
var yyyymmdd=nowDate.split('T')[0].replace(/\D/g,"");
var hhmmss=nowDate.split('T')[1].replace(/\D/g,"").substr(0, 6);

//기타 필요 변수 값 지정
//발송날짜 정보 NULL 여부 체크 후 통계 필요 ${SENDDATE}-YYYYMMDD- 변수 키 값 생성 및 지정
var sendDate=""+RSMAP.get("REGDATE");
if(sendDate==null){
	logger.info("[{}]\t {}",fname, "\"SEND_DATE\" NULL 값으로 인해 금일("+yyyymmdd+") 날짜로 설정");
	RSMAP.put("SENDDATE",yyyymmdd);
}else{
	sendDate=sendDate.replace(/\D/g,"").substr(0,6);
	RSMAP.put("SENDDATE",sendDate);
}
RSMAP.put("SENDDATE",yyyymmdd);

//알림톡 결과코드 맵
var codeMap=context.code("ALIMTOK");
//발송결과코드
var resultCode=RSMAP.get("SEND_RESULT_CODE1");
logMessage+="결과코드: "+resultCode+", ";
//발송결과메시지
var resultMsg=codeMap.get(RSMAP.get("SEND_RESULT_CODE1"));
logMessage+="결과메시지: "+resultMsg+", ";
//코드맵에 없다면 "알수없는 에러"로 설정
if(resultMsg == null){
	RSMAP.put("RESULTMSG","알수없는 에러");
	
}else{
	RSMAP.put("RESULTMSG",resultMsg);
}

//결과상태 처리 후 저장
if(resultCode==null){
	context.stack("rslt",2);
	logger.info("[{}]\t {}", fname, "결과코드 전환발송 없는 실패 처리");
} 
//성공여부 확인 및 결과 처리
if(resultCode=="OK"){
	//결과 처리
	context.stack("rslt",1);
	//일반 통계 처리
	RSMAP.put("STATIC_SEND", "1");
	RSMAP.put("STATIC_FAIL", "0");
	logMessage="발송 성공!!\t"+logMessage;
}else{
	logMessage="발송 실패!!\t"+logMessage;
	//전환 발송 여부 확인
	var back = RSMAP.get("BACKUP_MESSAGE");
	if(back==null){
		isResend=false;
	}
	//일반 통계 처리
	RSMAP.put("STATIC_SEND", "0");
	RSMAP.put("STATIC_FAIL", "1");
	//전환발송 실패, 일반발송 실패 처리, 원장 통계처리
	RSMAP.put("ALTCNT", "1");
	if(isResend){
		context.stack("rslt",3);
		RSMAP.put("FINALCNT", "0");
		RSMAP.put("SMSCNT", "1");
	}else{
		context.stack("rslt",2);
		RSMAP.put("FINALCNT", "1");
		RSMAP.put("SMSCNT",	"0");
	}
}

logger.info("[{}]\t {}",fname, logMessage);