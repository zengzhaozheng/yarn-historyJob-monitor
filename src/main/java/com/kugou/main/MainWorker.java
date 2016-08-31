package com.kugou.main;

import com.alibaba.fastjson.JSONArray;
import com.kugou.services.MrMetricWorkThread;
import com.kugou.util.Config;
import com.kugou.util.ServiceUtil;
import org.apache.commons.configuration.CompositeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhaozhengzeng on 2016/8/3.
 */
public class MainWorker {
    private static final Logger logger = LoggerFactory.getLogger(MainWorker.class);
    private ThreadPoolExecutor workPool;
    CompositeConfiguration configPropertiy = Config.getConfig();

    public MainWorker() {

        // 用户读取文件的线程池，其实就是一个线程，主要是利用里面的队列
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(
                5, true);
/*		int i = conf.getInt("etlTaskMaxThread");*/
        this.workPool = new ThreadPoolExecutor(configPropertiy.getInt("wordTaskMinThread", 1),
                configPropertiy.getInt("workTaskMaxThread", 1), 30, TimeUnit.SECONDS, queue,
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public static void main(String args[]) {

        MainWorker mainWorker = new MainWorker();
        String metricDate = args[0];
        JSONArray jsonArray = ServiceUtil.getApplicationIDsByDay(metricDate);
        for (Object e : jsonArray) {
            try {
                JSONArray item = (JSONArray) e;
                String yarnAppID = (String) item.get(0);
                String yarnSubmitUser = (String) item.get(1);
                String yarnQueue = (String) item.get(2);
                long yarnJobStartTimeStamp = item.getLong(3);
                String sumbitDay = item.getString(4);
                String arcType = item.getString(5);

                if (arcType.equals("MAPREDUCE")) {
                    MrMetricWorkThread mrMetricWorkThread = new MrMetricWorkThread(yarnAppID, yarnSubmitUser, yarnQueue, arcType, yarnJobStartTimeStamp, sumbitDay);
                    mainWorker.workPool.execute(mrMetricWorkThread);
                } else if (arcType.equals("SPARK")) {
                    //todo: SPARK RESOURCES COLLECT...

                }
                System.out.println("Thead Pool active thread num:" + mainWorker.workPool.getActiveCount());
            } catch (Exception ee) {
                System.out.println(ee);
            }
        }

        mainWorker.workPool.shutdown();
        try {
            boolean loop = true;
            do {    //每10秒检查一次是否所有任务执行完毕
                loop = !mainWorker.workPool.awaitTermination(10, TimeUnit.SECONDS);
            } while (loop);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }


    }


}
