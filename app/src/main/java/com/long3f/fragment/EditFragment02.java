package com.long3f.fragment;


import android.app.Fragment;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.group3f.gifmaker.R;

import static com.long3f.fragment.EditFragment01.runSeekBar;

;

public class EditFragment02 extends Fragment {

    SeekBar seekBar;
    TextView currentSpeed;

    public EditFragment02() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_fragment02, container, false);
        seekBar = view.findViewById(R.id.seekbar_speed_controler);
        seekBar.getProgressDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        seekBar.getThumb().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        seekBar.setMax(100-5);
        currentSpeed = view.findViewById(R.id.currentSpeed);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
                int value = seekBar.getMax() - (position + 5);
                currentSpeed.setText(String.valueOf(value / 100f));
                runSeekBar.setDelay(value*10);
                Log.e("speed",value+"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        return view;
    }

}
