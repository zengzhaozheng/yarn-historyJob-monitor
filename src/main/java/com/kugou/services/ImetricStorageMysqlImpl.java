package com.kugou.services;

import com.kugou.entity.Metric;
import com.kugou.util.Config;
import com.kugou.util.DBUtil;
import org.apache.commons.configuration.CompositeConfiguration;

/**
 * Created by zhaozhengzeng on 2016/8/19.
 */
public class ImetricStorageMysqlImpl implements ImetricStorage<Metric> {
    CompositeConfiguration configPropertiy = Config.getConfig();
    DBUtil dbUtil;

    public ImetricStorageMysqlImpl() {
        // this.dbUtil = new DBUtil();
    }

    public void storageMetric(Metric metric) {
        this.dbUtil = new DBUtil();
        this.dbUtil.insertClusterPerformance(configPropertiy.getString("kwsDbPoolName"), metric);
    }
}
