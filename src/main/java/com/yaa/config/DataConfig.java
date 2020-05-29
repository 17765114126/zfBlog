package com.yaa.config;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@Component
@Order(value=1)
public class DataConfig implements CommandLineRunner {

    private Logger logger = LoggerFactory.getLogger(DataConfig.class);

    @Resource
    private PathConfig pathConfig;
    @Resource
    private DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        // 建立连接
        Connection conn = dataSource.getConnection();
        try {
            String sql = "select count(*) from information_schema.tables where table_schema='"+pathConfig.getDatabase()+"'";
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            int count = 0;
            while (rs!=null && rs.next()){
                count = rs.getInt(1);
            }
            if (count == 0) {
                logger.info("initialize import database starting");
                // 创建ScriptRunner，用于执行SQL脚本
                ScriptRunner runner = new ScriptRunner(conn);
                runner.setErrorLogWriter(null);
                runner.setLogWriter(null);
                runner.runScript(Resources.getResourceAsReader("db/schema.sql"));
                logger.info("initialize import database finished");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            conn.close();
        }
    }

}
