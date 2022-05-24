			select t1.cd_cls_id as cd_cls_id
			     , t1.cd_cls_nm as cd_cls_nm
			     , t2.cd_id     as cd_id
			     , t2.cd_nm     as cd_nm
			     , t2.cd_sn     as cd_sn
			 from adm_cd_cls t1
			 left join adm_cd_mng t2
			   on t1.cd_cls_id = t2.cd_cls_id
		    where 1 = 1
		      and t1.use_yn = 'Y'
		      and t2.use_yn = 'Y'
		      order by cd_cls_id DESC

--C00000024
SELECT fn_generate_id('MS',10)
select * from mem_mbr_basic
select * from mem_mbr_send_hist 
select * from com_tmplt

SELECT fn_biz_msg_seq()
select now()
SELECT	COUNT(*) FROM mem_mbr_basic	WHERE mbr_id = 'C00000022'

select * from BIZ_LOG where REQUEST_TIME > '2021-12-24 00:12:24' order by REQUEST_TIME desc
delete from BIZ_LOG where REQUEST_TIME > '2021-12-24 00:12:24'

select * from mem_mbr_send_hist where send_dtm  > '2022-01-05 00:12:24'
delete from mem_mbr_send_hist where send_dtm  > '2022-01-05 00:12:24'


delete from BIZ_MSG where REQUEST_TIME < now()

commit

-- 발송 상태
select * from adm_cd_mng where cd_cls_id = '1065'
-- 샘플
select * from adm_cd_cls where cd_cls_id = '1085'
-- 전송 채널 구분 코드
select * from adm_cd_mng where cd_cls_id = '1063'
-- 메시지 타입 그룹 코드
select * from adm_cd_mng where cd_cls_id = '1073'
-- 발송 종류 구분(즉발, 예발, 반발)
select * from adm_cd_mng where cd_cls_id = '1059'
-- 발송대상 그룹
select * from adm_cd_mng where cd_cls_id = '1085'
-- 회원상태
select * from adm_cd_mng where cd_cls_id = '1005'
 -- 발신번호
select * from adm_cd_mng where cd_cls_id = '1086'
 -- 발신 메일
select * from adm_cd_mng where cd_cls_id = '1087'

select * from adm_cd_mng where cd_nm like '%한우리%'

SELECT 
	ttl AS templeteTitle,	cn AS templeteContent
FROM 
	com_tmplt 
WHERE 
	tmplt_sno = #{TEMPLATE_CODE} and 
	tmplt_div_cd = #{CANNEL} and 
	use_yn = 'Y'
	


	
	INSERT INTO com_tmplt (tmplt_sno, tmplt_grp_sno, tmplt_div_cd, cnct_tmplt_no, tmplt_nm, ttl, cn, reg_dtm, use_yn, fst_regr_id, fst_reg_dtm, fst_reg_pgm, last_updr_id, last_upd_dtm, last_upd_pgm)
  SELECT 5 as tmplt_sno, tmplt_grp_sno, tmplt_div_cd, cnct_tmplt_no, tmplt_nm, ttl, cn, reg_dtm, use_yn, fst_regr_id, fst_reg_dtm, fst_reg_pgm, last_updr_id, last_upd_dtm, last_upd_pgm
  FROM com_tmplt WHERE tmplt_div_cd = 'SMS';
	
	
SELECT a.cd_nm, ct.ttl , ct.cn 
		FROM com_tmplt ct
		left join adm_cd_mng a on a.cd_id = ct.tmplt_div_cd and  a.cd_cls_id = '1063'
		where ct.tmplt_sno = 1;

commit

