select * from adm_menu_mng

select * from adm_cd_mng
select  * from com_tmplt where 1=1 and tmplt_div_cd in ('SMS')

select tmplt_grp_sno , tmplt_grp_nm from com_tmplt_grp where 1=1 order by last_upd_dtm desc


and use_yn ='Y'


select * FROM com_tmplt
select * FROM com_tmplt_grp

DELETE FROM com_tmplt WHERE last_updr_id = 'A00000027'
DELETE FROM com_tmplt_grp WHERE last_updr_id = 'A00000027'

SELECT
	c1.tmplt_div_cd,
	c2.tmplt_grp_nm,
	c1.tmplt_nm,
	c1.use_yn,
	c1.fst_regr_id
FROM com_tmplt c1 join com_tmplt_grp c2 on c1.tmplt_grp_sno = c2.tmplt_grp_sno 
	SELECT		c1.tmplt_div_cd,
				c2.tmplt_grp_nm,
				c1.tmplt_nm,
				c1.use_yn,
				c1.fst_regr_id
			FROM 
				com_tmplt c1 
			JOIN
				com_tmplt_grp c2 
			ON 
				c1.tmplt_grp_sno = c2.tmplt_grp_sno 
			WHERE 1=1
	
	
			order by c1.reg_dtm desc