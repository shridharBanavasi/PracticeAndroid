package com.example.databaseexample;

import android.database.Cursor;
import android.os.Bundle;

import com.example.databaseexample.databas.DataBaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    DataBaseHelper myDb;
    String currentDateandTime;
    List<String> a=new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb = new DataBaseHelper(MainActivity.this);

        addDataTODataBase();



    }

    private void addDataTODataBase() {
        for (int i = 0; i < 10; i++) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
            currentDateandTime = sdf.format(new Date());
            myDb.insertToolsHistoryTB(currentDateandTime,"Spaner"+i,"1");
        }

        Cursor res=myDb.getHistory();
        while (res.moveToNext()) {
            String a,b;
            a=res.getString(0);
            b=res.getString(1);
            Log.d("jjj", "addDataTODataBase: "+a+" "+b);


        }
    }


}
