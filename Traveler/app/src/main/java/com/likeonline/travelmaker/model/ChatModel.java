package com.likeonline.travelmaker.model;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {

   public Map<String,Boolean> users= new HashMap<>(); //채팅방의 유저들
   public Map<String, Comment> comments=new HashMap<>(); //채팅방의 대화내용
    public String user_good;  // 칭찬하기 1:기본상태 2:칭찬하기를 한 자신과 상태의 상태
    public String user_report;  // 칭찬하기 1:기본상태 2:칭찬하기를 한 자신과 상태의 상태
    public String user_travel;  // 칭찬하기 1:기본상태 2:칭찬하기를 한 자신과 상태의 상태

    public static class Comment{

        public String uid;
        public String message;
        public Object timestamp;
        public  Map<String,Object> readUsers = new HashMap<>();
    }
}
