package com.kugou.util;

/**
 * Created by zhaozhengzeng on 2016/8/4.
 */
public interface Contants {
    public static final String RESOUCEMANAGER_URL= "http://kg-nn-2:8088/cluster/apps";
    public static final String HISTORY_CONTER_PATH = "/jobhistory/jobcounters/job_";
    public static final String HISTORY_CONF_PATH = "/jobhistory/conf/job_";
    public static final String DOMAIN = "kg-nn-1";
    public static final String HISTORY_PORT = "19888";
    public static final String FILE_SYSTEM_COUNTER = "FileSystemCounter";
    public static final String JOBCOUNTER = "JobCounter";
    public static final String TASKCOUNTER = "TaskCounter";

    public static final String MEMORY_CONSUME = "mConsume";
    public static final String CPU_CONSUME="cpuConsume";

    public static final String CLUSTER_PLAT = "plat";//集群类型
    public static final String KWS_USER_SUMBIT_NAME = "kUser";//kws提交用户名
    public static final String  HADOOP_SUMBIT_USER = "hUser";//hadoop提交用户名
    public static final String KWS_JOB_NAME = "kjName";
    public static final String KWS_JOB_GROUP_NAME="kjgName";
    public static final String KWS_JOB_PARENT_GROUP_NAME = "kjpgName";
    public static final String YARN_QUEUE = "yarnQueue";
    public static final String ARC_TYPE = "cType";

    public static final String IS_COME_FROM_KWS = "isComeFromKWS";

}
