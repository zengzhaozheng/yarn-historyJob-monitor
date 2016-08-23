package com.kugou.opentsdb;

import com.kugou.services.ImetricStorage;
import com.kugou.util.Config;
import com.kugou.util.Contants;
import org.apache.commons.configuration.CompositeConfiguration;

import java.util.*;

/**
 * Created by zhaozhengzeng on 2015/10/30.
 */
public class TsdbSender implements ImetricStorage <Set<OpenTsdbMetric>>{
    CompositeConfiguration configPropertiy = Config.getConfig();

    public static void main(String[] args) {

        //metricName|clusterPlat|kwsUserSumbitUser|hadoopSumbitUser|yarnJobID|kwsJobName|kwsJobGroupName|kwsJobParentGroupName|containerType(mr or spark)|
        Set<OpenTsdbMetric> metrics = new HashSet();
        Map<String, String> tagSet1 = new HashMap<String, String>();
        //此标志位上报点1
        tagSet1.put(Contants.CLUSTER_PLAT, "hadoop2");
        //tagSet1.put(Contants.KWS_USER_SUMBIT_NAME,)

        //topic名称为topci.gamge.info,10000为每天该topic上报条数,注意new Date().getTime()要改为当天整点时间戳
        OpenTsdbMetric m1 = new OpenTsdbMetric("topic.gamge.info3", new Date().getTime() / 1000, Integer.parseInt(args[0]), tagSet1);
        metrics.add(m1);
        TsdbSender mainTsdbSendWorker = new TsdbSender();
        mainTsdbSendWorker.storageMetric(metrics);
    }

    public void storageMetric(Set<OpenTsdbMetric> metrics) {
        OpenTsdb tsdb = OpenTsdb.forService(configPropertiy.getString("openTsdbHost"), configPropertiy.getInt("openTsdbPort")).withBatchSizeLimit(configPropertiy.getInt("batchSize")).create();
        tsdb.send(metrics);
    }
}
