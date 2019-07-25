package com.likeonline.travelmaker;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;

import com.likeonline.travelmaker.chat.MessageActivity;

public class TravelDialog extends Dialog {

    private Button mPositiveButton2;
    private View.OnClickListener mPositiveListener2;
    private Button mCancelButton2;
    private View.OnClickListener mCancelListener2;
    private RatingBar ratingBar;
    private float ratingScore;
    MessageActivity messageActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.report_dialog);

        //셋팅
        mPositiveButton2=(Button)findViewById(R.id.ok_button);
        mCancelButton2=(Button)findViewById(R.id.cancel_button);

        //클릭 리스너 셋팅 (클릭버튼이 동작하도록 만들어줌.)
        mPositiveButton2.setOnClickListener(mPositiveListener2);
        mCancelButton2.setOnClickListener(mCancelListener2);

    }

    //생성자 생성


    public TravelDialog(Context context, View.OnClickListener mPositiveListener, View.OnClickListener mCancelListener , MessageActivity ma) {
        super(context);
        this.messageActivity=ma;
        this.mPositiveListener2 = mPositiveListener;
        this.mCancelListener2 = mCancelListener;
    }
}
