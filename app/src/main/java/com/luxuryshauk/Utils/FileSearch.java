package com.luxuryshauk.Utils;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by User on 7/24/2017.
 */

public class FileSearch {

    /**
     * Search a directory and return a list of all **directories** contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getDirectoryPaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        try {
            for (int i = 0; i < listfiles.length; i++) {
                if (listfiles[i].isDirectory()) {
                    if(listfiles[i].getAbsolutePath().endsWith(".jpg")
                            || listfiles[i].getAbsolutePath().endsWith(".jpeg")
                            || listfiles[i].getAbsolutePath().endsWith(".png")) {
                        pathArray.add(listfiles[i].getAbsolutePath());
                    }
                }
            }
        }catch (Exception e)
        {

        }
        return pathArray;
    }

    /**
     * Search a directory and return a list of all **files** contained inside
     * @param directory
     * @return
     */
    public static ArrayList<String> getFilePaths(String directory){
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        try {
            for (int i = 0; i < listfiles.length; i++) {
                if (listfiles[i].isFile()) {
                    if(listfiles[i].getAbsolutePath().endsWith(".jpg")
                            || listfiles[i].getAbsolutePath().endsWith(".jpeg")
                            || listfiles[i].getAbsolutePath().endsWith(".png")) {
                        pathArray.add(listfiles[i].getAbsolutePath());
                    }
                }
            }
        }catch (Exception e)
        {

        }

        return pathArray;
    }
}
