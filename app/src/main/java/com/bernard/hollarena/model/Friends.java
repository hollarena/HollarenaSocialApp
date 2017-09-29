package com.bernard.hollarena.model;

public class Friends {
    String date;
    String thumb_image;

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Friends(String date, String thumb_image) {
        this.date = date;
        this.thumb_image = thumb_image;
    }

    public Friends() {
    }
}
