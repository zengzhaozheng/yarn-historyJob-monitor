package com.kugou.main;

import com.alibaba.fastjson.JSONArray;
import com.kugou.services.WorkerThread;
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

        // �û���ȡ�ļ����̳߳أ���ʵ����һ���̣߳���Ҫ����������Ķ���
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
        String arch = "MAPREDUCE";
        JSONArray jsonArray = ServiceUtil.getApplicationIDsByDay(metricDate, arch);
        for (Object e : jsonArray) {
            try {
                JSONArray item = (JSONArray) e;
                String yarnAppID = (String) item.get(0);
                String yarnSubmitUser = (String) item.get(1);
                String yarnQueue = (String) item.get(2);
                long yarnJobStartTimeStamp = item.getLong(3);
                String sumbitDay = item.getString(4);
                WorkerThread workerThread = new WorkerThread(yarnAppID, yarnSubmitUser, yarnQueue, arch,yarnJobStartTimeStamp,sumbitDay);
                mainWorker.workPool.execute(workerThread);
                System.out.println("Thead Pool active thread num:" + mainWorker.workPool.getActiveCount());
            } catch (Exception ee) {
                System.out.println(ee);
            }
        }

        mainWorker.workPool.shutdown();// �ر��̳߳�
        //�ر��̳߳أ���������ǰ�߳�ֱ���̳߳�����߳�ִ����ϻ�������һ��Сʱ
        try {
            boolean loop = true;
            do {    //ÿ10����һ���Ƿ���������ִ�����
                loop = !mainWorker.workPool.awaitTermination(10, TimeUnit.SECONDS);
            } while (loop);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
        }


    }


}
