package kr.uracle.ums.agent.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kr.uracle.ums.agent.core.DBCPDataSourceFactory;
import kr.uracle.ums.agent.exception.NONConfigVariableSetException;
import kr.uracle.ums.agent.exception.NotSupportSQLSyntaxException;
import kr.uracle.ums.agent.proccess.ResultTask;

public class SQLConverter {
	private static final Logger logger = LoggerFactory.getLogger(ResultTask.class);

	String sourceSQL="";
	String executeSQL="";
	
	boolean noParam=true;
	
	List<VarInfo> varArray=new ArrayList<VarInfo>();
	public SQLConverter(String sourceSql) {
		this.sourceSQL=sourceSql;
		this.convert();
	}
	public SQLConverter() {
	}
	
	public void setSourceSQL(String sql) {
		this.sourceSQL=sql;
	}
	
	public String convert() {
		if(!validateSQL(this.sourceSQL)) {
			throw new NONConfigVariableSetException("SQL 문법 오류...");
		}
		Pattern p= Pattern.compile("\\$\\{[^\\s,;\\(\\)\\*\\+\\-]+\\}");
		Matcher m= p.matcher(sourceSQL);
		int order=1;
		while(m.find()) {
			VarInfo var=null;
			String content=m.group().replaceAll("(\\$|\\{|\\})", "");
			if(content.indexOf(":")>0) {
				String[] info=content.split(":");
				var=new VarInfo(info[1], info[0]);				
			}else {
				var=new VarInfo(content, "S");
			}
			var.setOrder(order++);
			varArray.add(var);
		}
		executeSQL=m.replaceAll("?");
		//logger.debug(executeSQL);
		return executeSQL;
	}
	
	public String convert(Map<String, String> params) {
		if(!validateSQL(this.sourceSQL)) {
			throw new NONConfigVariableSetException("SQL 문법 오류...");
		}
		String convertSQL=convert();
		Pattern p= Pattern.compile("\\#\\{[^\\s,;\\(\\)\\*\\+\\-]+\\}");
		Matcher m= p.matcher(convertSQL);
		while(m.find()) {
			String target=m.group();
			String key=target.replaceAll("(\\#|\\{|\\})", "");
			String value=params.get(key);
			if(value==null) {
				throw new NONConfigVariableSetException("SQL 문법 오류...");
			}
			convertSQL=convertSQL.replace(target, value);
			
		}
		return convertSQL;
	}
	
	public String getExecuteSQL() {
		return this.executeSQL;
	}
	
	public boolean validateSQL(String sql) {
		boolean isVaild=true;
		Pattern p = Pattern.compile("(^select|^update|^delete|^merge|^insert)", Pattern.CASE_INSENSITIVE);
		Matcher m= p.matcher(sql);
		if(!(isVaild=m.find())) {
			logger.warn("SQL 문법 확인 하세요. 지원 SQL- SELECT, UPDATE, INSERT, DELETE, MERGE\n"+sql);						
		}

		if(isVaild && noParam ) {
			Pattern p2 = Pattern.compile("\\#\\{[^\\s,;\\(\\)\\*\\+\\-]+\\}");
			m=p2.matcher(sql);
			if(m.find()) {
				String content=m.group();
				isVaild=false;
				logger.warn("Define and Put Param "+content+"key and value");
			}
			
		}
		return isVaild;
	}
	
	public boolean setSQLVar(PreparedStatement ps, Map<String, String> params) throws SQLException, FileNotFoundException{
		
		for(VarInfo var: varArray) {
			int type=var.getType();
			int index=var.getOrder();
			String key=var.getName();
			if(!params.containsKey(key)) {
					throw new NotSupportSQLSyntaxException("\nCan't Find  Matched KEY in RSMAP , Check the ["+key+"] keyword in Params, Check JS FILE RSMAP SETTING\n["+sourceSQL+"]\n");				
			}
			String value=params.get(key);
			/*
			 * if(value==null) { throw new
			 * NotSupportSQLSyntaxException("\nCan't Find  Matched Value in SQLINFO LIST , Check the ["
			 * +key+"] keyword in Params, Check JS FILE RSMAP And Put Value\n["+sourceSQL+
			 * "]\n"); }
			 */
			switch(type) {
				case VarInfo.TYPE_STRING: ps.setString(index, value);
				break;
				case VarInfo.TYPE_INT: ps.setInt(index, Integer.parseInt(value)); 
				break;
				case VarInfo.TYPE_LONG: ps.setLong(index, Long.parseLong(value)); 
				break;
				case VarInfo.TYPE_BLOB: ps.setBlob(index, new FileInputStream(new File(value))); 
				break;
				case VarInfo.TYPE_CLOB: ps.setClob(index, new FileReader(new File(value)));; 
				break;
			}
			
		}
		
		return true;
	}
	
	public class VarInfo{

		public static final int TYPE_STRING =1 ;
		public static final int TYPE_INT =2 ;
		public static final int TYPE_LONG =3 ;
		public static final int TYPE_BLOB =4 ;
		public static final int TYPE_CLOB =5 ;
		
		String name="variableName";
		int type=VarInfo.TYPE_STRING;
		int order=0;
		
		public VarInfo(String valName, String type) {
			this.name=valName;
			switch(type.toUpperCase()) {
			case "I" 	:	this.type=VarInfo.TYPE_INT;
			break;
			case "L" 	:	this.type=VarInfo.TYPE_LONG;
			break;
			case "B" 	:	this.type=VarInfo.TYPE_BLOB;
			break;
			case "C" 	:	this.type=VarInfo.TYPE_CLOB;
			break;
			default 	: 	this.type=VarInfo.TYPE_STRING;
			break;
			}
		}
		
		public int getOrder() {
			return order;
		}
		public int setOrder(int order) {
			this.order=order;
			return this.order;
		}
		
		public String getName() {
			return name;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public int getType() {
			return type;
		}
		
		public void setType(int type) {
			this.type = type;
		}
		
	}
	
	public static void main(String[] args) throws Exception {
//		String testSQL="select * from T_UMS_SEND where UMS_SEQNO = ${I:UMS_SEQNO}";
//		SQLConverter converter=new SQLConverter(testSQL);
//		converter.convert();
//		System.out.println(converter.executeSQL);
//		
//		Connection con=DBCPDataSourceFactory.getConnection();
//		PreparedStatement ps=con.prepareStatement(converter.executeSQL);
//		System.out.println(ps);
//		Map<String, String> map=new HashMap<String,String>();
//		
//		map.put("UMS_SEQNO", "82");
//		converter.setSQLVar(ps, map);
//		
//		ResultSet rs=ps.executeQuery();
//		ResultSetMetaData rsmd = rs.getMetaData();
//		int colCount=rsmd.getColumnCount();
//		int rowcount = 0;
//		
//		while(rs.next()) {
//			rowcount++;
//			for(int i=1; i<=colCount;i++) {
//				System.out.print(rsmd.getColumnName(i)+": "+rs.getString(i)+", ");
//			}
//			System.out.println();
//			
//		}
//		System.out.println(colCount+", "+rowcount);
//		System.out.println(rs.getStatement().toString()+"]");
//		con.close();
	}
	
}
