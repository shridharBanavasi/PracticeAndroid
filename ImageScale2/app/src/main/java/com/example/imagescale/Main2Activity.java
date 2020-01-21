package com.example.imagescale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class Main2Activity extends AppCompatActivity {
    ImageView image;
    CoordinatorLayout background;
    int Scr_height;
    int Scr_weight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        image = new ImageView(getApplicationContext());
        background = findViewById(R.id.background);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Scr_height = background.getLayoutParams().height;
        Scr_weight = displayMetrics.widthPixels;
        Log.d("sss", "onCreate: "+Scr_weight+"  "+Scr_height);

        //RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)background.

        //params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        //params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        //background.setLayoutParams(params);
        //Log.d("h", "onCreate: "+background.getLayoutParams().height+"gg"+background.getPivotX()+"gg"+background.getTop()+background.getHeight()+"s");
        //Log.d("m", "onCreate: "+height+"  "+weight);
        image.setImageResource(R.drawable.bluetooth);




        setImagePosicton(416, 486, 850, 600);

    }

    private void setImagePosicton(float x, float y, int width, int height) {
        float x1=(x*Scr_weight)/width;
        if((Scr_weight-200)<x1 && x1<=Scr_weight){
            x1=x1-200;
        }


        float y1=(y*Scr_height)/height;
        if((Scr_height-100)<y1 && y1<=Scr_height){
            y1=y1-100;
        }
        Log.d("s", "setImagePosicton: "+x+"  "+y+"  "+x1+"  "+y1);

        image.setY(y1);
        image.setX(x1);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200, 100);
        background.addView(image,params);




    }
}
