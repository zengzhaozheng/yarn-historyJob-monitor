package com.kugou.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.kugou.entity.KwsGroupInfo;
import com.kugou.entity.KwsJob;
import com.kugou.entity.Metric;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by zhaozhengzeng on 2016/8/8.
 */
public class DBUtil {

    private static final Logger log = LoggerFactory.getLogger(DBUtil.class);
    Connection conn = null;
    PreparedStatement preStmt = null;
    private ResultSet rs = null;

    /**
     * 根据作业id判断此作业是否是由kws提交的。
     *
     * @param DbAlias
     * @param jobID
     * @return
     */
    public boolean isComeFromKWS(String DbAlias, String jobID) {
        boolean b = false;
        String sql = "select count(*)  from zeus_task_history_appid t where t.app_id='application_" + jobID + "'";
        try {
            conn = DBManager.getInstance().getConnection(DbAlias);
            preStmt = conn.prepareStatement(sql);
            rs = preStmt.executeQuery();
            if (rs.next()) {
                if (rs.getInt(1) != 0) {
                    b = true;
                }
            }

        } catch (Exception e) {
            log.error("", e);
        } finally {
            this.closeResult();
        }
        return b;
    }

    /**
     * 根据yarn作业ID，获取其在kws中对应的作业信息
     *
     * @param
     * @param jobID yarn中的作业ID
     * @return
     */
    public KwsJob getKwsJobStat(String dbAlias, String jobID) {
        KwsJob kwsJob = new KwsJob();
        boolean b = false;
        String sql = "select job.name, u.name ,job.`id` " +
                "from zeus_job as job, t_user as u " +
                "where job.id in( " +
                "select job_id from zeus_job " +
                "where id in (select task_id from zeus_task_history_appid where app_id = 'application_" + jobID + "')" +
                ") and job.`owner` = u.id";
        try {
            conn = DBManager.getInstance().getConnection(dbAlias);
            preStmt = conn.prepareStatement(sql);
            rs = preStmt.executeQuery();
            if (rs.next()) {
                kwsJob.setKwsJobName(rs.getString(1));
                kwsJob.setKwsJobOwner(rs.getString(2));
                kwsJob.setKwsJobID(rs.getString(3));
            }

        } catch (Exception e) {
            log.error("", e);
        } finally {
            this.closeResult();
        }
        return kwsJob;
    }

    /**
     * 获得kws分组信息
     *
     * @param dbAllias
     * @param kwsJobID
     * @return
     */
    public KwsGroupInfo getKwsGroupInfo(String dbAllias, String kwsJobID) {
        KwsGroupInfo kwsGroupInfo = new KwsGroupInfo();
        String sql = "select name, id, parent from zeus_group " +
                "where  id in (select group_id from zeus_job where id  = '" + kwsJobID + "')";
        try {
            conn = DBManager.getInstance().getConnection(dbAllias);
            preStmt = conn.prepareStatement(sql);
            rs = preStmt.executeQuery();
            if (rs.next()) {
                kwsGroupInfo.setGroupName(rs.getString(1));
                kwsGroupInfo.setGroupID(rs.getInt(2));
                kwsGroupInfo.setParentGroupID(rs.getInt(3));
            }
        } catch (Exception e) {
            log.error("", e);
        } finally {
            this.closeResult();
        }
        return kwsGroupInfo;

    }


    /**
     * 获得kws分组名
     *
     * @param dbAlias
     * @param kwsGroupID
     * @return
     */
    public String getKwsGroupName(String dbAlias, int kwsGroupID) {
        String sql = "select name from zeus_group where id = " + kwsGroupID + "";
        String kwsGroupName = null;
        try {
            conn = DBManager.getInstance().getConnection(dbAlias);
            preStmt = conn.prepareStatement(sql);
            rs = preStmt.executeQuery();
            if (rs.next()) {
                kwsGroupName = rs.getString(1);
            }


        } catch (Exception e) {
            log.error("", e);
        } finally {
            this.closeResult();
        }
        return kwsGroupName;
    }


    public void closeResult() {
        try {
            if (preStmt != null && !preStmt.isClosed())
                try {
                    preStmt.close();
                } catch (SQLException e) {
                    log.error("", e);
                }
            if (conn != null && !conn.isClosed())
                try {
                    conn.close();
                } catch (SQLException e) {
                    log.error("", e);
                }
            if (rs != null && !rs.isClosed()) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        //ProxoolFacade.shutdown(0);
    }

    public boolean insertClusterPerformance(String dbAlias, Metric metric) {
        boolean b = true;
        String sql = "insert into metric_cluster_performance(metricName,plat,kUser,hUser,kjName,kjgName,kjpgName,yarnQueue,cType,isComeFromKWS,dt,sumbitTime,metricValue) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try {
            conn = DBManager.getInstance().getConnection(dbAlias);
            preStmt = conn.prepareStatement(sql);
            preStmt.setString(1, metric.getMetricName());
            preStmt.setString(2, metric.getClusterPlat());
            preStmt.setString(3, metric.getKwsUserSumbitUser());
            preStmt.setString(4, metric.getHadoopSumbitUser());
            preStmt.setString(5, metric.getKwsJobName());
            preStmt.setString(6, metric.getKwsJobGroupName());
            preStmt.setString(7, metric.getKwsJobParentGroupName());
            preStmt.setString(8, metric.getYarnQueue());
            preStmt.setString(9, metric.getContainerType());
            preStmt.setInt(10, metric.getIsComeFromKWS());
            preStmt.setString(11, metric.getDt());
            preStmt.setLong(12, metric.getYarnJobStartTimeStamp());
            preStmt.setDouble(13, metric.getValue());
            preStmt.executeUpdate();
        } catch (Exception e) {
            log.error(e.getMessage());
            b = false;
            e.printStackTrace();
        } finally {
            this.closeResult();
        }
        return b;
    }

    public static void main(String args[]) {
        DBUtil dbUtil = new DBUtil();
        KwsJob kwsJob = dbUtil.getKwsJobStat("kwsPool", "1466392969130_0032");

        boolean b = dbUtil.isComeFromKWS("kwsPool", "1466392969130_0032");

        KwsGroupInfo kwsGroupInfo = dbUtil.getKwsGroupInfo("kwsPool", kwsJob.getKwsJobID());

        String groupName = dbUtil.getKwsGroupName("kwsPool", kwsGroupInfo.getGroupID());

        dbUtil.closeResult();

    }

}
