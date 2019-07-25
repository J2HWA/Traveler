package com.likeonline.travelmaker.travelbook;

public class ScheduleModel {
    private String schedule_id; // 스케쥴 키 값
    private String place_id;
    private String schedule_category; // 스케쥴 카테고리
    private String schedule_title; // 여행지의 이름
    private String schedule_tag; // 여행지의 태그
    private String schedule_imgUrl; // 여행지의 이미지 url
    private float schedule_totalrating; //여행지 점수
    private float schedule_reviewCount; //리뷰 갯수

    public ScheduleModel() {
    }

    public ScheduleModel(String place_id, String schedule_category, String schedule_title, String schedule_tag, String schedule_imgUrl, float schedule_totalrating, float schedule_reviewCount) {
        this.place_id = place_id;
        this.schedule_category = schedule_category;
        this.schedule_title = schedule_title;
        this.schedule_tag = schedule_tag;
        this.schedule_imgUrl = schedule_imgUrl;
        this.schedule_totalrating = schedule_totalrating;
        this.schedule_reviewCount = schedule_reviewCount;
    }

    public String getSchedule_id() {
        return schedule_id;
    }

    public void setSchedule_id(String schedule_id) {
        this.schedule_id = schedule_id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public String getSchedule_category() {
        return schedule_category;
    }

    public void setSchedule_category(String schedule_category) {
        this.schedule_category = schedule_category;
    }

    public String getSchedule_title() {
        return schedule_title;
    }

    public void setSchedule_title(String schedule_title) {
        this.schedule_title = schedule_title;
    }

    public String getSchedule_tag() {
        return schedule_tag;
    }

    public void setSchedule_tag(String schedule_tag) {
        this.schedule_tag = schedule_tag;
    }

    public String getSchedule_imgUrl() {
        return schedule_imgUrl;
    }

    public void setSchedule_imgUrl(String schedule_imgUrl) {
        this.schedule_imgUrl = schedule_imgUrl;
    }

    public float getSchedule_totalrating() {
        return schedule_totalrating;
    }

    public void setSchedule_totalrating(float schedule_totalrating) {
        this.schedule_totalrating = schedule_totalrating;
    }

    public float getSchedule_reviewCount() {
        return schedule_reviewCount;
    }

    public void setSchedule_reviewCount(float schedule_reviewCount) {
        this.schedule_reviewCount = schedule_reviewCount;
    }
}
