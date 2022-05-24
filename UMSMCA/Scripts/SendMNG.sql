show variables like 'max_connections';
show status where variable_name = 'threads_connected';
show global variables like 'wait_timeout';
show variables like 'interactive%';

select count(*) from information_schema.processlist where command='Sleep';

select 
	 s.send_sno 
	,s.trsm_div_cd 
	,g.members
	,c.cd_nm 
	,s.msg_grp_cd
	,s.send_div_cd
	,s.tmplt_sno 
	,s.send_dtm 
	,s.msg_cn 
	,s.send_rslt 
	,s.send_type 
	,s.rcv_trgt_grp_cd 
	,s.use_yn 
	,s.rmk
	,s.last_updr_id 
	,s.last_upd_dtm 
from 
	com_send_mng s
join
	(select send_sno, count(send_sno) as members from com_rcv_trgt_grp group by send_sno) g
on 
	s.send_sno = g.send_sno
join 
	adm_cd_mng c
on
	s.trsm_div_cd = c.cd_id
WHERE 
	1 = 1

order by s.last_upd_dtm 

select * from com_send_mng where last_upd_dtm > '2021-12-01' and last_upd_dtm < now() 
-- com_rcv_trgt_grp


select * from com_rcv_trgt_grp where last_upd_dtm  > '2022-01-19'
delete from com_rcv_trgt_grp where last_upd_dtm  > '2022-01-19'

select * from com_send_mng where last_upd_dtm  > '2022-01-19'
delete from com_send_mng where last_upd_dtm  > '2022-01-19'

select * from mem_mbr_send_hist where send_dtm  > '2022-01-19'
delete from mem_mbr_send_hist where send_dtm  > '2022-1-19'

commit;

select g.tmplt_grp_sno, g.tmplt_grp_nm from com_tmplt_grp g join com_tmplt t on g.tmplt_grp_sno = t.tmplt_grp_sno where g.use_yn = 'Y' and t.tmplt_div_cd ='EML' GROUP BY g.tmplt_grp_sno 	order by g.tmplt_grp_sno desc


-- 회원 전체, 성인, 어린이
select count(*) from mem_mbr_basic where 1 = 1 and mobl_telno is not null and chld_mbr_yn ='N'

-- 한우리 회원 전체, 한우리 성인, 한우리 학부모, 한우리 교사, 한우리 어린이
select count(*) from mem_mbr_basic b join mem_alli_mbr a on b.mbr_id = a.mbr_id where b.mobl_telno is not null and a.afco_id = 'AF0000001' and a.alli_mbr_div_cd = 'T'

select  count(*) from mem_mbr_basic
SELECT  b.mbr_id, b.mbr_stus_cd	, b.mbr_nm	, b.lgin_id	, b.mbr_email, b.mobl_telno	, b.fam_mbr_yn	, b.chld_mbr_yn	, a.*	FROM	mem_mbr_basic b	LEFT JOIN	mem_alli_mbr a ON b.mbr_id = a.mbr_id
-- C00000026
select * from mem_infom_agre_hist 






				

				
		