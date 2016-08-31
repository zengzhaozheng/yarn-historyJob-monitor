package com.kugou.util;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.kugou.entity.HistoryInfo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.script.ScriptException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by zhaozhengzeng on 2016/8/4.
 */
public class ServiceUtil {
    public static String getConfItemValue(String jobID, String confItem) {
        //hive.query.string
        Document doc = null;
        String hiveSql = "";
        try {
            doc = Jsoup.connect("http://" + Contants.DOMAIN + ":" + Contants.HISTORY_PORT + Contants.HISTORY_CONF_PATH + jobID).get();
            Elements elements = doc.getElementsByTag("td");
            for (int i = 0; i < elements.size(); i++) {
                if (elements.get(i).text().trim().equals(confItem)) {
                    hiveSql = elements.get(i + 1).text();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hiveSql;
    }

    public static JSONArray getApplicationIDsByDay(String dateStr) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        Document doc = null;
        try {
            doc = Jsoup.connect(Contants.RESOUCEMANAGER_URL).get();
            Element elements = doc.getElementsByTag("script").get(6);
            String appInfo = "[" + elements.data().split("\\=\\[")[1];
            Gson gson = new Gson();

            JSONArray jsonArray = gson.fromJson(appInfo, JSONArray.class);

            for (Object e : jsonArray) {
                ArrayList<String> item = (ArrayList) e;
                String appID = item.get(0).split("\\<\\/a\\>")[0].split(">")[1].substring(12);
                String yarnSubmitUser = item.get(1);
                String calArchitectureName = item.get(3);
                String yarnQueue = item.get(4);
                long yarnJobStartTimeStamp = Long.parseLong(item.get(5));
                Date date = new Date();
                date.setTime(yarnJobStartTimeStamp);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
                String sumbitTime = simpleDateFormat.format(date);
                String sumbitDay = sumbitTime.substring(0, 8);

                if (sumbitDay.trim().equals(dateStr.trim())) {
                    sb.append("[").append("\"").append(appID).append("\"").append(",").append("\"").append(yarnSubmitUser).append("\"").append(",").append("\"").append(yarnQueue).append("\"").append(",").
                            append("\"").append(yarnJobStartTimeStamp).append("\"").append(",").append("\"").append(sumbitDay).append("\"").append(",").append("\"").append(calArchitectureName.trim()).append("\"").append("]").append(",");
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        String sss = sb.substring(0, sb.length() - 1) + "]";

        return JSONArray.parseArray(sb.substring(0, sb.length() - 1) + "]");


    }


    public static HashMap<String, String> getCounterInfo(HashMap<String, String> source, String jobID, String counterFlag, Document doc) {
        Element counterItems = doc.getElementById("job_" + jobID + ".org.apache.hadoop.mapreduce." + counterFlag);
        Elements trs = counterItems.getElementsByTag("tr");
        for (int i = 0; i < trs.size() - 1; i++) {
            Elements tds = trs.get(i + 1).getElementsByTag("td");
            String titleName = tds.get(0).text();
            String mapValue = tds.get(1).text().replace(",", "");
            String reduceValue = tds.get(2).text().replace(",", "");
            String totalValue = tds.get(3).text().replace(",", "");
            //HashMap<String, String> item = new HashMap<String, String>();
            source.put(titleName, mapValue + "|" + reduceValue + "|" + totalValue);
        }

        return source;
    }

    public static HistoryInfo getHistoryInfo(String jobID, String sumbitUserName) {
        Document doc = null;
        try {
            doc = Jsoup.connect("http://" + Contants.DOMAIN + ":" + Contants.HISTORY_PORT + Contants.HISTORY_CONTER_PATH + jobID).get();
            HashMap<String, String> rs = new HashMap<String, String>();
            HashMap<String, String> fileSystemCounter = getCounterInfo(rs, jobID, Contants.FILE_SYSTEM_COUNTER, doc);
            HashMap<String, String> fileSystemCounter_jobCounter = getCounterInfo(fileSystemCounter, jobID, Contants.JOBCOUNTER, doc);
            HashMap<String, String> fileSystemCounter_jobCounter_taskCounter = getCounterInfo(fileSystemCounter_jobCounter, jobID, Contants.TASKCOUNTER, doc);

            String queryString = ServiceUtil.getConfItemValue(jobID, "hive.query.string");
            //String sumbitUserName = ServiceUtil.getConfItemValue(jobID, "mapreduce.job.user.name");
            DBUtil dbUtil = new DBUtil();
            boolean isFromKWS = dbUtil.isComeFromKWS("kwsPool", jobID);
            dbUtil.closeResult();

            HistoryInfo historyInfo = new HistoryInfo(fileSystemCounter_jobCounter_taskCounter, jobID, queryString, sumbitUserName, isFromKWS);
            return historyInfo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;

    }

    public static void main(String args[]) throws ScriptException {
      /*  String sql = getConfItemValue("1469759210389_45956", "hive.query.string");
        String sumbitUser = getConfItemValue("1469759210389_45956","mapreduce.job.user.name");
        System.out.print(sql);*/

/*        CompositeConfiguration configPropertiy = Config.getConfig();
        String memeoryScore = configPropertiy.getString("memoryScore");
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("JavaScript");

        System.out.println(memeoryScore);
        System.out.println(engine.eval(memeoryScore));*/

        //JSONArray jsonArray = ServiceUtil.getApplicationIDsByDay("20160812", "MAPREDUCE");
        String aa = "jasdf";
        System.out.println(aa.replaceAll("[()]", ""));

    }
}
