package com.long3f.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.group3f.gifmaker.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class GIFHYFragment extends Fragment {


    public GIFHYFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gifhy, container, false);
    }

}
