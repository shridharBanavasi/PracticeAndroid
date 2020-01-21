package com.example.mycube;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

public class MyGLSurfaceView extends GLSurfaceView {

    private final MyRenderer mRenderer;

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;


    float touchedX = 0;
    float touchedY = 0;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        if (event != null) {
            float x = event.getX();
            float y = event.getY();

            if (event.getAction() == MotionEvent.ACTION_MOVE) {

                if (mRenderer != null) {
                    float deltaX = (x - mPreviousX) / 2f;
                    float deltaY = (y - mPreviousY) / 2f;


                    mRenderer.mDeltaX += deltaX;
                    mRenderer.mDeltaY += deltaY;
                    mRenderer.mTotalDeltaX += mRenderer.mDeltaX;
                    mRenderer.mTotalDeltaY += mRenderer.mDeltaY;
                    mRenderer.mTotalDeltaX = mRenderer.mTotalDeltaX % 360;
                    mRenderer.mTotalDeltaY = mRenderer.mTotalDeltaY % 360;

                }
                requestRender();
            }
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (event.getX() < 950f && event.getX() > 150f && event.getX() < 1300f && event.getX() > 400f) {
                    Log.d("DEBUG", Float.toString(mRenderer.mTotalDeltaX) + " " + Float.toString(mRenderer.mTotalDeltaY));
                    Log.d("DEBUG", Float.toString(event.getX()) + " " + Float.toString(event.getY()));
//***Here is where I want to add toast*** Thank you so much!///

                } else {

                }
            }


            mPreviousX = x;
            mPreviousY = y;

            return true;
        } else {
            return super.onTouchEvent(event);
        }
    }


    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context
        setEGLContextClientVersion(2);

        mRenderer = new MyRenderer();

        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data.
        // To allow the Square to rotate automatically, this line is commented out:
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
