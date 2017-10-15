package com.long3f.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.group3f.gifmaker.R;
import com.long3f.Interface.OnYourGifAdapterChanged;
import com.long3f.adapter.YourGifGridAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourGifsFragment extends Fragment {

    boolean isYourGif;
    YourGifGridAdapter yourGifGridAdapter;
    private ArrayList<String> paths = new ArrayList<>();

    public YourGifsFragment() {
        isYourGif = true;
    }

    public YourGifsFragment(boolean isYourGif) {
        this.isYourGif = isYourGif;
    }

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private RelativeLayout nogif;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_your_gifs, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_your_gif);
        progressBar = view.findViewById(R.id.progress_bar_loading);
        nogif = view.findViewById(R.id.nogif);
        Log.e("progressbar", "show");

        yourGifGridAdapter = new YourGifGridAdapter(getActivity(), isYourGif);
        yourGifGridAdapter.setOnYourGifAdapterChanged(new OnYourGifAdapterChanged() {

            @Override
            public void OnFinish() {
                yourGifGridAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                Log.e("progressbar", "hide"+" "+yourGifGridAdapter.getItemCount());
                if(yourGifGridAdapter.getItemCount() == 0){
                    nogif.setVisibility(View.VISIBLE);
                }else {
                    nogif.setVisibility(View.GONE);

                }

            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        recyclerView.setAdapter(yourGifGridAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("resume", "changed gif");
        yourGifGridAdapter.updateList(getActivity(), isYourGif);

    }
}
