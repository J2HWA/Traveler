package com.likeonline.travelmaker.area;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.likeonline.travelmaker.R;
import com.likeonline.travelmaker.board.BoardActivity;

public class HomeFragment extends Fragment implements FragmentReplaceable {
    private Fragment paldoFragment;
    private Fragment sudoFragment;
    private Fragment gangwondoFragment;
    private Fragment jejudoFragment;
    private Fragment seoulFragment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup View = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        Button button = (Button) View.findViewById(R.id.home_btn_board);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(), BoardActivity.class);
                startActivity(intent);
            }
        });

        paldoFragment = new PaldoFragment();
        sudoFragment = new SudoFragment();
        gangwondoFragment = new GangwondoFragment();
        jejudoFragment = new JejudoFragment();
        seoulFragment = new SeoulFragment();
        setDefaultFragment();

        return View;
    }

    public interface onKeyBackPressedListener {
        void onBackKey();
    }
    private onKeyBackPressedListener mOnKeyBackPressedListener;
    public void setOnKeyBackPressedListener(onKeyBackPressedListener listener) {
        mOnKeyBackPressedListener = listener;
    }

    //메인에서 토스트를 띄우며 종료확인을 하기 위해 필드선언









    public void setDefaultFragment(){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.replace(R.id.container, paldoFragment);

        transaction.commit();
    }

    /**
     * Fragment 변경
     * @param fragmentId : 보여질 Fragment
     */
    @Override
    public void replaceFragment(int fragmentId) {
        /**
         * 화면에 보여지는 Fragment를 관리한다.
         * FragmentManager : Fragment를 바꾸거나 추가하는 객체
         */

        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();

        /*
         *
         * R.id.container(activity_main.xml)에 띄우겠다.
         * 파라미터로 오는 fragmentId에 따라 다음에 보여질 Fragment를 설정한다.
         */
        switch (fragmentId){
            case 1:
                transaction.replace(R.id.container, paldoFragment);
                break;
            case 2:
                transaction.replace(R.id.container, sudoFragment);
                break;
            case 3:
                transaction.replace(R.id.container, gangwondoFragment);
                break;
            case 10:
                transaction.replace(R.id.container, jejudoFragment);
                break;
            case 11:
                transaction.replace(R.id.container, seoulFragment);
                break;
        }
        /**
         * Fragment의 변경사항을 반영시킨다.
         */
        //transaction.addToBackStack(null);
        transaction.commit();
    }

}