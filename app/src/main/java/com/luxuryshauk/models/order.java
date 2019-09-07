package com.luxuryshauk.models;

import java.io.Serializable;

public class order implements Serializable {

    private String caption;
    private String city;
    private String flat;
    private String imgurl;
    private String name;
    private String order_no;
    private String order_id;
    private String phone;
    private String pin;
    private String price;
    private String seller;
    private String seller_id;
    private String state;
    private String street;
    private String date;
    private int status;
    private String extra;
    private String isseen;



    public order()
    {

    }

    public order(String caption, String city, String flat, String imgurl, String name, String order_no, String order_id, String phone, String pin, String price, String seller, String seller_id, String state, String street, String date, int status, String extra, String isseen) {
        this.caption = caption;
        this.city = city;
        this.flat = flat;
        this.imgurl = imgurl;
        this.name = name;
        this.order_no = order_no;
        this.order_id = order_id;
        this.phone = phone;
        this.pin = pin;
        this.price = price;
        this.seller = seller;
        this.seller_id = seller_id;
        this.state = state;
        this.street = street;
        this.date = date;
        this.status = status;
        this.extra = extra;
        this.isseen = isseen;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getFlat() {
        return flat;
    }

    public void setFlat(String flat) {
        this.flat = flat;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public String getSeller_id() {
        return seller_id;
    }

    public void setSeller_id(String seller_id) {
        this.seller_id = seller_id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getIsseen() {
        return isseen;
    }

    public void setIsseen(String isseen) {
        this.isseen = isseen;
    }
}
