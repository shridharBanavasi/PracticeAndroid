package com.example.mycube;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import static android.opengl.GLES10.glLoadIdentity;
import static android.opengl.GLES10.glMatrixMode;
import static android.opengl.GLES10.glScalef;
import static android.opengl.GLES20.glViewport;
import static javax.microedition.khronos.opengles.GL10.GL_MODELVIEW;
import static javax.microedition.khronos.opengles.GL10.GL_PROJECTION;

public class MyCube {

    private FloatBuffer vertexBuffer;
    private ShortBuffer drawListBuffer;
    private ShortBuffer[] ArrayDrawListBuffer;
    private FloatBuffer colorBuffer;

    private int mProgram;

    //For Projection and Camera Transformations
    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
                    "attribute vec4 vPosition;" +
                    //"attribute vec4 vColor;" +
                    //"varying vec4 vColorVarying;" +
                    "void main() {" +
                    // the matrix must be included as a modifier of gl_Position
                    // Note that the uMVPMatrix factor *must be first* in order
                    // for the matrix multiplication product to be correct.
                    "  gl_Position = uMVPMatrix * vPosition;" +
                    //"vColorVarying = vColor;"+
                    "}";

    // Use to access and set the view transformation
    private int mMVPMatrixHandle;

    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    //"varying vec4 vColorVarying;"+
                    "void main() {" +
                    //"  gl_FragColor = vColorVarying;" +
                    "  gl_FragColor = vColor;" +
                    "}";

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    float cubeCoords[] = {
            -0.5f, 0.5f, 0.5f,   // front top left 0
            -0.5f, -0.5f, 0.5f,   // front bottom left 1
            0.5f, -0.5f, 0.5f,   // front bottom right 2
            0.5f, 0.5f, 0.5f,  // front top right 3
            -0.5f, 0.5f, -0.5f,   // back top left 4
            0.5f, 0.5f, -0.5f,   // back top right 5
            -0.5f, -0.5f, -0.5f,   // back bottom left 6
            0.5f, -0.5f, -0.5f,  // back bottom right 7
    };


    // Set color with red, green, blue and alpha (opacity) values
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
    float red[] = { 1.0f, 0.0f, 0.0f, 1.0f };
    float blue[] = { 0.0f, 0.0f, 1.0f, 1.0f };

    private short drawOrder[][] = {
            {0, 1, 2, 0, 2, 3},//front
            {0, 4, 5, 0, 5, 3}, //Top
            {0, 1, 6, 0, 6, 4}, //left
            {3, 2, 7, 3, 7 ,5}, //right
            {1, 2, 7, 1, 7, 6}, //bottom
            {4, 6, 7, 4, 7, 5} //back
    }; //(order to draw vertices)


    final float cubeColor3[][] =
            {
                    // Front face (red)
                    {1.0f, 0.0f, 0.0f, 1.0f,
                            1.0f, 0.0f, 0.0f, 1.0f,
                            1.0f, 0.0f, 0.0f, 1.0f,
                            1.0f, 0.0f, 0.0f, 1.0f,
                            1.0f, 0.0f, 0.0f, 1.0f,
                            1.0f, 0.0f, 0.0f, 1.0f},

                    // Top face (green)
                   {0.0f, 1.0f, 0.0f, 1.0f,
                            0.0f, 1.0f, 0.0f, 1.0f,
                            0.0f, 1.0f, 0.0f, 1.0f,
                            0.0f, 1.0f, 0.0f, 1.0f,
                            0.0f, 1.0f, 0.0f, 1.0f,
                            0.0f, 1.0f, 0.0f, 1.0f},

                    // Left face (blue)
                    {0.0f, 0.0f, 1.0f, 1.0f,
                            0.0f, 0.0f, 1.0f, 1.0f,
                            0.0f, 0.0f, 1.0f, 1.0f,
                            0.0f, 0.0f, 1.0f, 1.0f,
                            0.0f, 0.0f, 1.0f, 1.0f,
                            0.0f, 0.0f, 1.0f, 1.0f},

                    // Right face (yellow)
                    {1.0f, 1.0f, 0.0f, 1.0f,
                            1.0f, 1.0f, 0.0f, 1.0f,
                            1.0f, 1.0f, 0.0f, 1.0f,
                            1.0f, 1.0f, 0.0f, 1.0f,
                            1.0f, 1.0f, 0.0f, 1.0f,
                            1.0f, 1.0f, 0.0f, 1.0f},

                    // Bottom face (cyan)
                    {0.0f, 1.0f, 1.0f, 1.0f,
                            0.0f, 1.0f, 1.0f, 1.0f,
                            0.0f, 1.0f, 1.0f, 1.0f,
                            0.0f, 1.0f, 1.0f, 1.0f,
                            0.0f, 1.0f, 1.0f, 1.0f,
                            0.0f, 1.0f, 1.0f, 1.0f},

                    //Back face (magenta)
                    {1.0f, 0.0f, 1.0f, 1.0f,
                            1.0f, 0.0f, 1.0f, 1.0f,
                            1.0f, 0.0f, 1.0f, 1.0f,
                            1.0f, 0.0f, 1.0f, 1.0f,
                            1.0f, 0.0f, 1.0f, 1.0f,
                            1.0f, 0.0f, 1.0f, 1.0f}
            };



    public MyCube() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                cubeCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(cubeCoords);
        vertexBuffer.position(0);
        //glScalef(24, 100, 22);



        int vertexShader = MyRenderer.loadShader(GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = MyRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        // create empty OpenGL ES Program
        mProgram = GLES20.glCreateProgram();

        // add the vertex shader to program
        GLES20.glAttachShader(mProgram, vertexShader);

        // add the fragment shader to program
        GLES20.glAttachShader(mProgram, fragmentShader);

        // creates OpenGL ES program executables
        GLES20.glLinkProgram(mProgram);
    }

    private int mPositionHandle;
    private int mColorHandle;

    private final int vertexCount = cubeCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    public void draw(float[] mvpMatrix) { // pass in the calculated transformation matrix
        // Draw the cube
        for (int face = 0; face < 6; face++) {

            // Add program to OpenGL ES environment
            GLES20.glUseProgram(mProgram);

            // get handle to vertex shader's vPosition member
            mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            // get handle to fragment shader's vColor member

            //mColorHandle = GLES20.glGetAttribLocation(mProgram, "vColor");
            mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

            // Enable a handle to the cube vertices
            GLES20.glEnableVertexAttribArray(mPositionHandle);
            // Prepare the cube coordinate data
            GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                    GLES20.GL_FLOAT, false,
                    vertexStride, vertexBuffer);
            // initialize byte buffer for the draw list
            ByteBuffer dlb = ByteBuffer.allocateDirect(
                    // (# of coordinate values * 2 bytes per short)
                    drawOrder[face].length * 2);
            dlb.order(ByteOrder.nativeOrder());
            drawListBuffer = dlb.asShortBuffer();
            drawListBuffer.put(drawOrder[face]);
            drawListBuffer.position(0);

            GLES20.glUniform4fv(mColorHandle, 1, cubeColor3[face], 0);

            // get handle to shape's transformation matrix
            mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

            // Pass the projection and view transformation to the shader
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

            GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder[face].length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);


        }


        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mMVPMatrixHandle);


    }
}
