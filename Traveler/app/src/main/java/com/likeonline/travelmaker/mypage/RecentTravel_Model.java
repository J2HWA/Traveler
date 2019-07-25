package com.likeonline.travelmaker.mypage;

public class RecentTravel_Model {
    private String place_imgUrl; // 여행지의 이미지 url
    private String place_title; // 여행지의 이름
    private String place_tag; // 여행지의 태그
    private Integer place_views; // 여행지의 조회수
    private float place_totalrating; //여행지 점수
    private float place_reviewCount; //리뷰 갯수
    private String place_category; //장소 카테고리
    private String post_id; //장소 키값

    public RecentTravel_Model(){

    }
    public RecentTravel_Model(String place_imgUrl, String place_title, String place_tag, Integer place_views, float place_totalrating, float place_reviewCount, String place_category, String post_id) {
        this.place_imgUrl = place_imgUrl;
        this.place_title = place_title;
        this.place_tag = place_tag;
        this.place_views = place_views;
        this.place_totalrating = place_totalrating;
        this.place_reviewCount = place_reviewCount;
        this.place_category=place_category;
        this.post_id=post_id;
    }

    public String getPlace_imgUrl() {
        return place_imgUrl;
    }

    public void setPlace_imgUrl(String place_imgUrl) {
        this.place_imgUrl = place_imgUrl;
    }

    public String getPlace_title() {
        return place_title;
    }

    public void setPlace_title(String place_title) {
        this.place_title = place_title;
    }

    public String getPlace_tag() {
        return place_tag;
    }

    public void setPlace_tag(String place_tag) {
        this.place_tag = place_tag;
    }

    public Integer getPlace_views() {
        return place_views;
    }

    public void setPlace_views(Integer place_views) {
        this.place_views = place_views;
    }

    public float getPlace_totalrating() {
        return place_totalrating;
    }

    public void setPlace_totalrating(float place_totalrating) {
        this.place_totalrating = place_totalrating;
    }

    public float getPlace_reviewCount() {
        return place_reviewCount;
    }

    public void setPlace_reviewCount(float place_reviewCount) {
        this.place_reviewCount = place_reviewCount;
    }

    public String getPlace_category() {
        return place_category;
    }

    public void setPlace_category(String place_category) {
        this.place_category = place_category;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }
}
