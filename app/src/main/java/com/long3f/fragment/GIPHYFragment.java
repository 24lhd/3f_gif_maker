package com.long3f.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.group3f.gifmaker.R;
import com.long3f.Model.GiphyModel;
import com.long3f.adapter.GiphyGridAdapter;
import com.long3f.helper.VolleySingleton;
import com.long3f.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class GIPHYFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Spinner spinner;
    private RelativeLayout rlSearch;
    private ImageView btnClose,btnSearch;
    private EditText edSearch;
    private RelativeLayout networkerr;
    private TextView tryAgain;
    public static ArrayList<GiphyModel> listUrl = new ArrayList<>();
    private GiphyGridAdapter giphyGridAdapter;
    public GIPHYFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gifhy, container, false);
        progressBar = view.findViewById(R.id.progress_bar_giphy);
        recyclerView = view.findViewById(R.id.recyclerview_giphy);
        spinner = view.findViewById(R.id.spinner_category);
        rlSearch = view.findViewById(R.id.rl_search);
        btnClose = view.findViewById(R.id.img_close);
        btnSearch = view.findViewById(R.id.img_search);
        edSearch = view.findViewById(R.id.ed_search);
        networkerr = view.findViewById(R.id.network_err);
        tryAgain = view.findViewById(R.id.btn_giphy_try_again);

        spinner.setOnItemSelectedListener(this);

        giphyGridAdapter = new GiphyGridAdapter(getActivity(),listUrl);
        int numberOfColumns = 3;
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        recyclerView.setAdapter(giphyGridAdapter);

        return view;
    }


    private void loadListGifFromGiphy(final String query) {

        String url_request = "http://api.giphy.com/v1/gifs/search?api_key=l41lJ2OONlirEGYOA&limit=50&q="+query;
        networkerr.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url_request, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("request", "JsonObjectRequest onResponse: " + response.getJSONObject("meta").toString());
                            //JSON.data[13].images["480w_still"].url
                            JSONArray arrdata= response.getJSONArray("data");
                            listUrl.clear();
                            for(int i = 0 ; i <arrdata.length() ; i++ ){
                                String id = arrdata.getJSONObject(i).getString("id");
                                String pre = arrdata.getJSONObject(i)
                                        .getJSONObject("images")
                                        .getJSONObject("480w_still")
                                        .getString("url");
                                String down = arrdata.getJSONObject(i)
                                        .getJSONObject("images")
                                        .getJSONObject("downsized_medium")
                                        .getString("url");

                                GiphyModel giphyModel = new GiphyModel(id,pre,down);
                                listUrl.add(giphyModel);
                                Log.e("onResponse: ", ""+giphyModel.toString());
                            }
                            giphyGridAdapter.notifyDataSetChanged();
                            networkerr.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("request", "JsonObjectRequest onErrorResponse: " + error.getMessage());
                listUrl.clear();
                giphyGridAdapter.notifyDataSetChanged();
                networkerr.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });
        VolleySingleton.getInstance(getActivity()).getRequestQueue().add(jsonObjectRequest);
        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("onClick"," try Again request "+query );
                networkerr.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                VolleySingleton.getInstance(getActivity()).getRequestQueue().add(jsonObjectRequest);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i){
            case 0:
                loadListGifFromGiphy("trending");
                break;
            case 1:
                loadListGifFromGiphy("funny");
                break;
            case 2:
                loadListGifFromGiphy("animal");
                break;
            case 3:
                loadListGifFromGiphy("movie");
                break;
            case 4:
                showSearch();
                break;
        }
    }

    private void showSearch() {
        rlSearch.setVisibility(View.VISIBLE);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!edSearch.getText().toString().equals("")) {
                    loadListGifFromGiphy(edSearch.getText().toString());
                    AppUtils.hideSoftKeyboard(getActivity());

                }
            }
        });
        edSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if(!edSearch.getText().toString().equals("")) {
                        loadListGifFromGiphy(edSearch.getText().toString());
                        AppUtils.hideSoftKeyboard(getActivity());
                    }
                    handled = true;
                }
                return handled;
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rlSearch.setVisibility(View.GONE);
                spinner.setSelection(0);
            }
        });
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
