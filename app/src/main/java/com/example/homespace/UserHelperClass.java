package com.example.homespace;

public class UserHelperClass {

    String username,email,id,password,phoneNum,imgURL;

    public UserHelperClass(){}

    public UserHelperClass(String imgURL) {
        this.imgURL = imgURL;
    }

    public UserHelperClass(String email, String id)
    {
        this.id =id;
        this.email=email;
    }

    public UserHelperClass(String username, String email, String id){
        this.username = username;
        this.email = email;
        this.id = id;
    }

    public UserHelperClass(String username, String email, String password, String phoneNum) {
        this.username = username;
        this.email=email;
        this.password = password;
        this.phoneNum = phoneNum;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
