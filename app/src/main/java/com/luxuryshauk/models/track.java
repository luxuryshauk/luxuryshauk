package com.luxuryshauk.models;

public class track {

    String name;
    String no;
    String pic;
    String status;
    int o_no;

    public track()
    {


    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getO_no() {
        return o_no;
    }

    public void setO_no(int o_no) {
        this.o_no = o_no;
    }

    public track(String name, String no, String pic, String status, int o_no) {
        this.name = name;
        this.no = no;
        this.pic = pic;
        this.status = status;
        this.o_no = o_no;
    }

    @Override
    public String toString() {
        return "track{" +
                "name='" + name + '\'' +
                ", no='" + no + '\'' +
                ", pic='" + pic + '\'' +
                ", status='" + status + '\'' +
                ", o_no=" + o_no +
                '}';
    }
}
