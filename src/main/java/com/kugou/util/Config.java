package com.kugou.util;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by zhaozhengzeng on 2016/8/8.
 */
public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private static CompositeConfiguration config;
    private static String configFile = "conf.properties";

    /**
     *
     * 设置配置文件的路径必须是绝对路径如：
     * 如果不设置将采用默认的路径，在classpath的目录中里查找
     * @author chenxinwei 新增日期2011-12-28
     * @param configFile
     */
    public static void setConfigFile(String configFile){
        Config.configFile = configFile;
    }


    public static synchronized CompositeConfiguration getConfig(){
        if(config==null){
            try {
                config = new CompositeConfiguration();
                if(configFile == null) configFile = Config.class.getResource("src/main/resources/conf.properties").getFile();
                configFile = java.net.URLDecoder.decode(configFile,"utf-8");
                PropertiesConfiguration propsConfig = new PropertiesConfiguration();
                propsConfig.setEncoding("UTF-8");
                propsConfig.load(configFile);
                FileChangedReloadingStrategy propsReload = new FileChangedReloadingStrategy();
                propsReload.setRefreshDelay(60000L);
                propsConfig.setReloadingStrategy(propsReload);
                config.addConfiguration(propsConfig);
            } catch (Exception ex) {
                System.out.print(ex);
                logger.error("", ex);
            }
        }
        return config;
    }


}
