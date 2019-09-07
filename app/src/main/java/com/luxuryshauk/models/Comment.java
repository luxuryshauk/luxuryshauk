package com.luxuryshauk.models;

import java.util.List;

/**
 * Created by User on 8/22/2017.
 */

public class Comment {

    private String comment;
    private String user_id;
    private List<Like> likes;
    private String date_created;
    private String comment_id;
    private String photo_id;

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public Comment() {

    }

    public Comment(String comment, String user_id, List<Like> likes, String date_created, String comment_id,String photo_id) {
        this.comment = comment;
        this.user_id = user_id;
        this.likes = likes;
        this.date_created = date_created;
        this.comment_id = comment_id;
        this.photo_id = photo_id;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "comment='" + comment + '\'' +
                ", user_id='" + user_id + '\'' +
                ", likes=" + likes +
                ", date_created='" + date_created + '\'' +
                ", comment_id='" + comment_id + '\'' +
                ", photo_id=" + photo_id +
                '}';
    }
}
