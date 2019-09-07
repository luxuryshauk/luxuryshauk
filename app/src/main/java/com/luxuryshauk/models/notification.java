package com.luxuryshauk.models;

import java.io.Serializable;

public class notification implements Serializable {

    public String type;
    public String by_user;
    public String seen;
    public String time;
    public String not_id;
    public String target;

    public notification(String type, String by_user, String seen, String time, String not_id, String target) {
        this.type = type;
        this.by_user = by_user;
        this.seen = seen;
        this.time = time;
        this.not_id = not_id;
        this.target = target;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public notification()
    {}

    public String getNot_id() {
        return not_id;
    }

    public void setNot_id(String not_id) {
        this.not_id = not_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBy_user() {
        return by_user;
    }

    public void setBy_user(String by_user) {
        this.by_user = by_user;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
