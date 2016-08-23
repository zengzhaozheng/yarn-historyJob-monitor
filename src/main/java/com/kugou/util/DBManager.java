package com.kugou.util;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Created by zhaozhengzeng on 2016/8/8.
 */
public class DBManager {

    private static DBManager instance;

    private static final Logger log = LoggerFactory.getLogger(DBManager.class);

    private DBManager() throws Exception {
        init();
    }
    /**
     *
     * 功能描述:
     * @throws Exception
     * @author chenhui / 2009-12-23 下午02:19:20
     */
    private void init() throws Exception {

        //初始化数据库连接配置参数
        InputStream in=this.getClass().getResourceAsStream("/proxool.xml");
        Reader reader = new InputStreamReader(in);


        JAXPConfigurator.configure(reader,false);
        log.info("Finish DBManager");
        return;
    }

    /**
     *
     * 方法用途和描述: 单例模式获取 DBManager 对象实例
     *
     * @return
     * @throws Exception
     * @author chenhui / 2009-12-23 下午02:19:20
     */
    public static synchronized DBManager getInstance() throws Exception {
        if (instance == null)
            instance = new DBManager();
        return instance;
    }


    /**
     *
     * 功能描述: 建立数据库别名为传入参数的连接池
     * @param DbAlias
     * @return
     * @throws Exception
     * @author chenhui / 2009-12-23 下午02:19:20
     */
    public Connection getConnection(String DbAlias) throws Exception {

        Connection conn = DriverManager.getConnection("proxool."+DbAlias);
        return conn;
    }
    /**
     *
     * 功能描述: 建立默认数据库别名的连接池
     * @return
     * @throws Exception
     * @author chenhui / 2009-12-23 下午02:19:20
     */
    public Connection getConnection() throws Exception {

        Connection conn = DriverManager.getConnection("proxool.DBPool");
        return conn;
    }
    /**
     *
     * 功能描述: 归还连接到连接池
     * @param conn
     * @author chenhui / 2009-12-23 下午02:19:20
     */
    public void freeConnection(Connection conn){

        try{
            if(conn!=null){
                conn.close();
            }

        }catch(Exception ex){
            log.error("freeConnError:",ex);
        }
    }

    public boolean executeDDL(String ddl){
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        boolean result = false;
        try{
            conn = DBManager.getInstance().getConnection();
            stmt = conn.createStatement();
            log.info("DDL:[" + ddl + "]");
            result = stmt.execute(ddl);
            log.info("DDL:[" + ddl + "],result="+result);
        }catch(Exception e){
            log.error("",e);
        }finally{
            if(rs!=null)
                try {
                    rs.close();
                } catch (SQLException e) {
                    log.error("",e);
                }
            if(stmt!=null)
                try {
                    stmt.close();
                } catch (SQLException e) {
                    log.error("",e);
                }
            if(conn!=null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("",e);
                }
        }
        return result;
    }
}
