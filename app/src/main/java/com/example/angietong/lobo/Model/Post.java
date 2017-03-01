package com.example.angietong.lobo.Model;

public class Post {

    private String imageTitle;
    private String imageURI;

    public Post(String imagePath, String title)
    {
        imageURI = imagePath;
        imageTitle = title;
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

}
