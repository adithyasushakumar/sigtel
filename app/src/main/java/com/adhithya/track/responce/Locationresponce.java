package com.adhithya.track.responce;

import com.adhithya.track.model.LocationModel;

import java.util.List;

/**
 * Created by ASUS on 3/18/2018.
 */

public class Locationresponce {
    int status;
    String message;
    List<LocationModel> user;

    public Locationresponce(int status, String message, List<LocationModel> user) {
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

    public List<LocationModel> getUser() {
        return user;
    }

    public void setUser(List<LocationModel> user) {
        this.user = user;
    }
}
