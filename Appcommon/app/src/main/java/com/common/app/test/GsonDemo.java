package com.common.app.test;

import com.common.app.entity.UserInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fangzhu on 2015/8/6.
 */
public class GsonDemo {

    public static void main(String[] args) {
        try {

//            CommonResponse<UserInfo> jsonDemo = new CommonResponse<UserInfo>();
//            jsonDemo.setSuccess(true);
//            UserInfo u = new UserInfo();
//            u.setNickName("nickName developer ");
//            u.setUid("10");
//            jsonDemo.setData(u);
//            String s = jsonDemo.toJson(UserInfo.class);
//            System.out.println(s);
//
//            CommonResponse<UserInfo> commonResponse = CommonResponse.fromJson(s, UserInfo.class);
//            System.out.println("nickName=" + commonResponse.getData().getNickName());


            CommonResponse<UserInfo> jsonDemo = new CommonResponse<UserInfo>();
            jsonDemo.setSuccess(true);

            List<UserInfo> list = new ArrayList<>();

           for (int i = 0; i < 10; i ++) {
               UserInfo u = new UserInfo();
               u.setNickName("nickName developer "+i);
               u.setUid("10");
               list.add(u);
           }

            jsonDemo.setData(list);
            String s = jsonDemo.toJson(UserInfo.class);
            System.out.println(s);

            CommonResponse<UserInfo> commonResponse = CommonResponse.fromJson(s, UserInfo.class);
            System.out.println(commonResponse.getData().size());
            System.out.println("nickName=" + commonResponse.getData().get(4).getNickName());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
