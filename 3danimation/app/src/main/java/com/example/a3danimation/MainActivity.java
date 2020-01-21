package com.example.a3danimation;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    //GLSurfaceView mySurfaceView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //mySurfaceView = (GLSurfaceView)findViewById(R.id.my_surface_view);
       // mySurfaceView.setEGLContextClientVersion(2);

    }
}
