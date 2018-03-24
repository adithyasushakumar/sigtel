package com.adhithya.track.model;

/**
 * Created by Hari Group Unity on 16-03-2018.
 */

public class User {

    int uid;
    String reg_mobile_no;
    String name;

    public User() {
    }

    public User(int uid, String reg_mobile_no, String name) {
        this.uid = uid;
        this.reg_mobile_no = reg_mobile_no;
        this.name = name;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getReg_mobile_no() {
        return reg_mobile_no;
    }

    public void setReg_mobile_no(String reg_mobile_no) {
        this.reg_mobile_no = reg_mobile_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
