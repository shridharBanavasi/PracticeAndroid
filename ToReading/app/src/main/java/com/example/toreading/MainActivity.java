package com.example.toreading;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.toreading.api.RetrofitClient;
import com.example.toreading.database.DataBaseHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
 TextView t;
 Button b1,b2,b3;
 boolean a=false;
 DataBaseHelper db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        t=findViewById(R.id.info);
        b1=findViewById(R.id.reading);
        b2=findViewById(R.id.clearstep);
        b3=findViewById(R.id.clear);

        db=new DataBaseHelper(MainActivity.this);

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(a==false){
                    b3.setVisibility(View.VISIBLE);
                    a=true;
                }else if(a==true){
                    b3.setVisibility(View.GONE);
                    a=false;
                }
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.clearAllData();
            }
        });



        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Call<ResponseBody> call=new RetrofitClient().getInstance().getApi().getMatData("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1YWE2YTViNDA0YTExYjBlNmMzNGQ4ZmUiLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE1Nzg1NTMxNTUsImV4cCI6MTU3ODU3MTE1NX0.JfgktLcBH0FP_L6OJKNTV6mXS5kY1SH8hX_cYJRN-2U");
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.code()==200){
                            setMessage("code is 200");
                            try {
                                String s= response.body().string();
                                JSONArray a=new JSONArray(s);
                                JSONObject innerObj = a.getJSONObject(0);
                                if (innerObj.has("reading")) {
                                    JSONArray reading = innerObj.getJSONArray("reading");
                                    ArrayList<JSONArray> op=new ArrayList<>();
                                    op.add(reading.getJSONArray(0));
                                    op.add(reading.getJSONArray(1));
                                    op.add(reading.getJSONArray(2));
                                    op.add(reading.getJSONArray(3));
                                    op.add(reading.getJSONArray(4));
                                    op.add(reading.getJSONArray(5));
                                    db.insert(op);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }



                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        setMessage("fail");
                    }
                });
            }
        });


    }

    private void setMessage(String msg) {
        t.setText(msg);
    }
}
