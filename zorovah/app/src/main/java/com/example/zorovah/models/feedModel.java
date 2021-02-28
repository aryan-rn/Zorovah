package com.example.zorovah.models;

public class feedModel {
    String userprofile,user,postfeed,likescnt,postdate,caption;
public feedModel( String userprofile,String user,String postfeed,String likescnt,String postdate,String caption){
    this.userprofile=userprofile;
    this.user=user;
    this.postfeed=postfeed;
    this.likescnt=likescnt;
    this.postdate=postdate;
    this.caption=caption;
}
    public String getUserprofile(){
        return userprofile;
    }
    public void setUserprofile(String userprofile){
        this.userprofile=userprofile;
    }
    public String getUser(){
        return user;
    }
    public void setUser(String user){
        this.user=user;
    }
    public String getPostfeed(){
        return postfeed;
    }
    public void setPostfeed(String postfeed){
        this.postfeed=postfeed;
    }
    public String getLikescnt(){
        return likescnt;
    }
    public void setLikescnt(String likescnt){
        this.likescnt=likescnt;
    }
    public String getPostdate(){
        return postdate;
    }
    public void setPostdate(String postdate){
        this.postdate=postdate;
    }
    public String getCaption(){
        return caption;
    }
    public void setCaption(String caption){
        this.caption=caption;
    }


}
