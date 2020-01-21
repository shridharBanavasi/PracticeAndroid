package com.example.a3danimation;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.a3danimation.R;
import com.example.a3danimation.ShadowsActivity;
import com.example.a3danimation.api.RetrofitClient;
import com.example.a3danimation.common.RenderConstants;
import com.example.a3danimation.common.FPSCounter;
import com.example.a3danimation.common.RenderProgram;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.Build;
import android.os.SystemClock;
import android.util.ArraySet;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.media.CamcorderProfile.get;
import static java.lang.Math.exp;

public class ShadowsRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "ShadowsRenderer";

    private final ShadowsActivity mShadowsActivity;

    private FPSCounter mFPSCounter;

    /**
     * Handles to vertex and fragment shader programs
     */
    private RenderProgram mSimpleShadowProgram;
    private RenderProgram mPCFShadowProgram;
    private RenderProgram mSimpleShadowDynamicBiasProgram;
    private RenderProgram mPCFShadowDynamicBiasProgram;

    /**
     * The vertex and fragment shader to render depth map
     */
    private RenderProgram mDepthMapProgram;

    private int mActiveProgram;

    private final float[] mMVPMatrix = new float[16];
    private final float[] mMVMatrix = new float[16];
    private final float[] mNormalMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];

    private final float[] mCubeRotation = new float[16];

    /**
     * MVP matrix used at rendering shadow map for stationary objects
     */
    private final float[] mLightMvpMatrix_staticShapes = new float[16];

    /**
     * MVP matrix used at rendering shadow map for the big cube in the center
     */
    private final float[] mLightMvpMatrix_dynamicShapes = new float[16];

    /**
     * Projection matrix from point of light source
     */
    private final float[] mLightProjectionMatrix = new float[16];

    /**
     * View matrix of light source
     */
    private final float[] mLightViewMatrix = new float[16];

    /**
     * Position of light source in eye space
     */
    private final float[] mLightPosInEyeSpace = new float[16];

    /**
     * Light source position in model space
     */
    private final float[] mLightPosModel = new float[]
            {-5.0f, 9.0f, 0.0f, 1.0f};

    private float[] mActualLightPosition = new float[4];

    /**
     * Current X,Y axis rotation of center cube
     */
    private float mRotationX;
    private float mRotationY;

    /**
     * Current display sizes
     */
    private int mDisplayWidth;
    private int mDisplayHeight;

    /**
     * Current shadow map sizes
     */
    private int mShadowMapWidth;
    private int mShadowMapHeight;

    private boolean mHasDepthTextureExtension = false;

    int[] fboId;
    int[] depthTextureId;
    int[] renderTextureId;

    // Uniform locations for scene render program
    private int scene_mvpMatrixUniform;
    private int scene_mvMatrixUniform;
    private int scene_normalMatrixUniform;
    private int scene_lightPosUniform;
    private int scene_schadowProjMatrixUniform;
    private int scene_textureUniform;
    private int scene_mapStepXUniform;
    private int scene_mapStepYUniform;

    // Uniform locations for shadow render program
    private int shadow_mvpMatrixUniform;

    // Shader program attribute locations
    private int scene_positionAttribute;
    private int scene_normalAttribute;
    private int scene_colorAttribute;

    private int shadow_positionAttribute;

    private int texture_mvpMatrixUniform;
    private int texture_positionAttribute;
    private int texture_texCoordAttribute;
    private int texture_textureUniform;

    // Shapes that will be displayed
    private Cube mCube;
    private Cube mSmallCube0;
    private Cube mSmallCube1;
    private Cube mSmallCube2;
    private Cube mSmallCube3;

    private Plane mPlane;

    //try code:sri:create cubes:1
    private ArraySet<Cube> cubeArraySet;
    boolean pre = true;

    public ShadowsRenderer(final ShadowsActivity shadowsActivity) {
        mShadowsActivity = shadowsActivity;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {
        mFPSCounter = new FPSCounter();

        // Test OES_depth_texture extension
        String extensions = GLES20.glGetString(GLES20.GL_EXTENSIONS);

        if (extensions.contains("OES_depth_texture"))
            mHasDepthTextureExtension = true;

        //Set the background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        //Enable depth testing
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        GLES20.glEnable(GLES20.GL_CULL_FACE);

        //arrange scene
        //center cube
        //mCube = new Cube(new float[] {0.0f, -3.9f, 0.0f}, 3.0f, new float[] {0.0f, 0.0f, 1.0f, 1.0f});

        //4 small cubes on the ground plane
        //mSmallCube0 = new Cube(new float[] {-4.0f, -3.9f, 4.0f}, 2.0f, new float[] {1.0f, 0.0f, 0.0f, 1.0f});
        //mSmallCube1 = new Cube(new float[] {4.0f, -3.9f, 4.0f}, 2.0f, new float[] {0.0f, 1.0f, 0.0f, 1.0f});
        //mSmallCube2 = new Cube(new float[] {4.0f, -3.9f, -4.0f}, 2.0f, new float[] {0.0f, 1.0f, 1.0f, 1.0f});
        //mSmallCube3 = new Cube(new float[] {-4.0f, -3.9f, -4.0f}, 2.0f, new float[] {1.0f, 0.0f, 1.0f, 1.0f});

        //ground
        mPlane = new Plane();

        //try code:sri:create cubes:1
        //create_cubes(new float[]{4.0f, -4.0f, 0.0f, -2.0f, 2.0f}, 1.0f, new float[]{1.0f, 0.0f, 1.0f, 1.0f});
        create_mat(new float[]{-1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f, -1.0f});

        //Set view matrix from eye position
        Matrix.setLookAtM(mViewMatrix, 0,
                //eyeX, eyeY, eyeZ,
                0, 4, -12,
                //lookX, lookY, lookZ,
                0, 0, 0,
                //upX, upY, upZ
                0, 1, 0);

        //Load shaders and create program used by OpenGL for rendering
        if (!mHasDepthTextureExtension) {
            // If there is no OES_depth_texture extension depth values must be coded in rgba texture and later decoded at calculation of shadow
            mSimpleShadowProgram = new RenderProgram(R.raw.v_with_shadow,
                    R.raw.f_with_simple_shadow, mShadowsActivity);

            mPCFShadowProgram = new RenderProgram(R.raw.v_with_shadow,
                    R.raw.f_with_pcf_shadow, mShadowsActivity);

            mSimpleShadowDynamicBiasProgram = new RenderProgram(R.raw.v_with_shadow,
                    R.raw.f_with_simple_shadow_dynamic_bias, mShadowsActivity);

            mPCFShadowDynamicBiasProgram = new RenderProgram(R.raw.v_with_shadow,
                    R.raw.f_with_pcf_shadow_dynamic_bias, mShadowsActivity);

            mDepthMapProgram = new RenderProgram(R.raw.v_depth_map,
                    R.raw.f_depth_map, mShadowsActivity);
        } else {
            // OES_depth_texture is available -> shaders are simplier
            mSimpleShadowProgram = new RenderProgram(R.raw.depth_tex_v_with_shadow,
                    R.raw.depth_tex_f_with_simple_shadow, mShadowsActivity);

            mPCFShadowProgram = new RenderProgram(R.raw.depth_tex_v_with_shadow,
                    R.raw.depth_tex_f_with_pcf_shadow, mShadowsActivity);

            mSimpleShadowDynamicBiasProgram = new RenderProgram(R.raw.depth_tex_v_with_shadow,
                    R.raw.depth_tex_f_with_simple_shadow_dynamic_bias, mShadowsActivity);

            mPCFShadowDynamicBiasProgram = new RenderProgram(R.raw.depth_tex_v_with_shadow,
                    R.raw.depth_tex_f_with_pcf_shadow_dynamic_bias, mShadowsActivity);

            mDepthMapProgram = new RenderProgram(R.raw.depth_tex_v_depth_map,
                    R.raw.depth_tex_f_depth_map, mShadowsActivity);
        }


        mActiveProgram = mSimpleShadowProgram.getProgram();

        Timer timer = new Timer();
        timer.schedule(task, new Date(), 3000);
    }

    /**
     * Sets up the framebuffer and renderbuffer to render to texture
     */
    public void generateShadowFBO() {
        mShadowMapWidth = Math.round(mDisplayWidth * mShadowsActivity.getmShadowMapRatio());
        mShadowMapHeight = Math.round(mDisplayHeight * mShadowsActivity.getmShadowMapRatio());

        fboId = new int[1];
        depthTextureId = new int[1];
        renderTextureId = new int[1];

        // create a framebuffer object
        GLES20.glGenFramebuffers(1, fboId, 0);

        // create render buffer and bind 16-bit depth buffer
        GLES20.glGenRenderbuffers(1, depthTextureId, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthTextureId[0]);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, mShadowMapWidth, mShadowMapHeight);

        // Try to use a texture depth component
        GLES20.glGenTextures(1, renderTextureId, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTextureId[0]);

        // GL_LINEAR does not make sense for depth texture. However, next tutorial shows usage of GL_LINEAR and PCF. Using GL_NEAREST
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        // Remove artifact on the edges of the shadowmap
        //GLES20.glTexParameteri( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE );
        //GLES20.glTexParameteri( GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE );

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId[0]);

        if (!mHasDepthTextureExtension) {
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mShadowMapWidth, mShadowMapHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

            // specify texture as color attachment
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, renderTextureId[0], 0);

            // attach the texture to FBO depth attachment point
            // (not supported with gl_texture_2d)
            GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, depthTextureId[0]);
        } else {
            // Use a depth texture
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT, mShadowMapWidth, mShadowMapHeight, 0, GLES20.GL_DEPTH_COMPONENT, GLES20.GL_UNSIGNED_INT, null);

            // Attach the depth texture to FBO depth attachment point
            GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, renderTextureId[0], 0);
        }

        // check FBO status
        int FBOstatus = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (FBOstatus != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            Log.e(TAG, "GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
            throw new RuntimeException("GL_FRAMEBUFFER_COMPLETE failed, CANNOT use FBO");
        }
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        mDisplayWidth = width;
        mDisplayHeight = height;

        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, mDisplayWidth, mDisplayHeight);

        // Generate buffer where depth values are saved for shadow calculation
        generateShadowFBO();

        float ratio = (float) mDisplayWidth / mDisplayHeight;

        // this projection matrix is applied at rendering scene
        // in the onDrawFrame() method
        float bottom = -1.0f;
        float top = 1.0f;
        float near = 1.0f;
        float far = 100.0f;

        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, bottom, top, near, far);

        // this projection matrix is used at rendering shadow map
        Matrix.frustumM(mLightProjectionMatrix, 0, -1.1f * ratio, 1.1f * ratio, 1.1f * bottom, 1.1f * top, near, far);
        //Matrix.frustumM(mLightProjectionMatrix, 0, -ratio, ratio, bottom, top, near, far);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDrawFrame(GL10 unused) {
        // Write FPS information to console
        mFPSCounter.logFrame();

        setRenderProgram();

        // Set program handles for cube drawing.
        scene_mvpMatrixUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.MVP_MATRIX_UNIFORM);
        scene_mvMatrixUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.MV_MATRIX_UNIFORM);
        scene_normalMatrixUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.NORMAL_MATRIX_UNIFORM);
        scene_lightPosUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.LIGHT_POSITION_UNIFORM);
        scene_schadowProjMatrixUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.SHADOW_PROJ_MATRIX);
        scene_textureUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.SHADOW_TEXTURE);
        scene_positionAttribute = GLES20.glGetAttribLocation(mActiveProgram, RenderConstants.POSITION_ATTRIBUTE);
        scene_normalAttribute = GLES20.glGetAttribLocation(mActiveProgram, RenderConstants.NORMAL_ATTRIBUTE);
        scene_colorAttribute = GLES20.glGetAttribLocation(mActiveProgram, RenderConstants.COLOR_ATTRIBUTE);
        scene_mapStepXUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.SHADOW_X_PIXEL_OFFSET);
        scene_mapStepYUniform = GLES20.glGetUniformLocation(mActiveProgram, RenderConstants.SHADOW_Y_PIXEL_OFFSET);

        //shadow handles
        int shadowMapProgram = mDepthMapProgram.getProgram();
        shadow_mvpMatrixUniform = GLES20.glGetUniformLocation(shadowMapProgram, RenderConstants.MVP_MATRIX_UNIFORM);
        shadow_positionAttribute = GLES20.glGetAttribLocation(shadowMapProgram, RenderConstants.SHADOW_POSITION_ATTRIBUTE);

        //display texture program handles (for debugging depth texture)
        //texture_mvpMatrixUniform = GLES20.glGetUniformLocation(textureProgram, RenderConstants.MVP_MATRIX_UNIFORM);
        //texture_positionAttribute = GLES20.glGetAttribLocation(textureProgram, RenderConstants.POSITION_ATTRIBUTE);
        //texture_texCoordAttribute = GLES20.glGetAttribLocation(textureProgram, RenderConstants.TEX_COORDINATE);
        //texture_textureUniform = GLES20.glGetUniformLocation(textureProgram, RenderConstants.TEXTURE_UNIFORM);

        //--------------- calc values common for both renderers

        // light rotates around Y axis in every 12 seconds
        long elapsedMilliSec = SystemClock.elapsedRealtime();
        //long rotationCounter = elapsedMilliSec % 12000L;
        long rotationCounter = 12000L;

        //float lightRotationDegree = (360.0f / 12000.0f) * ((int)rotationCounter);
        float lightRotationDegree = (360.0f / 12000.0f);

        float[] rotationMatrix = new float[16];

        Matrix.setIdentityM(rotationMatrix, 0);
        Matrix.rotateM(rotationMatrix, 0, lightRotationDegree, 0.0f, 1.0f, 0.0f);

        Matrix.multiplyMV(mActualLightPosition, 0, rotationMatrix, 0, mLightPosModel, 0);

        Matrix.setIdentityM(mModelMatrix, 0);

        //Set view matrix from light source position
        /*Matrix.setLookAtM(mLightViewMatrix, 0,
        					//lightX, lightY, lightZ, 
        					mActualLightPosition[0], mActualLightPosition[1], mActualLightPosition[2],
        					//lookX, lookY, lookZ,
        					//look in direction -y
        					mActualLightPosition[0], -mActualLightPosition[1], mActualLightPosition[2],
        					//upX, upY, upZ
        					//up vector in the direction of axisY
        					-mActualLightPosition[0], 0, -mActualLightPosition[2]);*/

        //Cube rotation with touch events
        float[] cubeRotationX = new float[16];
        float[] cubeRotationY = new float[16];

        Matrix.setRotateM(cubeRotationX, 0, mRotationX, 0, 1.0f, 0);
        Matrix.setRotateM(cubeRotationY, 0, mRotationY, 1.0f, 0, 0);

        Matrix.multiplyMM(mCubeRotation, 0, cubeRotationX, 0, cubeRotationY, 0);

        //------------------------- render depth map --------------------------

        // Cull front faces for shadow generation to avoid self shadowing
        GLES20.glCullFace(GLES20.GL_FRONT);

        renderShadowMap();

        //------------------------- render scene ------------------------------

        // Cull back faces for normal render
        GLES20.glCullFace(GLES20.GL_BACK);

        renderScene();

        // Print openGL errors to console
        int debugInfo = GLES20.glGetError();

        if (debugInfo != GLES20.GL_NO_ERROR) {
            String msg = "OpenGL error: " + debugInfo;
            Log.w(TAG, msg);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void renderShadowMap() {
        // bind the generated framebuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId[0]);

        GLES20.glViewport(0, 0, mShadowMapWidth,
                mShadowMapHeight);

        // Clear color and buffers
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        // Start using the shader
        GLES20.glUseProgram(mDepthMapProgram.getProgram());

        float[] tempResultMatrix = new float[16];

        // Calculate matrices for standing objects

        // View matrix * Model matrix value is stored
        Matrix.multiplyMM(mLightMvpMatrix_staticShapes, 0, mLightViewMatrix, 0, mModelMatrix, 0);

        // Model * view * projection matrix stored and copied for use at rendering from camera point of view
        Matrix.multiplyMM(tempResultMatrix, 0, mLightProjectionMatrix, 0, mLightMvpMatrix_staticShapes, 0);
        System.arraycopy(tempResultMatrix, 0, mLightMvpMatrix_staticShapes, 0, 16);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(shadow_mvpMatrixUniform, 1, false, mLightMvpMatrix_staticShapes, 0);

        // Render all stationary shapes on scene
        mPlane.render(shadow_positionAttribute, 0, 0, true);
        //mSmallCube0.render(shadow_positionAttribute, 0, 0, true);
        //mSmallCube1.render(shadow_positionAttribute, 0, 0, true);
        //mSmallCube2.render(shadow_positionAttribute, 0, 0, true);
        //mSmallCube3.render(shadow_positionAttribute, 0, 0, true);

        for (int m = 0; m < cubeArraySet.size(); m++) {
            if (cubeArraySet != null && cubeArraySet.valueAt(m) != null) {
                cubeArraySet.valueAt(m).render(shadow_positionAttribute, 0, 0, true);
            }
        }

        // Calculate matrices for moving objects

        // Rotate the model matrix with current rotation matrix
        Matrix.multiplyMM(tempResultMatrix, 0, mModelMatrix, 0, mCubeRotation, 0);

        // View matrix * Model matrix value is stored
        Matrix.multiplyMM(mLightMvpMatrix_dynamicShapes, 0, mLightViewMatrix, 0, tempResultMatrix, 0);

        // Model * view * projection matrix stored and copied for use at rendering from camera point of view
        Matrix.multiplyMM(tempResultMatrix, 0, mLightProjectionMatrix, 0, mLightMvpMatrix_dynamicShapes, 0);
        System.arraycopy(tempResultMatrix, 0, mLightMvpMatrix_dynamicShapes, 0, 16);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(shadow_mvpMatrixUniform, 1, false, mLightMvpMatrix_dynamicShapes, 0);

        // Render all moving shapes on scene
        //mCube.render(shadow_positionAttribute, 0, 0, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void renderScene() {
        // bind default framebuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(mActiveProgram);

        GLES20.glViewport(0, 0, mDisplayWidth, mDisplayHeight);

        //pass stepsize to map nearby points properly to depth map texture - used in PCF algorithm
        GLES20.glUniform1f(scene_mapStepXUniform, (float) (1.0 / mShadowMapWidth));
        GLES20.glUniform1f(scene_mapStepYUniform, (float) (1.0 / mShadowMapHeight));

        float[] tempResultMatrix = new float[16];

        float bias[] = new float[]{
                0.5f, 0.0f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f, 0.0f,
                0.0f, 0.0f, 0.5f, 0.0f,
                0.5f, 0.5f, 0.5f, 1.0f};

        float[] depthBiasMVP = new float[16];

        //calculate MV matrix
        Matrix.multiplyMM(tempResultMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        System.arraycopy(tempResultMatrix, 0, mMVMatrix, 0, 16);

        //pass in MV Matrix as uniform
        GLES20.glUniformMatrix4fv(scene_mvMatrixUniform, 1, false, mMVMatrix, 0);

        //calculate Normal Matrix as uniform (invert transpose MV)
        Matrix.invertM(tempResultMatrix, 0, mMVMatrix, 0);
        Matrix.transposeM(mNormalMatrix, 0, tempResultMatrix, 0);

        //pass in Normal Matrix as uniform
        GLES20.glUniformMatrix4fv(scene_normalMatrixUniform, 1, false, mNormalMatrix, 0);

        //calculate MVP matrix
        Matrix.multiplyMM(tempResultMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);
        System.arraycopy(tempResultMatrix, 0, mMVPMatrix, 0, 16);

        //pass in MVP Matrix as uniform
        GLES20.glUniformMatrix4fv(scene_mvpMatrixUniform, 1, false, mMVPMatrix, 0);

        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mActualLightPosition, 0);
        //pass in light source position
        GLES20.glUniform3f(scene_lightPosUniform, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

        if (mHasDepthTextureExtension) {
            Matrix.multiplyMM(depthBiasMVP, 0, bias, 0, mLightMvpMatrix_staticShapes, 0);
            System.arraycopy(depthBiasMVP, 0, mLightMvpMatrix_staticShapes, 0, 16);
        }

        //MVP matrix that was used during depth map render
        GLES20.glUniformMatrix4fv(scene_schadowProjMatrixUniform, 1, false, mLightMvpMatrix_staticShapes, 0);

        //pass in texture where depth map is stored
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, renderTextureId[0]);
        GLES20.glUniform1i(scene_textureUniform, 0);


        // Pass uniforms for moving objects (center cube) which are different from previously used uniforms
        // - MV matrix
        // - MVP matrix
        // - Normal matrix
        // - Light MVP matrix for dynamic objects

        // Rotate the model matrix with current rotation matrix
        Matrix.multiplyMM(tempResultMatrix, 0, mModelMatrix, 0, mCubeRotation, 0);

        //calculate MV matrix
        Matrix.multiplyMM(tempResultMatrix, 0, mViewMatrix, 0, tempResultMatrix, 0);
        System.arraycopy(tempResultMatrix, 0, mMVMatrix, 0, 16);

        //pass in MV Matrix as uniform
        GLES20.glUniformMatrix4fv(scene_mvMatrixUniform, 1, false, mMVMatrix, 0);

        //calculate Normal Matrix as uniform (invert transpose MV)
        Matrix.invertM(tempResultMatrix, 0, mMVMatrix, 0);
        Matrix.transposeM(mNormalMatrix, 0, tempResultMatrix, 0);

        //pass in Normal Matrix as uniform
        GLES20.glUniformMatrix4fv(scene_normalMatrixUniform, 1, false, mNormalMatrix, 0);

        //calculate MVP matrix
        Matrix.multiplyMM(tempResultMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);
        System.arraycopy(tempResultMatrix, 0, mMVPMatrix, 0, 16);

        //pass in MVP Matrix as uniform
        GLES20.glUniformMatrix4fv(scene_mvpMatrixUniform, 1, false, mMVPMatrix, 0);

        if (mHasDepthTextureExtension) {
            Matrix.multiplyMM(depthBiasMVP, 0, bias, 0, mLightMvpMatrix_dynamicShapes, 0);
            System.arraycopy(depthBiasMVP, 0, mLightMvpMatrix_dynamicShapes, 0, 16);
        }

        //MVP matrix that was used during depth map render
        GLES20.glUniformMatrix4fv(scene_schadowProjMatrixUniform, 1, false, mLightMvpMatrix_dynamicShapes, 0);

        //mCube.render(scene_positionAttribute, scene_normalAttribute, scene_colorAttribute, false);
        //mSmallCube0.render(scene_positionAttribute, scene_normalAttribute, scene_colorAttribute, false);
        //mSmallCube1.render(scene_positionAttribute, scene_normalAttribute, scene_colorAttribute, false);
        //mSmallCube2.render(scene_positionAttribute, scene_normalAttribute, scene_colorAttribute, false);
        //mSmallCube3.render(scene_positionAttribute, scene_normalAttribute, scene_colorAttribute, false);
        mPlane.render(scene_positionAttribute, scene_normalAttribute, scene_colorAttribute, false);

        //try code:sri:create cubes:1

        for (int z = 0; z < cubeArraySet.size(); z++) {
            if (cubeArraySet != null && cubeArraySet.valueAt(z) != null) {
                cubeArraySet.valueAt(z).render(scene_positionAttribute, scene_normalAttribute, scene_colorAttribute, false);
            }
        }
    }

    /**
     * Changes render program after changes in menu
     */
    private void setRenderProgram() {
        if (mShadowsActivity.getmShadowType() < 0.5)
            if (mShadowsActivity.getmBiasType() < 0.5)
                mActiveProgram = mSimpleShadowProgram.getProgram();
            else
                mActiveProgram = mSimpleShadowDynamicBiasProgram.getProgram();
        else if (mShadowsActivity.getmBiasType() < 0.5)
            mActiveProgram = mPCFShadowProgram.getProgram();
        else
            mActiveProgram = mPCFShadowDynamicBiasProgram.getProgram();
    }

    /**
     * Returns the X rotation angle of the cube.
     *
     * @return - A float representing the rotation angle.
     */
    public float getRotationX() {
        return mRotationX;
    }

    /**
     * Sets the X rotation angle of the cube.
     */
    public void setRotationX(float rotationX) {
        mRotationX = rotationX;
    }

    /**
     * Returns the Y rotation angle of the cube.
     *
     * @return - A float representing the rotation angle.
     */
    public float getRotationY() {
        return mRotationY;
    }

    /**
     * Sets the Y rotation angle of the cube.
     */
    public void setRotationY(float rotationY) {
        mRotationY = rotationY;
    }

    //try code:sri:create cubes:1
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void create_cubes(float[] pos, float size, float[] col) {
        cubeArraySet = new ArraySet<>();
        //Random random = new Random();
        cubeArraySet.add(new Cube(new float[]{0.0f, 0.0f, 0.0f}, size, col, 10.0f));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void create_mat(float[] height) {
        if (cubeArraySet != null) {
            cubeArraySet.clear();
        }
        cubeArraySet = new ArraySet<>();
        float row = -6.0f;
        int position = 0;
        for (int l = 0; l < 6; l++) {
            //Random random = new Random();
            float col = -10.0f;
            for (int p = 0; p < 11; p++) {
                cubeArraySet.add(new Cube(new float[]{col, -5.0f, row}, 2.0f, new float[]{1.0f, 0.0f, 1.0f, 1.0f}, height[position]));
                position++;
                col += 2.0f;
            }
            row += 2.0f;
        }

    }

    TimerTask task = new TimerTask() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void run() {
            Log.d("on render", "run: ");
            Call<ResponseBody> call = RetrofitClient.getInstance().getApi().getMatData("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJfaWQiOiI1YWE2YTViNDA0YTExYjBlNmMzNGQ4ZmUiLCJyb2xlIjoiYWRtaW4iLCJpYXQiOjE1NzcxODExMzIsImV4cCI6MTU3NzE5OTEzMn0.ySVGiZQyQxR_UzCS4EBGqKjO_ruCsxHkEwoCMOocoi0");

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("23", "onResponse: " + response.code());
                    if (response.code() == 200) {
                        try {
                            String s = response.body().string();
                            JSONArray data = new JSONArray(s);
                            JSONObject innerObj = data.getJSONObject(0);
                            if (innerObj.has("reading")) {
                                JSONArray reading = innerObj.getJSONArray("reading");
                                JSONArray array1 = reading.getJSONArray(0);
                                JSONArray array2 = reading.getJSONArray(1);
                                JSONArray array3 = reading.getJSONArray(2);
                                JSONArray array4 = reading.getJSONArray(3);
                                JSONArray array5 = reading.getJSONArray(4);
                                JSONArray array6 = reading.getJSONArray(5);
                                Log.d(TAG, "onResponse: " + array1 + " " + array1.getInt(0));
                                double x1, x2, x3, x4, x5, x6, x;
                                //calculating reason wise weight.
                                //TR
                                int sum1 = array2.getInt(8) + array3.getInt(8) + array4.getInt(8) +
                                        array2.getInt(9) + array3.getInt(9) + array4.getInt(9) +
                                        array2.getInt(10) + array3.getInt(10) + array4.getInt(10);
                                x1 = exp((sum1 - 851) / 317.6);
                                Log.d(TAG, "sum1:" + sum1 + " x1=" + x1);

                                //DR
                                int sum2 = array3.getInt(8) + array4.getInt(8) + array5.getInt(8) +
                                        array3.getInt(9) + array4.getInt(9) + array5.getInt(9) +
                                        array3.getInt(10) + array4.getInt(10) + array5.getInt(10);
                                x2 = exp((sum2 - 756) / 269);
                                Log.d(TAG, "sum2:" + sum2 + " x2=" + x2);

                                //TL
                                int sum3 = array2.getInt(0) + array3.getInt(0) + array4.getInt(0) +
                                        array2.getInt(1) + array3.getInt(1) + array4.getInt(1) +
                                        array2.getInt(2) + array3.getInt(2) + array4.getInt(2) +
                                        array2.getInt(3) + array3.getInt(3) + array4.getInt(3);
                                x3 = exp((sum3 - 684) / 245);
                                Log.d(TAG, "sum3:" + sum3 + " x3=" + x3);

                                //DL
                                int sum4 = array3.getInt(0) + array4.getInt(0) + array5.getInt(0) +
                                        array3.getInt(1) + array4.getInt(1) + array5.getInt(1) +
                                        array3.getInt(3) + array4.getInt(3) + array5.getInt(3) +
                                        array3.getInt(4) + array4.getInt(4) + array5.getInt(4);
                                x4 = exp((sum4 - 406) / 160);
                                Log.d(TAG, "sum4:" + sum4 + " x4=" + x4);

                                //TM
                                int sum5 = array2.getInt(5) + array3.getInt(5) + array4.getInt(5) +
                                        array2.getInt(6) + array3.getInt(6) + array4.getInt(6) +
                                        array2.getInt(7) + array3.getInt(7) + array4.getInt(7) +
                                        array2.getInt(8) + array3.getInt(8) + array4.getInt(8);
                                x5 = exp((sum5 - 1120) / 463);
                                Log.d(TAG, "sum5:" + sum5 + " x5=" + x5);

                                //DM
                                int sum6 = array4.getInt(5) + array5.getInt(5) + array6.getInt(5) +
                                        array4.getInt(6) + array5.getInt(6) + array6.getInt(6) +
                                        array4.getInt(7) + array5.getInt(7) + array6.getInt(7) +
                                        array4.getInt(8) + array5.getInt(8) + array6.getInt(8);
                                x6 = exp((sum6 - 1120) / 463);
                                Log.d(TAG, "sum6:" + sum6 + " x6=" + x6);

                                int totSum = 0;

                                for (int q = 0; q < 11; q++) {
                                    totSum += array1.getInt(q);
                                }
                                Log.d("1", "totSum:"+totSum);
                                for (int w = 0; w < 11; w++) {
                                    totSum += array2.getInt(w);
                                }
                                Log.d("2", "totSum:"+totSum);
                                for (int e = 0; e < 11; e++) {
                                    totSum += array3.getInt(e);
                                }
                                Log.d("3", "totSum:"+totSum);
                                for (int r = 0; r < 11; r++) {
                                    totSum += array4.getInt(r);
                                }
                                Log.d("4", "totSum:"+totSum);
                                for (int t = 0; t < 11; t++) {
                                    totSum += array5.getInt(t);
                                }
                                Log.d("5", "totSum:"+totSum);
                                for (int y = 0; y < 11; y++) {
                                    totSum += array6.getInt(y);
                                }
                                Log.d("6", "totSum:"+totSum);
                                x=exp((totSum-993)/398);
                                Log.d(TAG, "totSum:"+totSum+" x="+x);


                                 /* //TR
                                x1 = (array2.getInt(8) + array3.getInt(8) + array4.getInt(8) +
                                        array2.getInt(9) + array3.getInt(9) + array4.getInt(9) +
                                        array2.getInt(10) + array3.getInt(10) + array4.getInt(10) - 1131) / 93;

                                //DR
                                x2 = (array3.getInt(8) + array4.getInt(8) + array5.getInt(8) +
                                        array3.getInt(9) + array4.getInt(9) + array5.getInt(9) +
                                        array3.getInt(10) + array4.getInt(10) + array5.getInt(10) - 810) / 80;

                                //TL
                                x3 = (array2.getInt(0) + array3.getInt(0) + array4.getInt(0) +
                                        array2.getInt(1) + array3.getInt(1) + array4.getInt(1) +
                                        array2.getInt(2) + array3.getInt(2) + array4.getInt(2) +
                                        array2.getInt(3) + array3.getInt(3) + array4.getInt(3) - 658) / 84;

                                //DL
                                x4 = (array3.getInt(0) + array4.getInt(0) + array5.getInt(0) +
                                        array3.getInt(1) + array4.getInt(1) + array5.getInt(1) +
                                        array3.getInt(3) + array4.getInt(3) + array5.getInt(3) +
                                        array3.getInt(4) + array4.getInt(4) + array5.getInt(4) - 430) / 49;

                                //TM
                                x5 = (array2.getInt(5) + array3.getInt(5) + array4.getInt(5) +
                                        array2.getInt(6) + array3.getInt(6) + array4.getInt(6) +
                                        array2.getInt(7) + array3.getInt(7) + array4.getInt(7) +
                                        array2.getInt(8) + array3.getInt(8) + array4.getInt(8) - 1164) / 137;

                                //DM
                                x6 = (array4.getInt(5) + array5.getInt(5) + array6.getInt(5) +
                                        array4.getInt(6) + array5.getInt(6) + array6.getInt(6) +
                                        array4.getInt(7) + array5.getInt(7) + array6.getInt(7) +
                                        array4.getInt(8) + array5.getInt(8) + array6.getInt(8) - 1164) / 137;
                                Log.d(TAG, "onResponse: " + "X1:" + x1 + " X2:" + x2 + " X3:" + x3 + " X4:" + x4 + " X5:" + x5 + " X6:" + x6);

                                //Calibrate x1,x3,x4,x5,x6;
                                x1 = calibratex1(x1);
                                x3 = calibratex3(x3);
                                x4 = calibratex4(x4);
                                x5 = calibratex6(x5);
                                x6 = calibratex6(x6);
                                Log.d(TAG, "after calibration: " + "X1:" + x1 + " X2:" + x2 + " X3:" + x3 + " X4:" + x4 + " X5:" + x5 + " X6:" + x6);*/

                                 /*create_mat(new float[]{array6.getInt(10), array6.getInt(9), array6.getInt(8), array6.getInt(7), array6.getInt(6), array6.getInt(5), array6.getInt(4), array6.getInt(3), array6.getInt(2), array6.getInt(1), array6.getInt(0),
                                        array5.getInt(10), array5.getInt(9), array5.getInt(8), array5.getInt(7), array5.getInt(6), array5.getInt(5), array5.getInt(4), array5.getInt(3), array5.getInt(2), array5.getInt(1), array5.getInt(0),
                                        array4.getInt(10), array4.getInt(9), array4.getInt(8), array4.getInt(7), array4.getInt(6), array4.getInt(5),array4.getInt(4), array4.getInt(3), array4.getInt(2), array4.getInt(1), array4.getInt(0),
                                        array3.getInt(10), array3.getInt(9), array3.getInt(8), array3.getInt(7), array3.getInt(6), array3.getInt(5),array3.getInt(4), array3.getInt(3), array3.getInt(2), array3.getInt(1), array3.getInt(0),
                                        array2.getInt(10), array2.getInt(9), array2.getInt(8), array2.getInt(7), array2.getInt(6), array2.getInt(5),array2.getInt(4), array2.getInt(3), array2.getInt(2), array2.getInt(1), array2.getInt(0),
                                        array1.getInt(10), array1.getInt(9), array1.getInt(8), array1.getInt(7), array1.getInt(6), array1.getInt(5),array1.getInt(4), array1.getInt(3), array1.getInt(2), array1.getInt(1), array1.getInt(0)});*/
                                create_mat(new float[]{0.0f, (float) x4, (float) x4, (float) x4, (float) x6, (float) x6, (float) x6, (float) x2, (float) x2, (float) x2, 0.0f,
                                        0.0f, (float) x4, (float) x4, (float) x4, (float) x6, (float) x6, (float) x6, (float) x2, (float) x2, (float) x2, 0.0f,
                                        0.0f, (float) x4, (float) x4, (float) x4, (float) x6, (float) x6, (float) x6, (float) x2, (float) x2, (float) x2, 0.0f,
                                        0.0f, (float) x3, (float) x3, (float) x3, (float) x5, (float) x5, (float) x5, (float) x1, (float) x1, (float) x1, 0.0f,
                                        0.0f, (float) x3, (float) x3, (float) x3, (float) x5, (float) x5, (float) x5, (float) x1, (float) x1, (float) x1, 0.0f,
                                        0.0f, (float) x3, (float) x3, (float) x3, (float) x5, (float) x5, (float) x5, (float) x1, (float) x1, (float) x1, 0.0f
                                });
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d("12", "onFailure: ");
                }
            });

        }
    };

    public double calibratex1(double x) {
        if (-3 <= x && x < 0) {
            return 1;
        } else if (x >= 0 && x <= 6) {
            return x - 1;
        } else if (x > 6) {
            return x + 1;
        } else {
            return -2;
        }
    }

    public double calibratex3(double x) {
        if (-1 < x && x <= 1.5) {
            return x + 1;
        } else if (x > 1.5 && x <= 5) {
            return x - 1;
        } else if (x > 5) {
            return x + 1;
        } else {
            return -2;
        }
    }

    public double calibratex4(double x) {
        if (-1 < x && x <= 2.5) {
            return x + 1;
        } else if (x > 2.5 && x <= 5) {
            return x - 1;
        } else if (x > 5) {
            return x + 1;
        } else {
            return -2;
        }
    }

    public double calibratex6(double x) {
        if (-1 < x && x <= 1.5) {
            return x + 1;
        } else if (x > 1.5 && x <= 6) {
            return x - 1;
        } else if (x > 6) {
            return x + 1;
        } else {
            return -2;
        }
    }
}
