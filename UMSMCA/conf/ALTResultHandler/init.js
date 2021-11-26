/*
	ResultTask info.JS  
	1. 전달 PARAMS
		1) Context Instance
			(1) 목적
				A. JAVA와 Script Layer 간의 공유 자원
			(1) session map
				A. TASKNAME
					A) context.session("TASKNAME");
			(2) info map
				A. info.js 수행 후 script layer에서 산출된 정보 저장
			(3) stack map 
				A. handle.js 수행 후 script layer에서 산출된 정보 저장
				B. handle.js 수행 전 stack map은 clear 상태임 
		2) Logger Instance 
			(1) 목적
				A. log 포맷 통일
	2. 필수 설정 
		1) SQL 파일 로딩 및 info 저장
			(1) SQL 파일 읽어 오기
			(2) info 맵에 저장
				A. 필히 지정된 키로 지정해야함(하위참조)
	3. 부가 설정
		1) SQLTASK 여부 설정
			(1) Task Type을 지정함
			(2) isSqlTask 'Y' 혹은 'N' 값 지정
				A. context.info("isSqlTask", "Y");
				B. Default 'Y'
				C. 'Y'로 지정 시 SQL 설정 필수
		2) 추가 INSERT 사용 여부 설정
			(1) 추가 INSERT가 필요 시 지정함
			(2) extraInsertResend 'Y' 혹은 'N' 값 지정
				A. context.info("extraInsertResend", "Y");
				B. Default 'N'
				C. 'Y'로 지정 시 SQL 설정 필수
			(3) SQL 설정
				A. const extraInsertResend=FileLoader.readLineFile("extraInsertResend.sql", taskName);
				B. context.info("extraInsertResend", extraInsertResend);
		3) 추가 UPDATE 사용 여부 설정
			(1) 추가 UPDATE가 필요 시 지정함
			(2) extraUpdateResult 'Y' 혹은 'N' 값 지정
				A. context.info("extraUpdateResult", "Y");
				B. Default 'N'
				C. 'Y'로 지정 시 SQL 설정 필수
			(3) SQL 설정
				A. const extraUpdateResult=FileLoader.readLineFile("extraUpdateResult.sql", taskName);
				B. context.info("extraUpdateResult", extraUpdateResult);
*/
const taskName=context.session("TASKNAME");
const FileLoader = Java.type("kr.uracle.ums.agent.util.FileLoader");

//JSON 파일 읽어오기 (ALIMTOK ErrorCode)
const jsonMap=FileLoader.loadJsonToMap("ALIMTOK_CODE.json", "common");
context.code("ALIMTOK", jsonMap);

//SQL 파일 읽어오기
const selectResult=FileLoader.loadTextToString("selectResult.sql", taskName);
const insertResend=FileLoader.loadTextToString("insertResend.sql", taskName);
const insertResendDetail=FileLoader.loadTextToString("insertResendDetail.sql", taskName);
const updateDetail=FileLoader.loadTextToString("updateDetail.sql", taskName);
const updateLedger=FileLoader.loadTextToString("updateLedger.sql", taskName);
const upsertStatistics=FileLoader.loadTextToString("upsertStatistics.sql", taskName);
const deleteResult=FileLoader.loadTextToString("deleteResult.sql", taskName);

//SQL 파일 info 맵에 저장
context.info("selectResult", selectResult);
context.info("insertResend", insertResend);
context.info("insertResendDetail", insertResendDetail);
context.info("updateDetail", updateDetail);
context.info("updateLedger", updateLedger);
context.info("upsertStatistics", upsertStatistics);
context.info("deleteResult", deleteResult);

context.info("isSqlTask", "Y");
context.info("extraInsertResend", "N");
context.info("extraUpdateResult", "N");

