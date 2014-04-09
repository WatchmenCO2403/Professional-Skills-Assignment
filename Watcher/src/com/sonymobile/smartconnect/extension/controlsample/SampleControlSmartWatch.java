/*
Copyright (c) 2011, Sony Ericsson Mobile Communications AB
Copyright (c) 2011-2013, Sony Mobile Communications AB

 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
 list of conditions and the following disclaimer.

 * Redistributions in binary form must reproduce the above copyright notice,
 this list of conditions and the following disclaimer in the documentation
 and/or other materials provided with the distribution.

 * Neither the name of the Sony Ericsson Mobile Communications AB / Sony Mobile
 Communications AB nor the names of its contributors may be used to endorse or promote
 products derived from this software without specific prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.sonymobile.smartconnect.extension.controlsample;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;

/**
 * The sample control for SmartWatch handles the control on the accessory.
 * This class exists in one instance for every supported host application that
 * we have registered to
 */
class SampleControlSmartWatch extends ControlExtension {

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.RGB_565;

    private static final int ANIMATION_X_POS = 46;

    private static final int ANIMATION_Y_POS = 46;

    private static final int ANIMATION_DELTA_MS = 500;

    private Handler mHandler;

    private boolean mIsShowingAnimation = false;

    private boolean mIsVisible = false;

    private Animation mAnimation = null;

    private final int width;

    private final int height;

    /**
     * Create sample control.
     *
     * @param hostAppPackageName Package name of host application.
     * @param context The context.
     * @param handler The handler to use
     */
    SampleControlSmartWatch(final String hostAppPackageName, final Context context,
            Handler handler) {
        super(context, hostAppPackageName);
        if (handler == null) {
            throw new IllegalArgumentException("handler == null");
        }
        mHandler = handler;
        width = getSupportedControlWidth(context);
        height = getSupportedControlHeight(context);
    }

    /**
     * Get supported control width.
     *
     * @param context The context.
     * @return the width.
     */
    public static int getSupportedControlWidth(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.smart_watch_control_width);
    }

    /**
     * Get supported control height.
     *
     * @param context The context.
     * @return the height.
     */
    public static int getSupportedControlHeight(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.smart_watch_control_height);
    }

    @Override
    public void onDestroy() {

        Log.d(SampleExtensionService.LOG_TAG, "SampleControlSmartWatch onDestroy");
        stopAnimation();
        mHandler = null;
    };

    @Override
    public void onStart() {
        // Nothing to do. Animation is handled in onResume.
    }

    @Override
    public void onStop() {
        // Nothing to do. Animation is handled in onPause.
    }

    @Override
    public void onResume() {
        mIsVisible = true;

        Log.d(SampleExtensionService.LOG_TAG, "Starting animation");

        // Animation not showing. Show animation.
        mIsShowingAnimation = true;
        mAnimation = new Animation();
        mAnimation.run();
    }

    @Override
    public void onPause() {
        Log.d(SampleExtensionService.LOG_TAG, "Stopping animation");
        mIsVisible = false;

        if (mIsShowingAnimation) {
            stopAnimation();
        }
    }

    /**
     * Stop showing animation on control.
     */
    public void stopAnimation() {
        // Stop animation on accessory
        if (mAnimation != null) {
            mAnimation.stop();
            mHandler.removeCallbacks(mAnimation);
            mAnimation = null;
        }
        mIsShowingAnimation = false;

        // If the control is visible then stop it
        if (mIsVisible) {
            stopRequest();
        }
    }

    @Override
    public void onTouch(final ControlTouchEvent event) {
        Log.d(SampleExtensionService.LOG_TAG, "onTouch() " + event.getAction());
        if (event.getAction() == Control.Intents.TOUCH_ACTION_RELEASE) {
            if (mIsShowingAnimation) {
                Log.d(SampleExtensionService.LOG_TAG, "Stopping animation");

                // Stop the animation
                stopAnimation();
            }
        }
    }

    /**
     * The animation class shows an animation on the accessory. The animation
     * runs until mHandler.removeCallbacks has been called.
     */
    private class Animation implements Runnable {
        private int mIndex = 1;

        private final Bitmap mBackground;

        private boolean mIsStopped = false;

        /**
         * Create animation.
         */
        Animation() {
            mIndex = 1;

            // Extract the last part of the host application package name.
            String packageName = mHostAppPackageName
                    .substring(mHostAppPackageName.lastIndexOf(".") + 1);

            // Create background bitmap for animation.
            mBackground = Bitmap.createBitmap(width, height, BITMAP_CONFIG);
            // Set default density to avoid scaling.
            mBackground.setDensity(DisplayMetrics.DENSITY_DEFAULT);

            LinearLayout root = new LinearLayout(mContext);
            root.setLayoutParams(new LayoutParams(width, height));

            LinearLayout sampleLayout = (LinearLayout)LinearLayout.inflate(mContext,
                    R.layout.sample_control, root);
            ((TextView)sampleLayout.findViewById(R.id.sample_control_text)).setText(packageName);
            sampleLayout.measure(width, height);
            sampleLayout.layout(0, 0, sampleLayout.getMeasuredWidth(),
                    sampleLayout.getMeasuredHeight());

            Canvas canvas = new Canvas(mBackground);
            sampleLayout.draw(canvas);

            showBitmap(mBackground);
        }

        /**
         * Stop the animation.
         */
        public void stop() {
            mIsStopped = true;
        }

        @Override
        public void run() {
            int resourceId;
            switch (mIndex) {
                case 1:
                    resourceId = R.drawable.generic_anim_1_icn;
                    break;
                case 2:
                    resourceId = R.drawable.generic_anim_2_icn;
                    break;
                case 3:
                    resourceId = R.drawable.generic_anim_3_icn;
                    break;
                case 4:
                    resourceId = R.drawable.generic_anim_2_icn;
                    break;
                default:
                    Log.e(SampleExtensionService.LOG_TAG, "mIndex out of bounds: " + mIndex);
                    resourceId = R.drawable.generic_anim_1_icn;
                    break;
            }
            mIndex++;
            if (mIndex > 4) {
                mIndex = 1;
            }

            if (!mIsStopped) {
                updateAnimation(resourceId);
            }
            if (mHandler != null && !mIsStopped) {
                mHandler.postDelayed(this, ANIMATION_DELTA_MS);
            }
        }

        /**
         * Update the animation on the accessory. Only updates the part of the
         * screen which contains the animation.
         *
         * @param resourceId The new resource to show.
         */
        private void updateAnimation(int resourceId) {
            Bitmap animation = BitmapFactory.decodeResource(mContext.getResources(), resourceId,
                    mBitmapOptions);

            // Create a bitmap for the part of the screen that needs updating.
            Bitmap bitmap = Bitmap.createBitmap(animation.getWidth(), animation.getHeight(),
                    BITMAP_CONFIG);
            bitmap.setDensity(DisplayMetrics.DENSITY_DEFAULT);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint();
            Rect src = new Rect(ANIMATION_X_POS, ANIMATION_Y_POS, ANIMATION_X_POS
                    + animation.getWidth(), ANIMATION_Y_POS + animation.getHeight());
            Rect dst = new Rect(0, 0, animation.getWidth(), animation.getHeight());

            // Add first the background and then the animation.
            canvas.drawBitmap(mBackground, src, dst, paint);
            canvas.drawBitmap(animation, 0, 0, paint);

            showBitmap(bitmap, ANIMATION_X_POS, ANIMATION_Y_POS);
        }
    };

}
