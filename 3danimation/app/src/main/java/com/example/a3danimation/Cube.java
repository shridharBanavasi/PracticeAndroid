package com.example.a3danimation;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import com.example.a3danimation.common.RenderConstants;

import android.opengl.GLES20;

/**
 * Simple cube in openGL
 */
public class Cube {
    private final FloatBuffer cubePosition;
    private final FloatBuffer cubeNormal;
    private final FloatBuffer cubeColor;

    // 6 sides * 2 triangles * 3 vertices * 3 coordinates
    float[] cubePositionData = {
            -1.0f, -1.0f, -1.0f, // triangle 1 : begin:
            -1.0f, -1.0f, 1.0f, //
            -1.0f, 1.0f, 1.0f, // triangle 1 : end:5 -8
            1.0f, 1.0f, -1.0f, // triangle 2 : begin:2
            -1.0f, -1.0f, -1.0f, //
            -1.0f, 1.0f, -1.0f, // triangle 2 : end:1 -17
            1.0f, -1.0f, 1.0f,//triangle 3 : begin:
            -1.0f, -1.0f, -1.0f,//
            1.0f, -1.0f, -1.0f,// triangle 3 : end : -26
            1.0f, 1.0f, -1.0f, //triangle 4 : begin:2
            1.0f, -1.0f, -1.0f, //
            -1.0f, -1.0f, -1.0f, //triangle 4 : end :  -35
            -1.0f, -1.0f, -1.0f, //triangle 5 : begin :
            -1.0f, 1.0f, 1.0f, //5
            -1.0f, 1.0f, -1.0f, //triangle 5 : end :1 -44
            1.0f, -1.0f, 1.0f, //triangle 6 : begin:
            -1.0f, -1.0f, 1.0f, //
            -1.0f, -1.0f, -1.0f, //triangle 6 : end : -53
            -1.0f, 1.0f, 1.0f, //triangle 7 : begin :5
            -1.0f, -1.0f, 1.0f, //
            1.0f, -1.0f, 1.0f,  //triangle 7 : end : --62
            1.0f, 1.0f, 1.0f,  //triangle 8 : begin :6
            1.0f, -1.0f, -1.0f, //
            1.0f, 1.0f, -1.0f,  //triangle 8 : end :2 -71
            1.0f, -1.0f, -1.0f,  //triangle 9 : begin:
            1.0f, 1.0f, 1.0f, //6
            1.0f, -1.0f, 1.0f,  //triangle 9 : end :  -80
            1.0f, 1.0f, 1.0f,  //triangle 10 : begin:6
            1.0f, 1.0f, -1.0f,  //2
            -1.0f, 1.0f, -1.0f, //triangle 10 : end :1 -89
            1.0f, 1.0f, 1.0f,  //triangle 11 : begin:6
            -1.0f, 1.0f, -1.0f, //1
            -1.0f, 1.0f, 1.0f,  //triangle 11 : end :5  -98
            1.0f, 1.0f, 1.0f,   //triangle 12 : begin :6
            -1.0f, 1.0f, 1.0f,  //5
            1.0f, -1.0f, 1.0f  //triangle 12 : begin:  -107
    };


    float[] cubeNormalData = {
            // nX, nY, nZ
            -1.0f, 0.0f, 0.0f, // triangle 1 : begin
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f, // triangle 1 : end
            0.0f, 0.0f, -1.0f, // triangle 2 : begin
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f, // triangle 2 : end
            0.0f, -1.0f, 0.0f, //
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, 0.0f, -1.0f, //
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            -1.0f, 0.0f, 0.0f, //
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            0.0f, -1.0f, 0.0f, //
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, 0.0f, 1.0f, //
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            1.0f, 0.0f, 0.0f, //
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f, //
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, //
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f, //
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f, //
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f
    };

    //size of color data array: 6 sides * 2 triangles * 3 points * 4 color (RGBA) values
    float[] cubeColorData = new float[12 * 3 * 4];

