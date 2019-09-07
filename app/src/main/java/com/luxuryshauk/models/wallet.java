package com.luxuryshauk.models;

public class wallet {

    String price;
    String imgurl;
    String date;
    String orderno;
    String type;

    void wallet(){

    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOrderno() {
        return orderno;
    }

    public void setOrderno(String orderno) {
        this.orderno = orderno;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public wallet(){}

    public wallet(String price, String imgurl, String date, String orderno, String type) {
        this.price = price;
        this.imgurl = imgurl;
        this.date = date;
        this.orderno = orderno;
        this.type = type;
    }
}
