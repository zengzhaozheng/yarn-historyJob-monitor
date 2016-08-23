package com.kugou.entity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zhaozhengzeng on 2016/8/3.
 */
public class HistoryInfo {
    private HashMap<String, String> file_system_conters;
    private String jobID;
    private String sqlString;
    private String hadoopSumbitUser;
    private boolean isComeFromKWS = false;



    public HistoryInfo(HashMap<String, String> file_system_conters, String jobID, String sqlString, String hadoopSumbitUser, boolean isComeFromKWS) {
        this.file_system_conters = file_system_conters;
        this.jobID = jobID;
        this.sqlString = sqlString;
        this.hadoopSumbitUser = hadoopSumbitUser;
        this.isComeFromKWS = isComeFromKWS;
    }

    public HashMap<String, String> getFile_system_conters() {
        return file_system_conters;
    }

    public String getSumbitUser() {
        return hadoopSumbitUser;
    }

    public void setSumbitUser(String sumbitUser) {
        this.hadoopSumbitUser = sumbitUser;
    }

    public String getJobID() {
        return jobID;
    }

    public void setJobID(String jobID) {
        this.jobID = jobID;
    }


}
