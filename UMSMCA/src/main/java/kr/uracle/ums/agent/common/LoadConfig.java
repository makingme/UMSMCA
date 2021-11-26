package kr.uracle.ums.agent.common;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by IntelliJ IDEA.
 * User: mium2(Yoo Byung Hee)
 * Date: 2015-04-21
 * Time: 오후 4:24
 * To change this template use File | Settings | File Templates.
 */
public class LoadConfig {
    private static ConcurrentMap<String, String> properties = new ConcurrentHashMap<String, String>();
    public static String UMSAGENT_ID = "umsAgent.serverID";

    public static String UMS_LOCAL_URL = "ums.localUrl";
    public static String UMS_FAILOVER_URLS = "ums.failoverUrls";
    public static String UMS_CHECK_LOOP_TIME = "ums.checkLoopTime";

    public static String DBTYPE = "ums_db.dbtype";
    public static String DBCP_ENC = "ums_db.dbcp_enc";
    public static String JDBC_DRIVERCLASSNAME = "ums_db.jdbc.driverClassName";
    public static String JDBC_URL = "ums_db.jdbc.url";
    public static String JDBC_USERNAME = "ums_db.jdbc.username";
    public static String JDBC_PASSWORD = "ums_db.jdbc.password";
    public static String MINCONNECTIONCNT = "ums_db.jdbc.minConnectionsPerPartition";
    public static String MAXCONNECTIONCNT = "ums_db.jdbc.maxConnectionsPerPartition";
    public static String IDLEMAXSECONDS = "ums_db.jdbc.idleMaxAgeInSeconds";
    public static String IDLECONNECTIONTESTPERIODMIN = "ums_db.jdbc.idleConnectionTestPeriodInMinutes";
    public static String CONNECTIONTESTSTATEMENT = "ums_db.jdbc.connectionTestStatement";

    public static void Load(String filename) throws ConfigurationException {
        //기본 delimiter 가 , 여서 하는 설정
        XMLConfiguration.setDefaultListDelimiter((char) (0));
        XMLConfiguration xml = new XMLConfiguration(filename);

        properties.put(DBTYPE, xml.getString(DBTYPE, "oracle") );
        properties.put(DBCP_ENC, xml.getString(DBCP_ENC, "N"));
        properties.put(JDBC_DRIVERCLASSNAME, xml.getString(JDBC_DRIVERCLASSNAME,"") );
        properties.put(JDBC_URL, xml.getString(JDBC_URL,"") );
        properties.put(JDBC_USERNAME, xml.getString(JDBC_USERNAME,"") );
        properties.put(JDBC_PASSWORD, xml.getString(JDBC_PASSWORD,"") );
        properties.put(MINCONNECTIONCNT, xml.getString(MINCONNECTIONCNT,"0") );
        properties.put(MAXCONNECTIONCNT, xml.getString(MAXCONNECTIONCNT,"10") );
        properties.put(IDLEMAXSECONDS, xml.getString(IDLEMAXSECONDS,"3600") );
        properties.put(IDLECONNECTIONTESTPERIODMIN, xml.getString(IDLECONNECTIONTESTPERIODMIN,"5") );
        properties.put(CONNECTIONTESTSTATEMENT, xml.getString(CONNECTIONTESTSTATEMENT,"select 1") );

    }


    public static void reLoad(String filename) throws ConfigurationException {
        //기본 delimiter 가 , 여서 하는 설정
        XMLConfiguration.setDefaultListDelimiter((char) (0));
        XMLConfiguration xml = new XMLConfiguration(filename);
    }

    public static int getIntProperty(String name) {
        return Integer.parseInt(properties.get(name));
    }

    public static String getProperty(String name) {
        return properties.get(name);
    }
    public static void setProperties(String key, String value){
        properties.put(key,value);
    }

    public static long getLongProperty(String name) {
        return Long.parseLong(properties.get(name));
    }
}
