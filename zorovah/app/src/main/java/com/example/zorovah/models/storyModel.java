package com.example.zorovah.models;

import java.util.ArrayList;
import java.util.List;

public class storyModel {
    List<String> images=new ArrayList<>();
    String name;
    public storyModel(List<String> images,String name){
        this.images=images;
        this.name=name;
    }
    public List<String> getImages(){
        return images;
    }
    public void setImages(List<String> images){
        this.images=images;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name=name;
    }
}
