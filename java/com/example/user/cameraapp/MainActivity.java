package com.example.user.cameraapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    SurfaceView preview;
    Camera mCamera;
    Camera.PreviewCallback mPreviewCallback;
    int mPreviewWidth,mPreviewHeight;
    ByteBuffer mPreviewBuffer;
    Bitmap mBitmap;
    FilteredImageView mImageView;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        preview = (SurfaceView) findViewById(R.id.preview);
        preview.getHolder().addCallback(this);

        mImageView = (FilteredImageView)findViewById(R.id.filteredView);

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    //public native String stringFromJNI();

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        startPreview(surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        startPreview(surfaceHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        safeCameraOpen(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCameraAndPreview();
    }

    private boolean safeCameraOpen(int id) {
        boolean qOpened = false;
        try {
            releaseCameraAndPreview();
            mCamera = Camera.open(id);
            qOpened = (mCamera != null);
        } catch (Exception e) {
            Log.e("Joony", "failed to open Camera");
        }
        return qOpened;
    }

    private void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private void startPreview(SurfaceHolder holder) {
        if (mCamera == null) safeCameraOpen(0);

        mCamera.stopPreview();
        mCamera.setDisplayOrientation(90);
        Camera.Parameters p = mCamera.getParameters();
        p.setPreviewSize(640,480);
        mCamera.setParameters(p);
        p = mCamera.getParameters();
        mPreviewWidth  = p.getPreviewSize().width;
        mPreviewHeight = p.getPreviewSize().height;
        mPreviewBuffer = ByteBuffer.allocateDirect(mPreviewWidth * mPreviewHeight *3/2);

        mBitmap = Bitmap.createBitmap(mPreviewWidth, mPreviewHeight, Bitmap.Config.ARGB_8888);

        if( mImageView!=null )
            mImageView.setBitmap(mBitmap);
        try
        {
            mCamera.setPreviewDisplay(holder);
        } catch (IOException e)
        {         // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mPreviewCallback = new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] bytes, Camera camera) {
//                Log.e("Tag","Sending image");
                mPreviewBuffer.rewind();
                processImage(mPreviewBuffer,mPreviewWidth, mPreviewHeight,mBitmap);
// Force the custom view redraw
                mImageView.invalidate();
                camera.addCallbackBuffer(mPreviewBuffer.array());
            }
        };

        mCamera.addCallbackBuffer(mPreviewBuffer.array());
        mCamera.setPreviewCallbackWithBuffer(mPreviewCallback);
        mCamera.startPreview();
    }

    public native String processImage(ByteBuffer buffer, int width, int height, Bitmap mBitmap);
}







