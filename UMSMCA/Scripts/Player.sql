-- 회차 정보가 없을경우 cms_cnts_mng 정보
-- 회차 정보가 있을 경우 cms_cnts_seqn 정보
mem_mbr_terml_info
	device_id
	device_model
	
-- 컨텐츠 메인 정보
	SELECT
		s.mbr_id AS mbrId, s.cnts_vwr_crd_val AS cntsVwrCrdVal, s.cnts_id AS cntsId, 
		c.inn_pblc_no AS innPblcNo,	c.file_type AS fileType, c.cnts_ttl as cntsTtl
	FROM 
		svc_mbr_cnts_rels s 
	JOIN 
		cms_cnts_mng c 
	ON 
		s.cnts_id = c.cnts_id 
	WHERE 
		s.mbr_id ='C00000016' AND 
		s.cnts_id ='100000001003' AND 
		s.seqn_no =0 

-- 컨텐츠 회차 정보
	SELECT ttl FROM cms_cnts_seqn where cnts_id ='100000001083' and seqn_no= 9
	
	SELECT 
		s.ttl AS title , f.atch_file_path AS filePath, f.atch_file_nm AS fileName
	FROM 
		cms_cnts_atch_file f 
	JOIN 
		cms_cnts_seqn s 
	ON 
		f.cnts_id = s.cnts_id AND
		f.seqn_no = s.seqn_no 
	WHERE 
		f.cnts_id = '300000001167' AND
		f.seqn_no =	1 AND
		f.atch_file_div_cd ='CAD' AND 
		IFNULL(f.del_yn,'N') = 'N'
	
	SELECT
		'' AS seqnTtl , atch_file_path AS filePath, atch_file_nm AS fileName
	FROM 
		cms_cnts_atch_file
	WHERE 
		cnts_id = '300000001167' AND
		seqn_no =	1 AND
		atch_file_div_cd ='CAD' AND 
		IFNULL(del_yn,'N') = 'N'
		
		
	-- DATA 추출 및 검색	
	SELECT mbr_id, cnts_id, COUNT(*) as cnt FROM svc_mbr_cnts_rels group by mbr_id, cnts_id 
	SELECT * FROM cms_cnts_mng where cnts_id ='200000001017'
	SELECT * FROM svc_mbr_cnts_rels s join cms_cnts_mng c on s.cnts_id = c.cnts_id where s.mbr_id ='C00000016' AND s.cnts_id ='200000001017'
	
	select * from cms_cnts_atch_file where cnts_id='300000001167' and seqn_no=1
	select * from svc_mbr_cnts_rels where cnts_id='300000001167' and seqn_no=1

	
	update svc_mbr_cnts_rels set last_upd_dtm = now()
	where cnts_id='300000001167' and seqn_no=1 and mbr_id ='C00000016'
	commit
	
	SELECT * FROM cms_cnts_mng
	SELECT * FROM cms_cnts_seqn
	
	select now() from dual
	user-id = svc_mbr_cnts_rels.mbr_id
	id = svc_mbr_cnts_rels.cnts_id
	url = cms_cnts_mng.mov_url
	title = cms_cnts_mng.cnts_ttl
	position = svc_mbr_cnts_rels.cnts_vwr_crd_val
	TABLE: svc_mbr_cnts_rels
	
	COLUMN: cnts_vwr_crd_val	콘텐츠뷰어좌표값	VARCHAR	500


<position>0</position> // 포지션 값 사용시 0 이상 값, 사용 하지 않을 시 항목을 제거