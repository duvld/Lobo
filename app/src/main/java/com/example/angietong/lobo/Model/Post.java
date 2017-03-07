package com.example.angietong.lobo.Model;

import com.backendless.geo.GeoPoint;

public class Post {

    private String imageTitle;
    private String imageURI;
    private GeoPoint loc;

    public Post(String imagePath, String title, GeoPoint g)
    {
        imageURI = imagePath;
        imageTitle = title;
        loc = g;
    }
    public Post(){}

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
