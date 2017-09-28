package com.long3f.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.group3f.gifmaker.R;
import com.long3f.Interface.OnYourGifAdapterChanged;
import com.long3f.adapter.YourGifGridAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class YourGifsFragment extends Fragment {

    boolean isYourGif;
    YourGifGridAdapter yourGifGridAdapter;

    public YourGifsFragment() {
        isYourGif = true;
    }

    public YourGifsFragment(boolean isYourGif) {
        this.isYourGif = isYourGif;
    }

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_your_gifs, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_your_gif);
        yourGifGridAdapter = new YourGifGridAdapter(getActivity(), isYourGif);
        yourGifGridAdapter.setOnYourGifAdapterChanged(new OnYourGifAdapterChanged() {
            @Override
            public void OnChanged() {
                Log.e("change", "changed gif");
                yourGifGridAdapter.updateList(getActivity(), isYourGif);
                yourGifGridAdapter.notifyDataSetChanged();
            }

        });
        int numberOfColumns = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        recyclerView.setAdapter(yourGifGridAdapter);
        return view;
    }

}
