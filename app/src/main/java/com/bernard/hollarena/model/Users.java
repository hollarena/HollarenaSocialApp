package com.bernard.hollarena.model;


public class Users {
    String name;
    boolean isPremium;
    String thumb_image;

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public Users() {
    }

    public Users(String name,boolean isPremium,String thumb_image) {

        this.name = name;
        this.isPremium = isPremium;
        this.thumb_image = thumb_image;
    }

    public  String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }
}
