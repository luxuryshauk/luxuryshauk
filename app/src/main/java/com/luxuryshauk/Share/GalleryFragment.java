package com.luxuryshauk.Share;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.luxuryshauk.Utils.SquareImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import com.luxuryshauk.R;
import com.luxuryshauk.Utils.FilePaths;
import com.luxuryshauk.Utils.FileSearch;
import com.luxuryshauk.Utils.GridImageAdapter;
import com.squareup.picasso.Picasso;

/**
 * Created by User on 5/28/2017.
 */

public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";


    //constants
    private static final int NUM_GRID_COLUMNS = 3;

    private ArrayList<String> images;


    //widgets
    private GridView gridView;
    private GridView selectedGridView;

    private ImageView galleryImage;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;

    public ArrayList<ImageView> s;

    //vars
    private ArrayList<String> directories;
    private String mAppend = "file://";
    private String mSelectedImage;
    private int profile_flag=0;
    private Context mContext;
    private ArrayList<Integer> position_p;
    private ArrayList<String> imgURLs;

    private File root;
    private ArrayList<File> fileList = new ArrayList<File>();
    
    private ImageButton single,multiple; 
    private int share_type=1;//1 for single 2 for multiple

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        galleryImage = (ImageView) view.findViewById(R.id.galleryImageView);
        single = (ImageButton) view.findViewById(R.id.single);
        multiple = (ImageButton) view.findViewById(R.id.multiple);
        gridView = (GridView) view.findViewById(R.id.gridView);
        selectedGridView = (GridView) view.findViewById(R.id.selected_grid);
        directorySpinner = (Spinner) view.findViewById(R.id.spinnerDirectory);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mContext = getActivity();


        mProgressBar.setVisibility(View.GONE);
        directories = new ArrayList<>();
        Log.d(TAG, "onCreateView: started.");

        images = new ArrayList<>();

        ImageView shareClose = (ImageView) view.findViewById(R.id.ivCloseShare);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the gallery fragment.");
                getActivity().finish();
            }
        });
        
        single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share_type = 1;
            }
        });

        multiple.setBackgroundColor(Color.parseColor("#D3D3D3"));
        multiple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position_p = new ArrayList<>();
                if(share_type==1) {
                    share_type = 2;
                    multiple.setBackgroundColor(Color.parseColor("#4d94ff"));
                }else{
                    images = new ArrayList<>();
                    GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, mAppend, images);
                    selectedGridView.setAdapter(adapter);
                    share_type = 1;
                    multiple.setBackgroundColor(Color.parseColor("#D3D3D3"));
                }
            }
        });
        
        TextView nextScreen = (TextView) view.findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");

                if(share_type==1){
                    Intent intent = new Intent(getActivity(), NextActivity.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("type",1);
                    startActivity(intent);
                    getActivity().finish();

                }
                else if(share_type==2 && images.size()>1)
                {

                    Intent intent = new Intent(getActivity(), NextActivity.class);
                    intent.putExtra("images", images);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("type",2);
                    startActivity(intent);
                    getActivity().finish();

                }
                else{
                    Toast.makeText(getContext(),"Please select more than 1 photo",Toast.LENGTH_SHORT).show();
                }
            }
        });

        init();

        return view;
    }



    private boolean isRootTask(){
        if(((ShareActivity)getActivity()).getTask() == 1){
            return false;
        }
        else{
            return true;
        }

    }

    private void init(){
        FilePaths filePaths = new FilePaths();
        if (FileSearch.getDirectoryPaths(filePaths.CAMERA) != null) {


            directories = FileSearch.getDirectoryPaths(filePaths.CAMERA);
            directories.add(filePaths.CAMERA);
            File f = new File(Environment.getExternalStorageDirectory().getPath());
            File[] files = f.listFiles();
            try {
                for (File inFile : files) {
                    if (inFile.isDirectory()) {
                        if(!inFile.getAbsolutePath().startsWith("/.")) {
                            directories.add(inFile.getAbsolutePath());
                        }
                    }
                }
            }catch (Exception e){}
            directories.add(filePaths.CAMERA);
            directories.add(filePaths.ROOT_DIR);
            directories.add(filePaths.PICTURES);
            try {
                directories.add(filePaths.WAI);
            }catch (Exception e){}
            try {
                directories.add(filePaths.SS1);
            }catch (Exception e){}
            try {
                directories.add(filePaths.SS2);
            }catch (Exception e){}

        }

        ArrayList<String> directoryNames = new ArrayList<>();
        for (int i = 0; i < directories.size(); i++) {
            try {
                Log.d(TAG, "init: directory: " + directories.get(i));
                int index = directories.get(i).lastIndexOf("/");
                String string = directories.get(i).substring(index);
                directoryNames.add(string);
            }catch (Exception e){}
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected: " + directories.get(position));

                //setup our image grid for the directory chosen
                setupGridView(directories.get(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void setupGridView(final String selectedDirectory){
        imgURLs = FileSearch.getFilePaths(selectedDirectory);


       if(!selectedDirectory.equals("/camera"))
        {
            Log.d(TAG, "setupGridView: directory chosen: " + selectedDirectory);

            //set the grid column width
            int gridWidth = getResources().getDisplayMetrics().widthPixels;
            int imageWidth = gridWidth / NUM_GRID_COLUMNS;
            int sgridWidth = getResources().getDisplayMetrics().widthPixels;
            int simageWidth = sgridWidth / 9;
            selectedGridView.setColumnWidth(simageWidth);
            gridView.setColumnWidth(imageWidth);
            try {
                for (String i : imgURLs) {
                    if (!(i.endsWith(".png")
                            || i.endsWith(".jpg")
                            || i.endsWith(".jpeg"))) {
                        setImage(imgURLs.get(0), galleryImage, mAppend);
                        Log.d(TAG, "setupGridView: found an image name exception");
                        imgURLs.remove(i);
                    }
                }
            }catch (Exception e){}
            if(imgURLs.isEmpty())
            {
                setImage("", galleryImage, mAppend);
                Toast.makeText(getContext(),"Directory has no images",Toast.LENGTH_SHORT).show();
            }

            gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
            Collections.sort(imgURLs, Collections.reverseOrder());
            Log.d(TAG, "setupGridView: images path = " + imgURLs);

            //use the grid adapter to adapter the images to gridview
            GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, mAppend, imgURLs);
            gridView.setAdapter(adapter);
            //selected.setVisibility(View.GONE);
            //set the first image to be displayed when the activity fragment view is inflated
            try {
                if (imgURLs.get(0) != null) {
                    setImage(imgURLs.get(0), galleryImage, mAppend);
                    mSelectedImage = imgURLs.get(0);
                } else {
                }
            } catch (Exception e) {
                Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " + e.getMessage());
            }
        }
        else
        {
//            root = new File(Environment.getExternalStorageDirectory()
//                    .getAbsolutePath());
//
//            ArrayList<File> f = getfile(root);
//            //Collections.sort(f);
//            Collections.sort(f, new Comparator<File>(){
//                public int compare(File f1, File f2)
//                {
//                    return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
//                } });
//
//            Log.d(TAG, "setupGridView: file paths = "+f);
//            ArrayList<String> paths = new ArrayList<>();
//
//            for(int i=0;i<f.size();i++)
//            {
//                paths.add(f.get(i).getAbsolutePath());
//            }
//            imgURLs = paths;
//
//            Log.d(TAG, "setupGridView: All directory selected");
//
//            Log.d(TAG, "setupGridView: directory chosen: " + selectedDirectory);
//
//            //set the grid column width
//            int gridWidth = getResources().getDisplayMetrics().widthPixels;
//            int imageWidth = gridWidth / NUM_GRID_COLUMNS;
//            gridView.setColumnWidth(imageWidth);
//            gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
//
//
//
//            //use the grid adapter to adapter the images to gridview
//            GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, mAppend, paths);
//
//            gridView.setAdapter(adapter);
//            //selected.setVisibility(View.GONE);
//            //set the first image to be displayed when the activity fragment view is inflated
//            try {
//                if (imgURLs.get(0) != null) {
//                    setImage(imgURLs.get(0), galleryImage, mAppend);
//                    mSelectedImage = imgURLs.get(0);
//                } else {
//                }
//            } catch (Exception e) {
//                Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException: " + e.getMessage());
//            }

        }



        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //try {

                    if (share_type == 1) {
                        images = new ArrayList<>();
                        Log.d(TAG, "onItemClick: selected an image: " + imgURLs.get(position));
                        setImage(imgURLs.get(position), galleryImage, mAppend);
                        mSelectedImage = imgURLs.get(position);
                        ImageView selected2 = view.findViewById(R.id.selected);
                        selected2.setVisibility(View.GONE);
                    } else if (share_type == 2) {
                        if(images.size()<9) {
                            SquareImageView squareImageView = view.findViewById(R.id.gridImageView);
                            squareImageView.setScaleType(ImageView.ScaleType.CENTER);
                            ImageView selected1 = view.findViewById(R.id.selected);
                            selected1.setVisibility(View.GONE);
                            position_p.add(position);
                            Log.d(TAG, "onItemClick: position list = " + position_p);
                            //Issue_instaasell : Selected wala symbol kisi aur photo pe dikha raha h
                            Log.d(TAG, "onItemClick: Multiple photos upload selected");

                            Log.d(TAG, "onItemClick: position = " + position + " id = " + id + " ImageURL  = " + imgURLs.get(position));
                            if (images.contains(imgURLs.get(position))) {
                                //Picasso.get().load("file:///" + imgURLs.get(position)).resize(350, 350).centerCrop().into(squareImageView);
                                images.remove(images.indexOf(imgURLs.get(position)));
                                //imgURLs.remove(images.get(position));
                                //selected1.setVisibility(View.GONE);
                            } else {
                                images.add(imgURLs.get(position));
                                //Picasso.get().load("file:///" + imgURLs.get(position)).resize(300, 300).centerCrop().into(squareImageView);

                                //selected1.setVisibility(View.VISIBLE);
                            }
                            GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, mAppend, images);
                            selectedGridView.setAdapter(adapter);
                            Log.d(TAG, "onItemClick: All images ------new list-----");
                            for (String a : images) {
                                Log.d(TAG, "onItemClick: All Images URL : " + a);
                            }
                            setImage(imgURLs.get(position), galleryImage, mAppend);
                        }else if(images.contains(imgURLs.get(position))){
                            SquareImageView squareImageView = view.findViewById(R.id.gridImageView);
                            Picasso.get().load("file:///" + imgURLs.get(position)).resize(350, 350).centerCrop().into(squareImageView);
                            images.remove(images.indexOf(imgURLs.get(position)));                        }
                        else{
                            Toast.makeText(getContext(),"You can select upto 9 images",Toast.LENGTH_SHORT).show();
                        }
                    }
                //}catch (Exception e){
                 //   Toast.makeText(getContext(),"Unsupported image type eg. JPEG, JPG and PNG", Toast.LENGTH_LONG).show();
                //}
            }

        });

    }

    public ArrayList<File> getfile(File dir) {
        File listFile[] = dir.listFiles();
        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].isDirectory()) {
                    fileList.add(listFile[i]);
                    getfile(listFile[i]);

                } else {
                    if (listFile[i].getName().endsWith(".png")
                            || listFile[i].getName().endsWith(".jpg")
                            || listFile[i].getName().endsWith(".jpeg")
                    )
                    {
                        fileList.add(listFile[i]);
                    }
                }

            }
        }
        return fileList;
    }




    private void setImage(String imgURL, ImageView image, String append){
        Log.d(TAG, "setImage: setting image");
        Log.d(TAG, "setImage: image full path = " + append + imgURL);

        Picasso.get().load(append +"/"+ imgURL).into(image);


//        ImageLoader imageLoader = ImageLoader.getInstance();
//
//        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String imageUri, View view) {
//                mProgressBar.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//                mProgressBar.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                mProgressBar.setVisibility(View.INVISIBLE);
//            }
//
//            @Override
//            public void onLoadingCancelled(String imageUri, View view) {
//                mProgressBar.setVisibility(View.INVISIBLE);
//            }
//        });
    }
}