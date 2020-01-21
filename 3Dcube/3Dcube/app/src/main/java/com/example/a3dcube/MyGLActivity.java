package com.example.a3dcube;

import android.app.Activity;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;

public class MyGLActivity extends Activity {

    private GLSurfaceView glView;
    private MediaPlayer mp;// use GLSurfaceView

    // Call back when the activity is started, to initialize the view
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glView = new GLSurfaceView(this);           // Allocate a GLSurfaceView
        glView.setRenderer(new MyGLRenderer(this)); // Use a custom renderer
        this.setContentView(glView);
        /*glView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //mp = MediaPlayer.create(MyGLActivity.this, R.raw.sound);
                mp.start();
            }
        });// This activity sets to GLSurfaceView*/
    }

    // Call back when the activity is going into the background
    @Override
    protected void onPause() {
        super.onPause();
        glView.onPause();
    }

    // Call back after onPause()
    @Override
    protected void onResume() {
        super.onResume();
        glView.onResume();
    }
}
