package com.common.app.entity;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by fangzhu on 2015/8/6.
 */
public class UserInfo implements Serializable{

    /*本地数据库的id  orm中自增*/
    @DatabaseField(generatedId = true)
    private long localId;
    @DatabaseField
    private String uid;
    @DatabaseField
    private String userName;
    @DatabaseField
    private String nickName;
    @DatabaseField
    private String sex;
    @DatabaseField
    private String birthDay;
    @DatabaseField
    private String mobilePhone;
    @DatabaseField
    private String password;
    @DatabaseField
    private String email;
    /*用户角色权限*/
    @DatabaseField
    private String userLevel;

    public UserInfo() {
        // needed by ormlite
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }
}
