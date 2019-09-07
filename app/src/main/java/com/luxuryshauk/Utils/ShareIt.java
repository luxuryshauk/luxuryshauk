package com.luxuryshauk.Utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;

public class ShareIt {

    ArrayList<Uri> files;
    boolean[] done;
    int i=0;
    Drawable[] d;
    Bitmap[] b;
    String[] path;
    Uri[] uri;
    String TAG = "Shareit";
    int enough=0;
    boolean sharing = false;
    int index= 0;

    public void shareit(final String[] imgurl, final ImageView[] holders, final Context mContext, final long price, final String caption, final String photoid){
        files = new ArrayList<>();
        done = new boolean[imgurl.length];
        Log.d(TAG, "shareit: image count = " + imgurl.length );
        Log.d(TAG, "shareit: image loaded  = ");
        index = 0;
        for(int p =0;p<imgurl.length;p++)
        {
            if(imgurl[p].equals("null"))
            {
                imgurl[p] = "empty";
            }
        }
        for(String url : imgurl)
        {
            Log.d(TAG, "shareit: image url = " + url);

        }
        d = new Drawable[10];
        b = new Bitmap[10];
        final ProgressDialog dialog = ProgressDialog.show(mContext, "",
                "Preparing images to share. Please wait...", true);
        path = new String[imgurl.length];
        uri = new Uri[imgurl.length];


        Arrays.fill(done, false);
        if(imgurl.length>1) {
            imgurl[imgurl.length - 1] = imgurl[imgurl.length - 1].replace("]", "");
            Log.d(TAG, "shareit: image loaded imgurl[imgurl.length-1] = " + imgurl[imgurl.length-1]);

        }


//        Picasso.get().load(imgurl[imgurl.length-1]).into(holders[imgurl.length-1], new Callback() {
//            @Override
//            public void onSuccess() {
//
//                d[imgurl.length - 1] = holders[imgurl.length - 1].getDrawable();
//                b[imgurl.length - 1] = ((BitmapDrawable) d[imgurl.length - 1]).getBitmap();
//                path[imgurl.length - 1] = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), b[imgurl.length - 1], "Image Description", null);
//                uri[imgurl.length - 1] = Uri.parse(path[imgurl.length - 1]);
//                files.add(uri[imgurl.length - 1]);
//                done[imgurl.length - 1] = true;
//                Log.d(TAG, "shareit length : image loaded  = ");
//
//            }
//
//            @Override
//            public void onError(Exception e) {
//                Log.d(TAG, "shareit length : image loaded failed= " +e.getMessage() );
//            }
//        });


        if(imgurl.length>0) {
            Picasso.get().load(imgurl[0]).into(holders[0], new Callback() {
                @Override
                public void onSuccess() {

                        d[0] = holders[0].getDrawable();
                        b[0] = ((BitmapDrawable) d[0]).getBitmap();
                        path[0] = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), b[0], "Image Description", null);
                        uri[0] = Uri.parse(path[0]);
                        files.add(uri[0]);
                        done[0] = true;
                    Log.d(TAG, "shareit0: image loaded  = ");

                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
        if(imgurl.length>1) {
            Picasso.get().load(imgurl[1]).into(holders[1], new Callback() {
                @Override
                public void onSuccess() {

                        d[1] = holders[1].getDrawable();
                        b[1] = ((BitmapDrawable) d[1]).getBitmap();
                        path[1] = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), b[1], "Image Description", null);
                        uri[1] = Uri.parse(path[1]);
                        files.add(uri[1]);
                        done[1] = true;
                    Log.d(TAG, "shareit1: image loaded  = ");

                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
        if(imgurl.length>2) {
            Picasso.get().load(imgurl[2]).into(holders[2], new Callback() {
                @Override
                public void onSuccess() {

                        d[2] = holders[2].getDrawable();
                        b[2] = ((BitmapDrawable) d[2]).getBitmap();
                        path[2] = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), b[2], "Image Description", null);
                        uri[2] = Uri.parse(path[2]);
                        files.add(uri[2]);
                        done[2] = true;
                    Log.d(TAG, "shareit2: image loaded  = ");

                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
        if(imgurl.length>3) {
            Picasso.get().load(imgurl[3]).into(holders[3], new Callback() {
                @Override
                public void onSuccess() {

                        d[3] = holders[3].getDrawable();
                        b[3] = ((BitmapDrawable) d[3]).getBitmap();
                        path[3] = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), b[3], "Image Description", null);
                        uri[3] = Uri.parse(path[3]);
                        files.add(uri[3]);
                        done[3] = true;

                    Log.d(TAG, "shareit3: image loaded  = ");
                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
        if(imgurl.length>4) {
            Picasso.get().load(imgurl[4]).into(holders[4], new Callback() {
                @Override
                public void onSuccess() {

                        d[4] = holders[4].getDrawable();
                        b[4] = ((BitmapDrawable) d[4]).getBitmap();
                        path[4] = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), b[4], "Image Description", null);
                        uri[4] = Uri.parse(path[4]);
                        files.add(uri[4]);
                        done[4] = true;
                    Log.d(TAG, "shareit4: image loaded  = ");

                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
        if(imgurl.length>5) {
            Picasso.get().load(imgurl[5]).into(holders[5], new Callback() {
                @Override
                public void onSuccess() {

                        d[5] = holders[5].getDrawable();
                        b[5] = ((BitmapDrawable) d[5]).getBitmap();
                        path[5] = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), b[5], "Image Description", null);
                        uri[5] = Uri.parse(path[5]);
                        files.add(uri[5]);
                        done[5] = true;
                    Log.d(TAG, "shareit5: image loaded  = ");

                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
        if(imgurl.length>6) {
            Picasso.get().load(imgurl[6]).into(holders[6], new Callback() {
                @Override
                public void onSuccess() {

                        d[6] = holders[6].getDrawable();
                        b[6] = ((BitmapDrawable) d[6]).getBitmap();
                        path[6] = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), b[6], "Image Description", null);
                        uri[6] = Uri.parse(path[6]);
                        files.add(uri[6]);
                        done[6] = true;
                    Log.d(TAG, "shareit6: image loaded  = ");

                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
        if(imgurl.length>7) {
            Picasso.get().load(imgurl[7]).into(holders[7], new Callback() {
                @Override
                public void onSuccess() {

                        d[7] = holders[7].getDrawable();
                        b[7] = ((BitmapDrawable) d[7]).getBitmap();
                        path[7] = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), b[7], "Image Description", null);
                        uri[7] = Uri.parse(path[7]);
                        files.add(uri[7]);
                        done[7] = true;
                    Log.d(TAG, "shareit7: image loaded  = ");

                }

                @Override
                public void onError(Exception e) {

                }
            });
        }
        if(imgurl.length>8) {
            Picasso.get().load(imgurl[8]).into(holders[8], new Callback() {
                @Override
                public void onSuccess() {

                        d[8] = holders[8].getDrawable();
                        b[8] = ((BitmapDrawable) d[8]).getBitmap();
                        path[8] = MediaStore.Images.Media.insertImage(mContext.getContentResolver(), b[8], "Image Description", null);
                        uri[8] = Uri.parse(path[8]);
                        files.add(uri[8]);
                        done[8] = true;
                    Log.d(TAG, "shareit7: image loaded  = ");

                }

                @Override
                public void onError(Exception e) {

                }
            });
        }

        final Activity activity = (Activity) mContext;
        Thread t = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(50);
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "run: count = Started" );
                                int count = 0;
                                for(boolean doing : done) {
                                    Log.d(TAG, "run: count = "+count + " doing" + doing );
                                    count++;
                                }

                                if (areAllTrue(done) || enough>500) {
                                    dialog.dismiss();
                                    try {
                                        if(!sharing) {
                                            Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                                            intent.setType("image/jpeg");

                                            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);

                                            intent.putExtra(Intent.EXTRA_TEXT, caption + " \nPrice : ₹ " + price +
                                                    "\nBuy this : https://collectmoney.in/app/?id=" + photoid);
                                            mContext.startActivity(intent);
                                            sharing = true;
                                        }

                                    } catch (NullPointerException e) {
                                    }
                                } else {
                                    Toast.makeText(mContext, "Loading", Toast.LENGTH_SHORT).show();
                                    enough++;

                                }
                            }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
                t.start();
    }

    public static boolean areAllTrue(boolean[] array)
    {
        for(boolean b : array) if(!b) return false;
        return true;
    }




}
