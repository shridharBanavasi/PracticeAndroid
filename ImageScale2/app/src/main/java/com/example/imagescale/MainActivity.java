package com.example.imagescale;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ImageView floor;
    Point point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        floor=findViewById(R.id.floor);
        Log.d("qq", "onCreate: ");

        /*Drawable drawable = floor.getDrawable();
        Rect imageBounds = drawable.getBounds();

//original height and width of the bitmap
        int intrinsicHeight = drawable.getIntrinsicHeight();
        int intrinsicWidth = drawable.getIntrinsicWidth();
        Log.d("qq", "onCreate: "+intrinsicHeight+"11"+intrinsicWidth);

//height and width of the visible (scaled) image
        int scaledHeight = imageBounds.height();
        int scaledWidth = imageBounds.width();
        Log.d("qqq", "onCreate: "+scaledHeight+"11"+scaledWidth);
*/


        Bitmap decodedByte = BitmapFactory.decodeResource(getResources(),R.drawable.floorpleaan);
        floor.setImageBitmap(decodedByte);


        Drawable drawable = floor.getDrawable();
        Rect imageBounds = drawable.getBounds();

//original height and width of the bitmap
        int intrinsicHeight = drawable.getIntrinsicHeight();
        int intrinsicWidth = drawable.getIntrinsicWidth();
        Log.d("qq", "onCreate: "+intrinsicHeight+"11"+intrinsicWidth);

//height and width of the visible (scaled) image
        int scaledHeight = imageBounds.height();
        int scaledWidth = imageBounds.width();
        Log.d("qqq", "onCreate: "+scaledHeight+"11"+scaledWidth);
//Find the ratio of the original image to the scaled image
//Should normally be equal unless a disproportionate scaling
//(e.g. fitXY) is used.
        /*float heightRatio = intrinsicHeight / scaledHeight;
        float widthRatio = intrinsicWidth / scaledWidth;
        Log.d("qqqq", "onCreate: "+heightRatio+"11"+widthRatio);

//do whatever magic to get your touch point
//MotionEvent event;

//get the distance from the left and top of the image bounds
        int scaledImageOffsetX = event.getX() - imageBounds.left;
        int scaledImageOffsetY = event.getY() - imageBounds.top;

//scale these distances according to the ratio of your scaling
//For example, if the original image is 1.5x the size of the scaled
//image, and your offset is (10, 20), your original image offset
//values should be (15, 30).
        int originalImageOffsetX = scaledImageOffsetX * widthRatio;
        int originalImageOffsetY = scaledImageOffsetY * heightRatio;*/

    }

    /*public void floodFill(Bitmap image, Point node, int targetColor,
                          int replacementColor) {
        int width = image.getWidth();
        int height = image.getHeight();
        int target = targetColor;
        int replacement = replacementColor;
        if (target != replacement) {
            Queue<Point> queue = new Queue<Point>();
            do {

                int x = node.x;
                int y = node.y;
                while (x > 0 && image.getPixel(x - 1, y) == target) {
                    x--;

                }
                boolean spanUp = false;
                boolean spanDown = false;
                while (x < width && image.getPixel(x, y) == target) {
                    image.setPixel(x, y, replacement);
                    if (!spanUp && y > 0
                            && image.getPixel(x, y - 1) == target) {
                        queue.add(new Point(x, y - 1));
                        spanUp = true;
                    } else if (spanUp && y > 0
                            && image.getPixel(x, y - 1) != target) {
                        spanUp = false;
                    }
                    if (!spanDown && y < height - 1
                            && image.getPixel(x, y + 1) == target) {
                        queue.add(new Point(x, y + 1));
                        spanDown = true;
                    } else if (spanDown && y < height - 1
                            && image.getPixel(x, y + 1) != target) {
                        spanDown = false;
                    }
                    x++;
                }
            } while ((node = queue.poll()) != null);
        }
    }*/
}
