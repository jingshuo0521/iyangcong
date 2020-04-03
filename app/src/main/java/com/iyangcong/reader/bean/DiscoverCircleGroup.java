package com.iyangcong.reader.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WuZepeng on 2017-03-13.
 * 用于DiscoverNewCircleInviteFriends
 */

public class DiscoverCircleGroup {
    /**
     * 好友列表
     */
    private List<DiscoverCircleFriends> friendsList = new ArrayList<>();
    /**
     * 分组Id
     */
    private int grouptagId;
    /**
     * 分组名字
     */
    private String grouptagName;

    public int getGrouptagId() {
        return grouptagId;
    }

    public void setGrouptagId(int grouptagId) {
        this.grouptagId = grouptagId;
    }

    public String getGrouptagName() {
        return grouptagName;
    }

    public void setGrouptagName(String grouptagName) {
        this.grouptagName = grouptagName;
    }

    public List<DiscoverCircleFriends> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(List<DiscoverCircleFriends> friendsList) {
        this.friendsList = friendsList;
    }
}
