SELECT * FROM
	(
    	SELECT 
    		*
        FROM 
        	TSMS_AGENT_MESSAGE_LOG t1
        JOIN
        	T_UMS_SEND_DETAIL t2
        ON
        	t1.MESSAGE_SEQNO = t2.KKO_ALT_SEQNO
        JOIN
        	T_UMS_SEND t3
        ON
        	t2.UMS_SEQNO = t3.UMS_SEQNO
        WHERE 
        	t1.REGISTER_DATE > SYSDATE -250 AND t1.REGISTER_DATE < SYSDATE AND t1.START_SEND_TYPE ='UMSUI'
        ORDER BY 
        	MESSAGE_SEQNO ASC
	)
WHERE 
	ROWNUM<=${I:FETCHCNT}