package com.luxuryshauk.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by User on 7/29/2017.
 */

public class MPhoto implements Parcelable {

    private String caption;
    private String date_created;
    private List<String> image_path;
    private String photo_id;
    private String user_id;
    private String tags;
    private List<Like> likes;
    private List<Comment> comments;
    private Float price;
    private int type;


    public MPhoto() {

    }

    public MPhoto(String caption, String date_created, List<String> image_path, String photo_id,
                  String user_id, String tags, List<Like> likes, List<Comment> comments, Float price, int type) {
        this.caption = caption;
        this.date_created = date_created;
        this.image_path = image_path;
        this.photo_id = photo_id;
        this.user_id = user_id;
        this.tags = tags;
        this.likes = likes;
        this.comments = comments;
        this.price = price;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    protected MPhoto(Parcel in) {
        caption = in.readString();
        date_created = in.readString();
        //image_path = in.readStringList(String);
        photo_id = in.readString();
        user_id = in.readString();
        tags = in.readString();
        price = in.readFloat();
        type = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(caption);
        dest.writeString(date_created);
        dest.writeList(image_path);
        dest.writeString(photo_id);
        dest.writeString(user_id);
        dest.writeString(tags);
        try {
            dest.writeFloat(price);
        }catch (NullPointerException e)
        {

        }
        dest.writeInt(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MPhoto> CREATOR = new Creator<MPhoto>() {
        @Override
        public MPhoto createFromParcel(Parcel in) {
            return new MPhoto(in);
        }

        @Override
        public MPhoto[] newArray(int size) {
            return new MPhoto[size];
        }
    };

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public static Creator<MPhoto> getCREATOR() {
        return CREATOR;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getDate_created() {
        return date_created;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public List<String> getImage_path() {
        return image_path;
    }

    public void setImage_path(List<String> image_path) {
        this.image_path = image_path;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public void setPrice(Float price)
    {
        this.price = price;
    }

    public float getPrice()
    {
        return price;
    }

    @Override
    public String toString() {
        return "Photo{" +
                "caption='" + caption + '\'' +
                ", date_created='" + date_created + '\'' +
                ", image_path='" + image_path + '\'' +
                ", photo_id='" + photo_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", tags='" + tags + '\'' +
                ", likes=" + likes +'\'' +
                ", price=" + price +'\'' +
                ", type=" + type +'\'' +
                '}';
    }
}
