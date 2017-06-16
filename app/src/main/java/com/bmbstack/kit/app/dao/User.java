package com.bmbstack.kit.app.dao;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.Keep;

@Entity
public class User {
    @PrimaryKey
    private String userID;
    private String username;
    private String headPhoto;
    private int sex;
    private int height;
    private double weightStart;
    private double weightCurrent;
    private double weightTarget;
    private int pid;
    private int cid;
    private String token;

    public User() {
    }

    public String getHeadPhoto() {
        return this.headPhoto;
    }

    public void setHeadPhoto(String headPhoto) {
        this.headPhoto = headPhoto;
    }

    public int getSex() {
        return this.sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getWeightStart() {
        return this.weightStart;
    }

    public void setWeightStart(double weightStart) {
        this.weightStart = weightStart;
    }

    public double getWeightCurrent() {
        return this.weightCurrent;
    }

    public void setWeightCurrent(double weightCurrent) {
        this.weightCurrent = weightCurrent;
    }

    public double getWeightTarget() {
        return this.weightTarget;
    }

    public void setWeightTarget(double weightTarget) {
        this.weightTarget = weightTarget;
    }

    public String getUserID() {
        return this.userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getPid() {
        return this.pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getCid() {
        return this.cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    @Keep
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("User{");
        sb.append("userID='").append(userID).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append(", headPhoto='").append(headPhoto).append('\'');
        sb.append(", sex=").append(sex);
        sb.append(", height=").append(height);
        sb.append(", weightStart=").append(weightStart);
        sb.append(", weightCurrent=").append(weightCurrent);
        sb.append(", weightTarget=").append(weightTarget);
        sb.append(", pid=").append(pid);
        sb.append(", cid=").append(cid);
        sb.append('}');
        return sb.toString();
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
