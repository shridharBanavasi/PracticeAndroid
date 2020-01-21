package com.example.a3danimation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MotionEventCompat;

import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

public class SecondOne extends AppCompatActivity {
    ImageView m360DegreeImageView;
    int mStartX;
    int mStartY;
    int mEndX;
    int mEndY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_one);

        m360DegreeImageView = (ImageView) findViewById(R.id.santafe3dview);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_DOWN):

                mStartX = (int) event.getX();
                mStartY = (int) event.getY();
                return true;

            case (MotionEvent.ACTION_MOVE):

                mEndX = (int) event.getX();
                mEndY = (int) event.getY();

                int mImageIndex = 0;
                if ((mEndX - mStartX) > 3) {
                    mImageIndex++;
                    if (mImageIndex > 56)
                        mImageIndex = 0;

                    m360DegreeImageView.setImageLevel(mImageIndex);

                }
                if ((mEndX - mStartX) < -3) {
                    mImageIndex--;
                    if (mImageIndex < 0)
                        mImageIndex = 56;

                    m360DegreeImageView.setImageLevel(mImageIndex);

                }
                mStartX = (int) event.getX();
                mStartY = (int) event.getY();
                return true;

            case (MotionEvent.ACTION_UP):
                mEndX = (int) event.getX();
                mEndY = (int) event.getY();

                return true;

            case (MotionEvent.ACTION_CANCEL):
                return true;

            case (MotionEvent.ACTION_OUTSIDE):
                return true;

            default:
                return super.onTouchEvent(event);
        }
    }
}
