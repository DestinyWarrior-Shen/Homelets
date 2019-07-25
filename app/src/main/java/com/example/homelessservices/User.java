package com.example.homelessservices;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Eric on 2/9/17.
 */

public class User implements Serializable
{
    private ArrayList<FoodPlace> favouriteList;
    private String email;
    private ArrayList<String> commentList;
    private String userName;
    private String headIconURL;


    public User()
    {
        favouriteList = new ArrayList<>();
        commentList = new ArrayList<>();
        email = "";
        userName = "";
        headIconURL = "";
    }

    public User(String email)
    {
        this.favouriteList = new ArrayList<>();
        this.commentList = new ArrayList<>();
        this.email = email;
        this.userName = "";
        headIconURL = "";
    }

    public ArrayList<FoodPlace> getFavouriteList() {
        return favouriteList;
    }

    public void setFavouriteList(ArrayList<FoodPlace> favouriteList) {
        this.favouriteList = favouriteList;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getCommentList() {
        return commentList;
    }

    public void setCommentList(ArrayList<String> commentList) {
        this.commentList = commentList;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadIconURL() {
        return headIconURL;
    }

    public void setHeadIconURL(String headIconURL) {
        this.headIconURL = headIconURL;
    }
}
