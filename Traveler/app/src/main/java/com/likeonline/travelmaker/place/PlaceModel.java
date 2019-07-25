package com.likeonline.travelmaker.place;

public class PlaceModel {
    private String place_id; // 여행지를 구분하기 위한 아이디
    private String place_area; // 어느지역의 여행지인지 구분
    //private String place_category; // 여행지의 어떤 카테고리인가
    private String place_imgUrl; // 여행지의 이미지 url
    private String place_title; // 여행지의 이름
    private float place_rating; // 여행지의 점수
    private String place_tag; // 여행지의 태그
    private Integer place_views; // 여행지의 조회수
    // 여기부턴 detail에서 필요한 정보들입니다.
    private String place_content; // 여행지의 설명
    private String place_address; // 여행지의 주소
    private String place_tel; // 여행지의 연락처
    private String place_price; // 여행지의 가격
    private String place_park; // 여행지의 주차가능 여부
    private double place_wedo; // 여행지의 지도 위도
    private double place_gyoungdo;// 여행지의 지도 경도
    private float place_totalrating;
    private float place_reviewCount; //리뷰 갯수

    public PlaceModel() {
    }

    public PlaceModel(String place_area, String place_imgUrl, String place_title, float place_rating, String place_tag, Integer place_views, String place_content, String place_address, String place_tel, String place_price, String place_park, double place_wedo, double place_gyoungdo, float place_totalrating, float reviewCount) {
        this.place_area = place_area;
        this.place_imgUrl = place_imgUrl;
        this.place_title = place_title;
        this.place_rating = place_rating;
        this.place_tag = place_tag;
        this.place_views = place_views;
        this.place_content = place_content;
        this.place_address = place_address;
        this.place_tel = place_tel;
        this.place_price = place_price;
        this.place_park = place_park;
        this.place_wedo = place_wedo;
        this.place_gyoungdo = place_gyoungdo;
        this.place_totalrating=place_totalrating;
        this.place_reviewCount=reviewCount;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getPlace_area() {
        return place_area;
    }

    public void setPlace_area(String place_area) {
        this.place_area = place_area;
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

    public float getPlace_rating() {
        return place_rating;
    }

    public void setPlace_rating(float place_rating) {
        this.place_rating = place_rating;
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

    public String getPlace_content() {
        return place_content;
    }

    public void setPlace_content(String place_content) {
        this.place_content = place_content;
    }

    public String getPlace_address() {
        return place_address;
    }

    public void setPlace_address(String place_address) {
        this.place_address = place_address;
    }

    public String getPlace_tel() {
        return place_tel;
    }

    public void setPlace_tel(String place_tel) {
        this.place_tel = place_tel;
    }

    public String getPlace_price() {
        return place_price;
    }

    public void setPlace_price(String place_price) {
        this.place_price = place_price;
    }

    public String getPlace_park() {
        return place_park;
    }

    public void setPlace_park(String place_park) {
        this.place_park = place_park;
    }

    public double getPlace_wedo() {
        return place_wedo;
    }

    public void setPlace_wedo(double place_wedo) {
        this.place_wedo = place_wedo;
    }

    public double getPlace_gyoungdo() {
        return place_gyoungdo;
    }

    public void setPlace_gyoungdo(double place_gyoungdo) {
        this.place_gyoungdo = place_gyoungdo;
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

}
