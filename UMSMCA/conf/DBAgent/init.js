const info={	"selectkey.sql":"select",
				"update.sql":"update",
				"select.sql":"select",
				"delete.sql":"delete",
				"order":"selectkey.sql,update.sql,select.sql,handle.js,delete.sql"
				};

context.init("isSqlTask", "Y");				
context.init("sqlInfo", sqlList);
