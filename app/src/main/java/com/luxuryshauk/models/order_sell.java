package com.luxuryshauk.models;

import java.io.Serializable;

public class order_sell implements Serializable {

    public String buyer;
    public String buyer_id;
    public String buyer_email;
    public int fromshare;
    public int status;
    public int lock;
    public String caption;
    public String city;
    public String flat;
    public String imgurl;
    public String name;
    public String order_no;
    public String order_id;
    public String phone;
    public String pin;
    public String price;
    public String state;
    public String street;
    public String date;
    public String isseen;
    public String extra;

    public order_sell() {
    }

    public order_sell(String buyer, String buyer_id, String buyer_email, int fromshare, int status, int lock, String caption, String city, String flat, String imgurl, String name, String order_no, String order_id, String phone, String pin, String price, String state, String street, String date, String isseen, String extra) {
        this.buyer = buyer;
        this.buyer_id = buyer_id;
        this.buyer_email = buyer_email;
        this.fromshare = fromshare;
        this.status = status;
        this.lock = lock;
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
        this.state = state;
        this.street = street;
        this.date = date;
        this.isseen = isseen;
        this.extra = extra;
    }

    public String getBuyer() {
        return buyer;
    }

    public void setBuyer(String buyer) {
        this.buyer = buyer;
    }

    public String getBuyer_id() {
        return buyer_id;
    }

    public void setBuyer_id(String buyer_id) {
        this.buyer_id = buyer_id;
    }

    public String getBuyer_email() {
        return buyer_email;
    }

    public void setBuyer_email(String buyer_email) {
        this.buyer_email = buyer_email;
    }

    public int getFromshare() {
        return fromshare;
    }

    public void setFromshare(int fromshare) {
        this.fromshare = fromshare;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getLock() {
        return lock;
    }

    public void setLock(int lock) {
        this.lock = lock;
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

    public String getIsseen() {
        return isseen;
    }

    public void setIsseen(String isseen) {
        this.isseen = isseen;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