INSERT INTO BIZ_MSG (
	UMID, MSG_TYPE, STATUS, CALL_STATUS,
	REQUEST_TIME, SEND_TIME, REPORT_TIME, DEST_PHONE, SEND_PHONE, 
	DEST_NAME, SEND_NAME, SUBJECT, MSG_BODY, NATION_CODE, 
	SENDER_KEY, TEMPLATE_CODE, RESPONSE_METHOD, TIMEOUT, RE_TYPE,
	RE_BODY, RE_PART, COVER_FLAG, SMS_FLAG, REPLY_FLAG,
	RETRY_CNT, ATTACHED_FILE, VXML_FILE, USE_PAGE, USE_TIME,
	SN_RESULT, TEL_INFO, CINFO, USER_KEY, AD_FLAG,
	RCS_REFKEY, USER_NO
) VALUES(
	null, #{MSG_TYPE}, null, null,
	now(), #{SEND_TIME}, null, #{DEST_PHONE}, #{SEND_PHONE},
	#{DEST_NAME}, #{SEND_NAME}, #{SUBJECT}, #{MSG_BODY}, #{NATION_CODE},
	#{SENDER_KEY}, #{TEMPLATE_CODE}, null, #{TIMEOUT}, #{RE_TYPE},
	#{RE_BODY}, #{RE_PART}, null, null, null,
	null, #{ATTACHED_FILE}, null, null, null,
	null, null, #{CINFO}, null, null,
	#{RCS_REFKEY}, #{USER_NO}
)


INSERT INTO BIZ_MSG (
	CMID, UMID, MSG_TYPE, STATUS, CALL_STATUS,
	REQUEST_TIME, SEND_TIME, REPORT_TIME, DEST_PHONE, SEND_PHONE, 
	DEST_NAME, SEND_NAME, SUBJECT, MSG_BODY, NATION_CODE, 
	SENDER_KEY, TEMPLATE_CODE, RESPONSE_METHOD, TIMEOUT, RE_TYPE,
	RE_BODY, RE_PART, COVER_FLAG, SMS_FLAG, REPLY_FLAG,
	RETRY_CNT, ATTACHED_FILE, VXML_FILE, USE_PAGE, USE_TIME,
	SN_RESULT, TEL_INFO, CINFO, USER_KEY, AD_FLAG,
	RCS_REFKEY, USER_NO
) VALUES(
	'1', null, '0', null, null,
	now(), now(), null, '01026313590', '022334567',
	null, null, null, 'SMS발송 테스트', null,
	null, null, null, 3,'RS',
	'재전송 발송', 'C', null, null, null,
	null, null, null, null, null,
	null, null, null, null, null,
	null, null
)

INSERT INTO dmaru.mem_mbr_basic
(mbr_id, mbr_stus_cd, mbr_nm, brdt, sex, mobl_telno, mbr_email, lgin_id, lgin_pwd, mobl_pors_certi_yn, slf_certi_yn, slf_certi_dtm, cid, adlt_certi_yn, adlt_certi_dtm, chld_mbr_yn, fam_mbr_yn, fam_mbr_id, alli_mbr_yn, prtcr_nm, prtcr_mobl_telno, legal_agntr_sno, easy_subs_yn, acct_lckd_yn, pwd_initl_yn, pwd_chg_dtm, last_lgin_dtm, auto_lgin_yn, auto_lgin_dtm, subs_path_cd, subs_path_url, subs_dtm, cttpc_chg_yn, prtcr_cttpc_chg_yn, email_chg_yn, fst_regr_id, fst_reg_dtm, fst_reg_pgm, last_updr_id, last_upd_dtm, last_upd_pgm)
VALUES('C00000999', 'MS1', '메일테스트', NULL, NULL, '01026313590', '', 'mailtest1', 'wkdehf0722', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'UNKNOWN', '2021-12-18 19:12:39', 'Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Mobile Safari/537.36', 'UNKNOWN', '2021-12-18 19:12:39', 'Mozilla/5.0 (Linux; Android 5.0; SM-G900P Build/LRX21T) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.110 Mobile Safari/537.36');


	


 select *
		from 	svc_mbr_cnts_rels
		
		WHERE
			mbr_id = 'C00000019'   AND 
			cnts_id = '400000006301' AND 
			seqn_no = '1'
