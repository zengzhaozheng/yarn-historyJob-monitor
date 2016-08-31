package com.kugou.services;

import com.kugou.entity.HistoryInfo;
import com.kugou.entity.Metric;
import com.kugou.util.Config;
import com.kugou.util.Contants;
import com.kugou.util.DBUtil;
import com.kugou.util.ServiceUtil;
import org.apache.commons.configuration.CompositeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhaozhengzeng on 2016/8/12.
 */
public class SparkMetricWorkThread implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(SparkMetricWorkThread.class);
    private String yarnAppID;
    private String yarnQueue;
    private String arcType;
    private String yarnSumbitName;
    private long mb_millis_maps;
    private long mb_millis_reduces;
    private long vcores_millis_maps;
    private long vcores_millis_reduces;
    private long yarnJobStartTimeStamp;
    private DBUtil dbUtil;
    private String sumbitDay;

    public SparkMetricWorkThread(String yarnAppID, String yarnSumbitName, String yarnQueue, String arcType, long yarnJobStartTimeStamp, String sumbitDay) {
        this.sumbitDay = sumbitDay;
        this.yarnJobStartTimeStamp = yarnJobStartTimeStamp;
        this.arcType = arcType;
        this.yarnAppID = yarnAppID;
        this.yarnQueue = yarnQueue;
        this.yarnSumbitName = yarnSumbitName;
        this.dbUtil = new DBUtil();
    }


    public void run() {
        Set<Metric> metricSet = new HashSet<Metric>();
        Metric metric1 = new Metric();
        Metric metric2 = new Metric();
        logger.info(Thread.currentThread().getName() + "running....");
        CompositeConfiguration configPropertiy = Config.getConfig();

        HistoryInfo historyInfo = ServiceUtil.getHistoryInfo(this.yarnAppID, this.yarnSumbitName);
        HashMap<String, String> counters = historyInfo.getFile_system_conters();

        Set<String> keys = counters.keySet();
        for (String counterTitile : keys) {
            if (counterTitile.trim().equals("Total megabyte-seconds taken by all map tasks")) {
                this.mb_millis_maps = Long.parseLong(counters.get(counterTitile).split("\\|")[2]);
            } else if (counterTitile.trim().equals("Total megabyte-seconds taken by all reduce tasks")) {
                this.mb_millis_reduces = Long.parseLong(counters.get(counterTitile).split("\\|")[2]);
            } else if (counterTitile.trim().equals("Total vcore-seconds taken by all map tasks")) {
                this.vcores_millis_maps = Long.parseLong(counters.get(counterTitile).split("\\|")[2]);
            } else if (counterTitile.trim().equals("Total vcore-seconds taken by all reduce tasks")) {
                this.vcores_millis_reduces = Long.parseLong(counters.get(counterTitile).split("\\|")[2]);
            }
        }
        try {
            boolean isComeFromKws = this.dbUtil.isComeFromKWS(configPropertiy.getString("kwsDbPoolName"), this.yarnAppID);

            if (isComeFromKws) {
                String kwsJobID = this.dbUtil.getKwsJobStat(configPropertiy.getString("kwsDbPoolName"), this.yarnAppID).getKwsJobID();
                int kwsParentJobID = this.dbUtil.getKwsGroupInfo(configPropertiy.getString("kwsDbPoolName"), kwsJobID).getParentGroupID();
                metric1.setClusterPlat("t10");
                metric1.setKwsUserSumbitUser(this.dbUtil.getKwsJobStat(configPropertiy.getString("kwsDbPoolName"), this.yarnAppID).getKwsJobOwner());
                metric1.setHadoopSumbitUser(this.yarnSumbitName);
                metric1.setKwsJobName(this.dbUtil.getKwsJobStat(configPropertiy.getString("kwsDbPoolName"), this.yarnAppID).getKwsJobName());
                metric1.setKwsJobGroupName(this.dbUtil.getKwsGroupInfo(configPropertiy.getString("kwsDbPoolName"), kwsJobID).getGroupName().replaceAll("[()]", ""));
                metric1.setKwsJobParentGroupName(this.dbUtil.getKwsGroupName(configPropertiy.getString("kwsDbPoolName"), kwsParentJobID).replaceAll("[()]", ""));
                metric1.setYarnQueue(this.yarnQueue);
                metric1.setContainerType(this.arcType);
                metric1.setYarnJobStartTimeStamp(this.yarnJobStartTimeStamp / 1000);
                metric1.setDt(this.sumbitDay);
                metric1.setIsComeFromKWS(1);

                metric2.setClusterPlat("t10");
                metric2.setKwsUserSumbitUser(this.dbUtil.getKwsJobStat(configPropertiy.getString("kwsDbPoolName"), this.yarnAppID).getKwsJobOwner());
                metric2.setHadoopSumbitUser(this.yarnSumbitName);
                metric2.setKwsJobName(this.dbUtil.getKwsJobStat(configPropertiy.getString("kwsDbPoolName"), this.yarnAppID).getKwsJobName());
                metric2.setKwsJobGroupName(this.dbUtil.getKwsGroupInfo(configPropertiy.getString("kwsDbPoolName"), kwsJobID).getGroupName().replaceAll("[()]", ""));
                metric2.setKwsJobParentGroupName(this.dbUtil.getKwsGroupName(configPropertiy.getString("kwsDbPoolName"), kwsParentJobID).replaceAll("[()]", ""));
                metric2.setYarnQueue(this.yarnQueue);
                metric2.setContainerType(this.arcType);
                metric2.setYarnJobStartTimeStamp(this.yarnJobStartTimeStamp / 1000);
                metric2.setDt(this.sumbitDay);
                metric2.setIsComeFromKWS(1);
            } else {
                metric1.setClusterPlat("t10");
                metric1.setKwsUserSumbitUser("notExist");
                metric1.setHadoopSumbitUser(this.yarnSumbitName);
                metric1.setKwsJobName("notExist");
                metric1.setKwsJobGroupName("notExist");
                metric1.setKwsJobParentGroupName("notExist");
                metric1.setYarnQueue(this.yarnQueue);
                metric1.setContainerType(this.arcType);
                metric1.setYarnJobStartTimeStamp(this.yarnJobStartTimeStamp / 1000);
                metric1.setDt(this.sumbitDay);
                metric1.setIsComeFromKWS(0);

                metric2.setClusterPlat("t10");
                metric2.setKwsUserSumbitUser("notExist");
                metric2.setHadoopSumbitUser(this.yarnSumbitName);
                metric2.setKwsJobName("notExist");
                metric2.setKwsJobGroupName("notExist");
                metric2.setKwsJobParentGroupName("notExist");
                metric2.setYarnQueue(this.yarnQueue);
                metric2.setContainerType(this.arcType);
                metric2.setYarnJobStartTimeStamp(this.yarnJobStartTimeStamp / 1000);
                metric2.setDt(this.sumbitDay);
                metric2.setIsComeFromKWS(0);
            }
            String memeoryScore = configPropertiy.getString("memoryScore");
            String cpuScore = configPropertiy.getString("cpuScore");
            ScriptEngineManager mgr = new ScriptEngineManager();
            ScriptEngine engine = mgr.getEngineByName("JavaScript");

           // double megabyte_seconds_value = (this.mb_millis_maps + this.mb_millis_reduces) / (Double) engine.eval(memeoryScore);
           // double vcores_seconds_value = (this.vcores_millis_maps + this.vcores_millis_reduces) / (Double) engine.eval(cpuScore);
            double megabyte_seconds_value = this.mb_millis_maps + this.mb_millis_reduces;
            double vcores_seconds_value = this.vcores_millis_maps + this.vcores_millis_reduces;

            metric1.setMetricName(Contants.MEMORY_CONSUME);
            metric1.setValue(megabyte_seconds_value);
            metric2.setMetricName(Contants.CPU_CONSUME);
            metric2.setValue(vcores_seconds_value);

            metricSet.add(metric1);
            metricSet.add(metric2);

            ImetricStorage imetricStorage = new ImetricStorageMysqlImpl();
            imetricStorage.storageMetric(metric1);
            imetricStorage.storageMetric(metric2);

        } catch (Exception e) {

            logger.error(e.getMessage());
        } finally {
            this.dbUtil.closeResult();
        }
        logger.info("appid:" + this.yarnAppID);
        logger.info(Thread.currentThread().getName() + "end....");

    }

}
