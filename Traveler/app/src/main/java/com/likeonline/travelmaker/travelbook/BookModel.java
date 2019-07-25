package com.likeonline.travelmaker.travelbook;

public class BookModel {
    private String book_id; // 트래블 북의 아이디값
    private String book_title; // 트래블 북 제목
    private String book_category; // 생성용버튼, 계획중, 여행중, 다녀옴
    private int book_like; // 트래블 북의 좋아요 갯수가 여기에 저장
    private int book_views; // 트래블 북의 조회수가 여기에 저장
    private boolean book_private; // 트래북 북이 삭제 방지가 되어있는지 검사

    public BookModel() {
    }

    public BookModel(String book_title, String book_category, int book_like, int book_views, boolean book_private) {
        this.book_title = book_title;
        this.book_category = book_category;
        this.book_like = book_like;
        this.book_views = book_views;
        this.book_private = book_private;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getBook_title() {
        return book_title;
    }

    public void setBook_title(String book_title) {
        this.book_title = book_title;
    }

    public String getBook_category() {
        return book_category;
    }

    public void setBook_category(String book_category) {
        this.book_category = book_category;
    }

    public int getBook_like() {
        return book_like;
    }

    public void setBook_like(int book_like) {
        this.book_like = book_like;
    }

    public int getBook_views() {
        return book_views;
    }

    public void setBook_views(int book_views) {
        this.book_views = book_views;
    }

    public boolean isBook_private() {
        return book_private;
    }

    public void setBook_private(boolean book_private) {
        this.book_private = book_private;
    }
}