package com.kugou.entity;

/**
 * Created by zhaozhengzeng on 2016/8/8.
 */
public class KwsGroupInfo {
    private String groupName;
    private int groupID;
    private int parentGroupID;

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getParentGroupID() {
        return parentGroupID;
    }

    public void setParentGroupID(int parentGroupID) {
        this.parentGroupID = parentGroupID;
    }

}
