package com.likeonline.travelmaker.review;

public class reviewModel {
    private String user_id;
    private String post_id;
    private String mPlace_category;
    private String mReview_id;
    private String time;
    private String title;
    private String content;
    private String user_name;
    private String user_profileUrl; // 유저 프로필 사진 Url을 저장
    private float rating; //리뷰 레이팅
    private int user_grade; //유저 등급

    public reviewModel() {}

    public reviewModel(String user_id, String post_id, String time, String title, String content, String user_name, String user_profileUrl, int user_grade, float rating, String mPlace_category, String mReview_id) {
        this.user_id = user_id;
        this.post_id = post_id;
        this.time = time;
        this.title = title;
        this.content = content;
        this.user_name = user_name;
        this.user_profileUrl = user_profileUrl;
        this.user_grade=user_grade;
        this.rating=rating;
        this.mPlace_category=mPlace_category;
        this.mReview_id=mReview_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_profileUrl() {
        return user_profileUrl;
    }

    public void setUser_profileUrl(String user_profileUrl) {
        this.user_profileUrl = user_profileUrl;
    }

    public int getUser_grade() {
        return user_grade;
    }

    public void setUser_grade(int user_grade) {
        this.user_grade = user_grade;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getmPlace_category() {
        return mPlace_category;
    }

    public void setmPlace_category(String mPlace_category) {
        this.mPlace_category = mPlace_category;
    }

    public String getmReview_id() {
        return mReview_id;
    }

    public void setmReview_id(String mReview_id) {
        this.mReview_id = mReview_id;
    }
}
