package com.likeonline.travelmaker.area;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.likeonline.travelmaker.R;

public class SudoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SudoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment JejudoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SudoFragment newInstance(String param1, String param2) {
        SudoFragment fragment = new SudoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private ImageButton sudo_seoul;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_paldo, container, false);
        View view = inflater.inflate(R.layout.fragment_sudo,container,false);

        sudo_seoul = (ImageButton) view.findViewById(R.id.sudo_seoul);
        sudo_seoul.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                ((FragmentReplaceable)getActivity()).replaceFragment(11);
            }
        });

        ImageButton sudobtn=(ImageButton) view.findViewById (R.id.sudobtn);
        sudobtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().remove(SudoFragment.this).commit();
                fragmentManager.popBackStack();
            }
        });


        return view;
    }
}
