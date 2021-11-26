package kr.uracle.ums.agent.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

import kr.uracle.ums.agent.config.UProperties;

public class DBCPDataSourceFactory {
	private static BasicDataSource mainDS=null;
	private static boolean autoCommit=false;
	static {
		try {
			mainDS = new BasicDataSource();
			UProperties properties = new UProperties();
			//InputStream is = DBCPDataSource.class.getClassLoader().getResourceAsStream("conf/db.properties");
			InputStream is = new FileInputStream(new File("conf/db.properties"));
			properties.load(is);
			mainDS.setDriverClassName(properties.getProperty("driver"));
			mainDS.setUrl(properties.getProperty("url"));
			mainDS.setUsername(properties.getProperty("user"));
			mainDS.setPassword(properties.getProperty("pw"));
			mainDS.setInitialSize(5);;
			mainDS.setMaxTotal(5);
			autoCommit=properties.getProperty("autocommit", false);
			//.....
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() throws SQLException {
		Connection con = mainDS.getConnection();
		con.setAutoCommit(autoCommit);
		return con;
	}

	private DBCPDataSourceFactory() {
	}
}
