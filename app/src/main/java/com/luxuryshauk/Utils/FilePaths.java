package com.luxuryshauk.Utils;

import android.os.Environment;

/**
 * Created by User on 7/24/2017.
 */

public class FilePaths {

    //"storage/emulated/0"
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String PICTURES = ROOT_DIR + "/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/camera";
    public String STORIES = ROOT_DIR + "/Stories";

    public String FIREBASE_STORY_STORAGE = "stories/users";
    public String FIREBASE_IMAGE_STORAGE = "photos/users/";

    public String WAI = ROOT_DIR + "/WhatsApp/Media/WhatsApp Images";
    public String SS1 = ROOT_DIR + "/DCIM/Screenshots";

    public String SS2 = ROOT_DIR + "/Pictures/Screenshots";
    public String all = "/All";
    //public String FIREBASE_IMAGE_STORAGE = "photos/users/";



}
