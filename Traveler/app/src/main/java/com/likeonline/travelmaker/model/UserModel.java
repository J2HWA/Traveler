package com.likeonline.travelmaker.model;

import android.widget.Spinner;

import java.util.HashMap;
import java.util.Map;

public class UserModel {
    public String userName;
    public String userAge;
    public String userSNS;
    public String userSex;
    public String uid;
    public String profileImage1;
    public String pushToken;
    public String comment;
    public String userid;
    public String user_accept;  // 매칭 요청 1:기본상태 2:신청받은상태 3: 여행하기 완료 상태
    public String user_acceptCP; // 매칭요청 1: 기본 상태 2:이미 여행상태가 완료된 상태
    public String user_good;  // 칭찬하기 1:기본상태 2:칭찬하기를 한 자신과 상태의 상태
    public String user_goodCP; // 매칭요청 1: 기본 상태 2:칭찬하기를 받은 상대의 상태
    public String user_report; // 매칭요청 1: 기본 상태 2:칭찬하기를 받은 상대의 상태
    public int user_score; // 등급점수
    public float user_trustScore; //신뢰점수
    public int user_trustCount; //신뢰도 점수 갯수
    public long kakaouid;
    public String facetrue; //페이스북 가입화면 체크
    public String user_job;
    public String user_religion;
    public String user_drinking;
    public String user_bloodtype;
    public String user_height;
    public String user_location;
    public String user_introduce;

}
