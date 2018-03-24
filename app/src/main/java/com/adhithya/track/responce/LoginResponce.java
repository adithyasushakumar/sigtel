package com.adhithya.track.responce;

import com.adhithya.track.model.User;

import java.util.List;

/**
 * Created by ASUS on 3/18/2018.
 */

public class LoginResponce {
    int status;
    String message;
    List<User> user;

    public LoginResponce() {
    }

    public LoginResponce(int status, String message, List<User> user) {
        this.status = status;
        this.message = message;
        this.user = user;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<User> getUser() {
        return user;
    }

    public void setUser(List<User> user) {
        this.user = user;
    }
}
