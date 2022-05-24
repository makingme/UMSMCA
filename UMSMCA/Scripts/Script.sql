			SELECT 
				 s.send_sno 										/* 발송 관리 번호 */
				,s.trsm_div_cd 										/* 전송 구분코드 - 1063 */
				,c.cd_nm											/* 전송 구분명 - 1063 */
				,IFNULL(g.members, 0) as members					/* 발송 대상자수  */ 
				,s.msg_grp_cd										/* 메시지 그룹 코드 -1073 */
				,e.cd_nm as msgGropuNm								/* 메시지 그룹 명 -1073 */
				,s.send_div_cd										/* 발송구분 - 1059 */
				,s.tmplt_sno 										/* 템플릿번호 */
				,DATE_FORMAT(s.send_dtm, '%Y-%m-%d %T') as send_dtm	/* 발송 일시 */
				,s.msg_cn 											/* 발송 메시지 내용 */
				,s.send_stus_cd										/* 발송 상태 */
				,d.cd_nm as statusNm								/* 발송 상태 명 */
				,IFNULL(s.send_rslt, '-') as send_rslt 				/* 발송 결과 */
				,s.send_type 										/* 발송 유형 */
				,s.rcv_trgt_grp_cd 									/* 발송 그룹 코드 */
				,s.use_yn 											/* 사용 여부 */
				,s.rmk												/* 발송 요청 응답 내용 */
				,s.last_updr_id 									/* 최종 수정자 */
				,DATE_FORMAT(s.last_upd_dtm, '%Y-%m-%d %T') as last_upd_dtm  /* 최종 수정일 */
			FROM 
				com_send_mng s
			LEFT JOIN
				(select send_sno, count(send_sno) as members from com_rcv_trgt_grp group by send_sno) g
			ON
				s.send_sno = g.send_sno
			LEFT JOIN 
				(select cd_id, cd_nm from adm_cd_mng where cd_cls_id = '1063' ) c
			ON
				s.trsm_div_cd = c.cd_id
			LEFT JOIN
				(select cd_id, cd_nm from adm_cd_mng where cd_cls_id = '1065') d
			ON
				s.send_stus_cd = d.cd_id
			LEFT JOIN
				(select cd_id, cd_nm from adm_cd_mng where cd_cls_id = '1073') e
			ON
				s.send_stus_cd = e.cd_id