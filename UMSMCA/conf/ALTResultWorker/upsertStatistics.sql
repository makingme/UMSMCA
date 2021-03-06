MERGE INTO 
	T_UMS_SEND_STATISTICS t  
	USING 
		DUAL
	ON 
		(t.SENDERSYSTEM=${START_SEND_TYPE} and t.SENDDATE=${SENDDATE} and t.SENDERID=${SENDERID} and t.SENDTYPE=${SEND_KIND} and t.OPTIONAL=${KKOALT_SVCID})
 	WHEN MATCHED THEN
      	UPDATE SET 
      		t.SEND_CNT=t.SEND_CNT+${STATIC_SEND}, t.FAIL_CNT=t.FAIL_CNT+${STATIC_FAIL}
 	WHEN NOT MATCHED THEN 
      	INSERT (t.SENDERSYSTEM, t.SENDDATE, t.SENDERID, t.OPTIONAL, t.SENDERGROUP, t.SENDTYPE, t.SEND_CNT, t.FAIL_CNT)
      	VALUES (${START_SEND_TYPE},${SENDDATE},${SENDERID},${KKOALT_SVCID},${SENDGROUPCODE},${SEND_KIND},${STATIC_SEND},${STATIC_FAIL})    