package com.example.user.cameraapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


/**
 * Created by User on 8/23/2017.
 */

public class FilteredImageView extends View {

    Bitmap mBitmap = null;

    public FilteredImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if( mBitmap!=null ) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
        }
    }

    void setBitmap( Bitmap bitmap ) {
        mBitmap = bitmap;
    }
}
