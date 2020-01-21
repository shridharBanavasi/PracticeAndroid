package com.example.mycube;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Plane {

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

	float[] planePositionData = {
			// X, Y, Z, 
			-25.0f, -5.0f, -25.0f,//0
			-25.0f, -5.0f, 25.0f,//1
			25.0f, -5.0f, -25.0f,//2
			25.0f, -5.0f, 25.0f,//3
			};

	// Set color with red, green, blue and alpha (opacity) values
	float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
	float red[] = { 1.0f, 0.0f, 0.0f, 1.0f };
	float blue[] = { 0.0f, 0.0f, 1.0f, 1.0f };

	private short drawOrder[]= {
			0, 1, 2, 3, 2, 0 //bottom
	}; //(order to draw vertices)
			
	float[] planeColorData = {
			// R, G, B, A
			0.5f, 0.5f, 0.5f, 1.0f,
			0.5f, 0.5f, 0.5f, 1.0f,
			0.5f, 0.5f, 0.5f, 1.0f,
			0.5f, 0.5f, 0.5f, 1.0f,
			0.5f, 0.5f, 0.5f, 1.0f,
			0.5f, 0.5f, 0.5f, 1.0f
		};
	
	public Plane() {
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
				// (# of coordinate values * 4 bytes per float)
				planePositionData.length * 4);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(planePositionData);
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

	private final int vertexCount = planePositionData.length / COORDS_PER_VERTEX;
	private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

	public void draw(float[] mvpMatrix) { // pass in the calculated transformation matrix
		// Draw the cube


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
					drawOrder.length * 2);
			dlb.order(ByteOrder.nativeOrder());
			drawListBuffer = dlb.asShortBuffer();
			drawListBuffer.put(drawOrder);
			drawListBuffer.position(0);

			GLES20.glUniform4fv(mColorHandle, 1, planeColorData, 0);

			// get handle to shape's transformation matrix
			mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

			// Pass the projection and view transformation to the shader
			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

			GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);





		// Disable vertex array
		GLES20.glDisableVertexAttribArray(mMVPMatrixHandle);


	}
}