    /**
     * Create a new cube with specified center position, size and color
     *
     * @param center
     * @param size
     * @param color
     */
    public Cube(float[] center, float size, float[] color,float hig) {
        //set color data
        for (int v = 0; v < 12 * 3; v++) {
            cubeColorData[4 * v + 0] = color[0];
            cubeColorData[4 * v + 1] = color[1];
            cubeColorData[4 * v + 2] = color[2];
            cubeColorData[4 * v + 3] = color[3];
        }

        //resize the cube
        for (int i = 0; i < 108; i++) {
            cubePositionData[i] = cubePositionData[i] * size / 2;
        }

        //move the center of the cube to the place specified in parameter
        for (int j = 0; j < 36; j++) {
            cubePositionData[3 * j] = cubePositionData[3 * j] + center[0];
            cubePositionData[3 * j + 1] = cubePositionData[3 * j + 1] + center[1];
            cubePositionData[3 * j + 2] = cubePositionData[3 * j + 2] + center[2];
        }


        /*for (int k = 0; k < 36; k++) {
            cubePositionData[3 * k] = cubePositionData[3 * k];
            cubePositionData[3 * k + 1] = cubePositionData[3 * k + 1] + hig;
            cubePositionData[3 * k + 2] = cubePositionData[3 * k + 2];
        }*/
        //To increase height.
        increaseHeight(hig);

        // Initialize the buffers.
        ByteBuffer bPos = ByteBuffer.allocateDirect(cubePositionData.length * RenderConstants.FLOAT_SIZE_IN_BYTES);
        bPos.order(ByteOrder.nativeOrder());
        cubePosition = bPos.asFloatBuffer();

        ByteBuffer bNormal = ByteBuffer.allocateDirect(cubeNormalData.length * RenderConstants.FLOAT_SIZE_IN_BYTES);
        bNormal.order(ByteOrder.nativeOrder());
        cubeNormal = bNormal.asFloatBuffer();

        ByteBuffer bColor = ByteBuffer.allocateDirect(cubeColorData.length * RenderConstants.FLOAT_SIZE_IN_BYTES);
        bColor.order(ByteOrder.nativeOrder());
        cubeColor = bColor.asFloatBuffer();

        cubePosition.put(cubePositionData).position(0);
        cubeNormal.put(cubeNormalData).position(0);
        cubeColor.put(cubeColorData).position(0);
    }

    public void render(int positionAttribute, int normalAttribute, int colorAttribute, boolean onlyPosition) {

        // Pass in the position information
        cubePosition.position(0);
        GLES20.glVertexAttribPointer(positionAttribute, 3, GLES20.GL_FLOAT, false,
                0, cubePosition);

        GLES20.glEnableVertexAttribArray(positionAttribute);


        if (!onlyPosition) {
            // Pass in the normal information
            cubeNormal.position(0);
            GLES20.glVertexAttribPointer(normalAttribute, 3, GLES20.GL_FLOAT, false,
                    0, cubeNormal);

            GLES20.glEnableVertexAttribArray(normalAttribute);

            // Pass in the color information
            cubeColor.position(0);
            GLES20.glVertexAttribPointer(colorAttribute, 4, GLES20.GL_FLOAT, false,
                    0, cubeColor);

            GLES20.glEnableVertexAttribArray(colorAttribute);
        }

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
    }

    /*public void setCubePosition(float hig){
        for (int j = 0; j < 36; j++) {
            cubePositionData[3*j] = cubePositionData[3*j];
            cubePositionData[3*j + 1] = cubePositionData[3*j + 1]+hig;
            cubePositionData[3*j + 2] = cubePositionData[3*j + 2];
        }
    }*/
    public void increaseHeight(float hig){
        //pt 1, increase z cor:
        cubePositionData[16]=cubePositionData[16]+hig;
        cubePositionData[43]=cubePositionData[43]+hig;
        cubePositionData[88]=cubePositionData[88]+hig;
        cubePositionData[94]=cubePositionData[94]+hig;

        //pt 2, increase z cor:
        cubePositionData[10]=cubePositionData[10]+hig;
        cubePositionData[28]=cubePositionData[28]+hig;
        cubePositionData[70]=cubePositionData[70]+hig;
        cubePositionData[85]=cubePositionData[85]+hig;

        //pt 5, increase z cor:
        cubePositionData[7]=cubePositionData[7]+hig;
        cubePositionData[40]=cubePositionData[40]+hig;
        cubePositionData[55]=cubePositionData[55]+hig;
        cubePositionData[97]=cubePositionData[97]+hig;
        cubePositionData[103]=cubePositionData[103]+hig;


        //pt 6, increase z cor:
        cubePositionData[64]=cubePositionData[64]+hig;
        cubePositionData[76]=cubePositionData[76]+hig;
        cubePositionData[82]=cubePositionData[82]+hig;
        cubePositionData[91]=cubePositionData[91]+hig;
        cubePositionData[100]=cubePositionData[100]+hig;

    }
}