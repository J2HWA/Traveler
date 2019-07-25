package com.likeonline.travelmaker.board;

public class PostModel {
    private String post_id; // 글을 식별하기 위한 번호
    private String user_id; // 유저를 식별하기 위한 번호
    private String board_id; // 게시판을 식별하기 위한 번호
    private String user_profileUrl; // 유저 프로필 사진 Url을 저장
    private String post_title; // 제목 텍스트가 들어감
    private String post_contents; // 내용 텍스트가 들어감
    private boolean post_bookbool; // 트래블 북을 넣었는지 안넣었는지 여부
    private String travelbook_id; // 첨부된 트래블 북의 아이디 값
    private String travelbook_title;
    private String post_traveling; // 여행중 / 계획중 내용이 들어감
    private boolean post_chatbool; // 채팅신청을 받을지 안받을지 여부
    private String post_tag; // 태그 내용이 들어감
    private String user_name; // 글쓴이가 누군지 들어감
    private String post_postingtime; // 글을 쓴 시간이 들어감

    public PostModel() {
    }

    public PostModel(String user_id, String board_id, String user_profileUrl, String post_title, String post_contents, boolean post_bookbool, String travelbook_id, String travelbook_title, String post_traveling, boolean post_chatbool, String post_tag, String user_name, String post_postingtime) {
        this.user_id = user_id;
        this.board_id = board_id;
        this.user_profileUrl = user_profileUrl;
        this.post_title = post_title;
        this.post_contents = post_contents;
        this.post_bookbool = post_bookbool;
        this.travelbook_id = travelbook_id;
        this.travelbook_title = travelbook_title;
        this.post_traveling = post_traveling;
        this.post_chatbool = post_chatbool;
        this.post_tag = post_tag;
        this.user_name = user_name;
        this.post_postingtime = post_postingtime;
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getBoard_id() {
        return board_id;
    }

    public void setBoard_id(String board_id) {
        this.board_id = board_id;
    }

    public String getUser_profileUrl() {
        return user_profileUrl;
    }

    public void setUser_profileUrl(String user_profileUrl) {
        this.user_profileUrl = user_profileUrl;
    }

    public String getPost_title() {
        return post_title;
    }

    public void setPost_title(String post_title) {
        this.post_title = post_title;
    }

    public String getPost_contents() {
        return post_contents;
    }

    public void setPost_contents(String post_contents) {
        this.post_contents = post_contents;
    }

    public boolean isPost_bookbool() {
        return post_bookbool;
    }

    public void setPost_bookbool(boolean post_bookbool) {
        this.post_bookbool = post_bookbool;
    }

    public String getTravelbook_id() {
        return travelbook_id;
    }

    public void setTravelbook_id(String travelbook_id) {
        this.travelbook_id = travelbook_id;
    }

    public String getTravelbook_title() {
        return travelbook_title;
    }

    public void setTravelbook_title(String travelbook_title) {
        this.travelbook_title = travelbook_title;
    }

    public String getPost_traveling() {
        return post_traveling;
    }

    public void setPost_traveling(String post_traveling) {
        this.post_traveling = post_traveling;
    }

    public boolean isPost_chatbool() {
        return post_chatbool;
    }

    public void setPost_chatbool(boolean post_chatbool) {
        this.post_chatbool = post_chatbool;
    }

    public String getPost_tag() {
        return post_tag;
    }

    public void setPost_tag(String post_tag) {
        this.post_tag = post_tag;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPost_postingtime() {
        return post_postingtime;
    }

    public void setPost_postingtime(String post_postingtime) {
        this.post_postingtime = post_postingtime;
    }
}