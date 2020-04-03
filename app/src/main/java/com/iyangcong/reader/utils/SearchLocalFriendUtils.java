package com.iyangcong.reader.utils;

import com.iyangcong.reader.bean.DiscoverCircleFriends;
import com.orhanobut.logger.Logger;

import java.util.List;

import static com.iyangcong.reader.utils.NotNullUtils.isNull;

/**
 * Created by WuZepeng on 2017-06-24.
 * 本地搜索好友
 */

public class SearchLocalFriendUtils {

	public static List<DiscoverCircleFriends> getSearchedList(String inputString,List<DiscoverCircleFriends> inputList){
		if(inputString != null && !inputString.trim().equals("")){
			String[] strTmp = inputString.split("");
			for(String tmp:strTmp){
				Logger.e("wzp split:" + tmp);
				if(tmp == null || tmp.equals("")){
					continue;
				}
				for(DiscoverCircleFriends friend:inputList){
					if(friend.isChecked() || (friend.getUserName()!= null &&friend.getUserName().contains(tmp))){
						friend.setVisibile(true);
					}else{
						friend.setVisibile(false);
					}
				}
			}
		}
		return inputList;
	}
}
