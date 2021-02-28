package com.example.zorovah.models;

public class searchModel {
    String name,image,email,hisUid;
    public searchModel(String image,String name,String email,String hisUid){
        this.email=email;
        this.image=image;
        this.name=name;
        this.hisUid=hisUid;
    }

    public String getEmail() {
        return email;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getHisUid() {
        return hisUid;
    }

    public void setHisUid(String hisUid) {
        this.hisUid = hisUid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }
}
