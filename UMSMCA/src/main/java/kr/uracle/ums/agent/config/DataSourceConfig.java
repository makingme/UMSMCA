package kr.uracle.ums.agent.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.jolbox.bonecp.BoneCPDataSource;

import kr.msp.util.AES128Cipher;
import kr.uracle.ums.agent.common.LoadConfig;

@EnableTransactionManagement
@ComponentScan(basePackages = {"kr.uracle.ums.agent"},useDefaultFilters = true)
@Configurable
public class DataSourceConfig {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
    /****************************************************************************************************************
     *  JNDI 사용
     *****************************************************************************************************************/
/*
    @Bean(name = "jndiDataSource")
    public DataSource jndiDataSource() throws NamingException {
        return JndiLocatorDelegate.createDefaultResourceRefLocator().lookup("jdbc/msp_sample", DataSource.class);
    }
*/
    /****************************************************************************************************************
     *  BoneCP 사용
     *****************************************************************************************************************/
    @Bean
    public DataSource dataSource() {
        logger.info("###[DB] dataSource load start");
        String JDBC_URL = LoadConfig.getProperty(LoadConfig.JDBC_URL);
        String JDBC_USERNAME = LoadConfig.getProperty(LoadConfig.JDBC_USERNAME);
        String JDBC_PASSWORD = LoadConfig.getProperty(LoadConfig.JDBC_PASSWORD);
        if(LoadConfig.getProperty(LoadConfig.DBCP_ENC).equals("Y")){
            JDBC_URL = getDbcpDecode(JDBC_URL);
            JDBC_USERNAME = getDbcpDecode(JDBC_USERNAME);
            JDBC_PASSWORD = getDbcpDecode(JDBC_PASSWORD);
        }

        BoneCPDataSource ds = new BoneCPDataSource();
        ds.setDriverClass(LoadConfig.getProperty(LoadConfig.JDBC_DRIVERCLASSNAME));
        ds.setJdbcUrl(JDBC_URL);
        ds.setUsername(JDBC_USERNAME);
        ds.setPassword(JDBC_PASSWORD);
        ds.setMinConnectionsPerPartition(LoadConfig.getIntProperty(LoadConfig.MINCONNECTIONCNT));
        ds.setMaxConnectionsPerPartition(LoadConfig.getIntProperty(LoadConfig.MAXCONNECTIONCNT));
        ds.setIdleMaxAgeInSeconds(LoadConfig.getIntProperty(LoadConfig.IDLEMAXSECONDS));
        ds.setConnectionTestStatement(LoadConfig.getProperty(LoadConfig.CONNECTIONTESTSTATEMENT));
        ds.setIdleConnectionTestPeriodInMinutes(LoadConfig.getIntProperty(LoadConfig.IDLECONNECTIONTESTPERIODMIN));
        ds.setDisableConnectionTracking(true);
        logger.info("###[DB] dataSource completed");
        return ds;
    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        logger.info("###[DB] sqlSessionFactory start");
        String mapperSrc= "classpath:sqlMap/"+ LoadConfig.getProperty(LoadConfig.DBTYPE)+"/*.xml";
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:sqlMap/configuration.xml"));
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(mapperSrc));
        logger.info("###[DB] sqlSessionFactory completed");
        return bean.getObject();
    }

    @Bean(name="mspSessionTemplate")
    @Qualifier("mspSessionTemplate")
    public SqlSessionTemplate sqlSession(DataSource dataSource) throws Exception {
        return new SqlSessionTemplate(sqlSessionFactory(dataSource));
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    public PlatformTransactionManager annotationDrivenTransactionManager () {
        return transactionManager(); // reference the existing @Bean method above
    }

    private String getDbcpEncode(String str){
        AES128Cipher a256 = AES128Cipher.getInstance();
        String returnEncodedStr = null;
        try {
            returnEncodedStr = a256.AES_Encode(str);
        }catch (Exception e){
            e.printStackTrace();
        }
        return returnEncodedStr;
    }

    private String getDbcpDecode(String encodeStr) {
        AES128Cipher a256 = AES128Cipher.getInstance();
        String returnDecodedStr = null;
        try {
            returnDecodedStr = a256.AES_Decode(encodeStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnDecodedStr;
    }
}
