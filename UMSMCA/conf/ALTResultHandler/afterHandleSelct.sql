SELECT 
	* 
FROM
	#{TAGETTABLE}
where
	KKO_ALT_SEQNO = ${KKO_ALT_SEQNO} AND
	MOBILE_NUM = ${MOBILE_NUM}
	
