package com.example.velmurugan.volleycacheexample;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerviewAdapter recyclerviewAdapter;
    private List<Movie> movieList;
    private String jsonArrayUrl="http://velmm.com/apis/volley_array.json";
    private Gson gson;
    private ProgressDialog pDialog;
    private String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();

        movieList = new ArrayList<>();
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        recyclerviewAdapter = new RecyclerviewAdapter();
        recyclerView.setAdapter(recyclerviewAdapter);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");

        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(jsonArrayUrl);
        if(entry != null){

            try {
                String data = new String(entry.data ,"UTF-8");
                setData(data,true);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        } else {
            callJsonArrayRequest();
        }
    }

    private void callJsonArrayRequest() {
        // TODO Auto-generated method stub
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                jsonArrayUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                setData(response,false);
                dismissDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dismissDialog();
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
    }

    private void setData(String response,Boolean isCache){
        //Log.d(TAG, response.toString());
        movieList = Arrays.asList(gson.fromJson(response,Movie[].class));
        recyclerviewAdapter.setMovieList(movieList);
        if(isCache){
            Toast.makeText(getApplicationContext(), "Loading from Volley Cache", Toast.LENGTH_SHORT).show();
            dismissDialog();
        }
    }

    private void dismissDialog() {
        // TODO Auto-generated method stub
        if(pDialog.isShowing()){
            pDialog.dismiss();
        }
    }
    private void showDialog() {
        // TODO Auto-generated method stub
        if(!pDialog.isShowing()){
            pDialog.show();
        }
    }

}
