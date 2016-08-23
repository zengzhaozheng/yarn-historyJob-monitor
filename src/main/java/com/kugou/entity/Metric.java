package com.kugou.entity;

/**
 * Created by zhaozhengzeng on 2016/8/8.
 */
public class Metric {
    //metricName|clusterPlat|kwsUserSumbitUser|hadoopSumbitUser|yarnJobID|kwsJobName|kwsJobGroupName|kwsJobParentGroupName|yarnQueue|containerType(mr or spark)|isComeFromKWS

    //Total megabyte-seconds taken by all map tasks
    //Total megabyte-seconds taken by all reduce tasks
    //Total vcore-seconds taken by all map tasks
    //Total vcore-seconds taken by all reduce tasks

    //内存总分数 5.89*1024*1024*1024*1024*24*60*60*1000
    //cpu总分数 2030 * 24 * 60 * 60 *1000


    private String metricName;
    private String clusterPlat;
    private String kwsUserSumbitUser;
    private String hadoopSumbitUser;
    private String yarnJobID;
    private String kwsJobName;
    private String kwsJobGroupName;
    private String kwsJobParentGroupName;
    private String yarnQueue;
    private String containerType;
    private int isComeFromKWS;
    private long yarnJobStartTimeStamp;
    private double value;
    private String dt;

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public long getYarnJobStartTimeStamp() {
        return yarnJobStartTimeStamp;
    }

    public void setYarnJobStartTimeStamp(long yarnJobStartTimeStamp) {
        this.yarnJobStartTimeStamp = yarnJobStartTimeStamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public int getIsComeFromKWS() {
        return isComeFromKWS;
    }

    public void setIsComeFromKWS(int isComeFromKWS) {
        this.isComeFromKWS = isComeFromKWS;
    }

    public String getYarnQueue() {
        return yarnQueue;
    }

    public void setYarnQueue(String yarnQueue) {
        this.yarnQueue = yarnQueue;
    }

    public String getClusterPlat() {
        return clusterPlat;
    }

    public void setClusterPlat(String clusterPlat) {
        this.clusterPlat = clusterPlat;
    }

    public String getContainerType() {
        return containerType;
    }

    public void setContainerType(String containerType) {
        this.containerType = containerType;
    }

    public String getHadoopSumbitUser() {
        return hadoopSumbitUser;
    }

    public void setHadoopSumbitUser(String hadoopSumbitUser) {
        this.hadoopSumbitUser = hadoopSumbitUser;
    }

    public String getKwsJobGroupName() {
        return kwsJobGroupName;
    }

    public void setKwsJobGroupName(String kwsJobGroupName) {
        this.kwsJobGroupName = kwsJobGroupName;
    }

    public String getKwsJobName() {
        return kwsJobName;
    }

    public void setKwsJobName(String kwsJobName) {
        this.kwsJobName = kwsJobName;
    }

    public String getKwsJobParentGroupName() {
        return kwsJobParentGroupName;
    }

    public void setKwsJobParentGroupName(String kwsJobParentGroupName) {
        this.kwsJobParentGroupName = kwsJobParentGroupName;
    }

    public String getKwsUserSumbitUser() {
        return kwsUserSumbitUser;
    }

    public void setKwsUserSumbitUser(String kwsUserSumbitUser) {
        this.kwsUserSumbitUser = kwsUserSumbitUser;
    }

    public String getMetricName() {
        return metricName;
    }

    public void setMetricName(String metricName) {
        this.metricName = metricName;
    }

    public String getYarnJobID() {
        return yarnJobID;
    }

    public void setYarnJobID(String yarnJobID) {
        this.yarnJobID = yarnJobID;
    }


}
