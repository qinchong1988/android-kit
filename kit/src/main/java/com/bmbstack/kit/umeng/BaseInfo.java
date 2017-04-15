package com.bmbstack.kit.umeng;


public class BaseInfo {
    public String uid;
    public String accessToken;
    public String refreshtoken;
    public String expiration;
    public String gender;
    public String iconurl;
    public String city;
    public String province;
    public String name;

    @Override
    public String toString() {
        return "BaseInfo{" +
                "accessToken='" + accessToken + '\'' +
                ", uid='" + uid + '\'' +
                ", refreshtoken='" + refreshtoken + '\'' +
                ", expiration='" + expiration + '\'' +
                ", gender='" + gender + '\'' +
                ", iconurl='" + iconurl + '\'' +
                ", city='" + city + '\'' +
                ", province='" + province + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
