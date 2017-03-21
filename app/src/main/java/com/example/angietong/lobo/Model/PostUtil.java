package com.example.angietong.lobo.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by angietong on 2017-03-20.
 */

public class PostUtil {

    private static Bitmap imageContent;

    public PostUtil(){}

    public static Bitmap getImageFromURL(final String imageURI) throws InterruptedException {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                URL url = null;
                try {
                    url = new URL(imageURI);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    imageContent = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    Log.d("THING","I GOT THIS FROM THE URK: " + imageContent);
                } catch (IOException e) {
                    Log.d("THING",e.toString());
                }
            }
        });

        t.start();
        t.join();

        return imageContent;
    }
}
