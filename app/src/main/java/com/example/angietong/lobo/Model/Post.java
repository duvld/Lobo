package com.example.angietong.lobo.Model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.backendless.geo.GeoPoint;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Post {

    private String imageTitle;
    private String imageURI;
    private GeoPoint loc;
    private Bitmap imageContent;

    public Post(String imagePath, String title, GeoPoint g)
    {
        imageURI = imagePath;
        imageTitle = title;
        loc = g;
    }
    public Post(){}

    public Bitmap getImage() throws InterruptedException {

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

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getImageTitle() {
        return imageTitle;
    }

    public void setImageTitle(String imageTitle) {
        this.imageTitle = imageTitle;
    }

    public GeoPoint getLoc() {
        return loc;
    }

    public void setLoc(GeoPoint loc) {
        this.loc = loc;
    }

}
