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
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.sonyericsson.extras.liveware.aef.control.Control;
import com.sonyericsson.extras.liveware.extension.util.ExtensionUtils;
import com.sonyericsson.extras.liveware.extension.util.control.ControlExtension;
import com.sonyericsson.extras.liveware.extension.util.control.ControlObjectClickEvent;
import com.sonyericsson.extras.liveware.extension.util.control.ControlTouchEvent;
import com.sonyericsson.extras.liveware.extension.util.control.ControlView;
import com.sonyericsson.extras.liveware.extension.util.control.ControlView.OnClickListener;
import com.sonyericsson.extras.liveware.extension.util.control.ControlViewGroup;

/**
 * The sample control for SmartWatch handles the control on the accessory. This
 * class exists in one instance for every supported host application that we
 * have registered to
 */
class SampleControlSmartWatch2 extends ControlExtension {

    private static final int ANIMATION_DELTA_MS = 500;
    private static final int SELECT_TOGGLER_MS = 2000;
    private static final int MENU_ITEM_0 = 0;
    private static final int MENU_ITEM_1 = 1;
    private static final int MENU_ITEM_2 = 2;
    private static final int MENU_ITEM_3 = 3;
    private static final int MENU_ITEM_4 = 4;
    private static final int MENU_ITEM_5 = 5;

    private Handler mHandler;

    private boolean mIsShowingAnimation = false;

    private Animation mAnimation = null;

    private ControlViewGroup mLayout = null;

    private boolean mTextMenu = false;
    Bundle[] mMenuItemsText = new Bundle[3];
    Bundle[] mMenuItemsIcons = new Bundle[3];

    /**
     * Create sample control.
     *
     * @param hostAppPackageName Package name of host application.
     * @param context The context.
     * @param handler The handler to use
     */
    SampleControlSmartWatch2(final String hostAppPackageName, final Context context,
            Handler handler) {
        super(context, hostAppPackageName);
        if (handler == null) {
            throw new IllegalArgumentException("handler == null");
        }
        mHandler = handler;
        setupClickables(context);
        initializeMenus();
    }

    private void initializeMenus() {
        mMenuItemsText[0] = new Bundle();
        mMenuItemsText[0].putInt(Control.Intents.EXTRA_MENU_ITEM_ID, MENU_ITEM_0);
        mMenuItemsText[0].putString(Control.Intents.EXTRA_MENU_ITEM_TEXT, "Item 1");
        mMenuItemsText[1] = new Bundle();
        mMenuItemsText[1].putInt(Control.Intents.EXTRA_MENU_ITEM_ID, MENU_ITEM_1);
        mMenuItemsText[1].putString(Control.Intents.EXTRA_MENU_ITEM_TEXT, "Item 2");
        mMenuItemsText[2] = new Bundle();
        mMenuItemsText[2].putInt(Control.Intents.EXTRA_MENU_ITEM_ID, MENU_ITEM_2);
        mMenuItemsText[2].putString(Control.Intents.EXTRA_MENU_ITEM_TEXT, "Item 3");

        mMenuItemsIcons[0] = new Bundle();
        mMenuItemsIcons[0].putInt(Control.Intents.EXTRA_MENU_ITEM_ID, MENU_ITEM_3);
        mMenuItemsIcons[0].putString(Control.Intents.EXTRA_MENU_ITEM_ICON,
                ExtensionUtils.getUriString(mContext, R.drawable.actions_call));
        mMenuItemsIcons[1] = new Bundle();
        mMenuItemsIcons[1].putInt(Control.Intents.EXTRA_MENU_ITEM_ID, MENU_ITEM_4);
        mMenuItemsIcons[1].putString(Control.Intents.EXTRA_MENU_ITEM_ICON,
                ExtensionUtils.getUriString(mContext, R.drawable.actions_reply));
        mMenuItemsIcons[2] = new Bundle();
        mMenuItemsIcons[2].putInt(Control.Intents.EXTRA_MENU_ITEM_ID, MENU_ITEM_5);
        mMenuItemsIcons[2].putString(Control.Intents.EXTRA_MENU_ITEM_ICON,
                ExtensionUtils.getUriString(mContext, R.drawable.actions_view_in_phone));
    }

    /**
     * Get supported control width.
     *
     * @param context The context.
     * @return the width.
     */
    public static int getSupportedControlWidth(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_width);
    }

    /**
     * Get supported control height.
     *
     * @param context The context.
     * @return the height.
     */
    public static int getSupportedControlHeight(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.smart_watch_2_control_height);
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
        Log.d(SampleExtensionService.LOG_TAG, "Starting animation");

        Bundle b1 = new Bundle();
        b1.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.sample_control_text_1);
        b1.putString(Control.Intents.EXTRA_TEXT, "1");

        Bundle b2 = new Bundle();
        b2.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.sample_control_text_2);
        b2.putString(Control.Intents.EXTRA_TEXT, "2");

        Bundle b3 = new Bundle();
        b3.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.sample_control_text_3);
        b3.putString(Control.Intents.EXTRA_TEXT, "3");

        Bundle b4 = new Bundle();
        b4.putInt(Control.Intents.EXTRA_LAYOUT_REFERENCE, R.id.sample_control_text_4);
        b4.putString(Control.Intents.EXTRA_TEXT, "4");

        Bundle[] data = new Bundle[4];

        data[0] = b1;
        data[1] = b2;
        data[2] = b3;
        data[3] = b4;

        showLayout(R.layout.sample_control_2, data);

        startAnimation();
    }

    @Override
    public void onPause() {
        Log.d(SampleExtensionService.LOG_TAG, "Stopping animation");
        stopAnimation();
    }

    private void toggleAnimation() {
        if (mIsShowingAnimation) {
            stopAnimation();
        }
        else {
            startAnimation();
        }
    }

    /**
     * Start showing animation on control.
     */
    private void startAnimation() {
        if (!mIsShowingAnimation) {
            mIsShowingAnimation = true;
            mAnimation = new Animation();
            mAnimation.run();
        }
    }

    /**
     * Stop showing animation on control.
     */
    private void stopAnimation() {
        if (mIsShowingAnimation) {
            // Stop animation on accessory
            if (mAnimation != null) {
                mAnimation.stop();
                mHandler.removeCallbacks(mAnimation);
                mAnimation = null;
            }
            mIsShowingAnimation = false;
        }
    }

    @Override
    public void onTouch(final ControlTouchEvent event) {
        Log.d(SampleExtensionService.LOG_TAG, "onTouch() " + event.getAction());
        if (event.getAction() == Control.Intents.TOUCH_ACTION_RELEASE) {
            Log.d(SampleExtensionService.LOG_TAG, "Toggling animation");
            toggleAnimation();
        }
    }

    @Override
    public void onObjectClick(final ControlObjectClickEvent event) {
        Log.d(SampleExtensionService.LOG_TAG, "onObjectClick() " + event.getClickType());
        if (event.getLayoutReference() != -1) {
            mLayout.onClick(event.getLayoutReference());
        }
    }

    @Override
    public void onKey(final int action, final int keyCode, final long timeStamp) {
        Log.d(SampleExtensionService.LOG_TAG, "onKey()");
        if (action == Control.Intents.KEY_ACTION_RELEASE
                && keyCode == Control.KeyCodes.KEYCODE_OPTIONS) {
            toggleMenu();
        }
        else if (action == Control.Intents.KEY_ACTION_RELEASE
                && keyCode == Control.KeyCodes.KEYCODE_BACK) {
            Log.d(SampleExtensionService.LOG_TAG, "onKey() - back button intercepted.");
        }
    }

    @Override
    public void onMenuItemSelected(final int menuItem) {
        Log.d(SampleExtensionService.LOG_TAG, "onMenuItemSelected() - menu item " + menuItem);
        if (menuItem == MENU_ITEM_0) {
            clearDisplay();
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    onResume();
                }
            }, 1000);
        }
    }

    private void toggleMenu() {
        if (mTextMenu) {
            showMenu(mMenuItemsIcons);
        }
        else
        {
            showMenu(mMenuItemsText);
        }
        mTextMenu = !mTextMenu;
    }

    private void setupClickables(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.sample_control_2
                , null);
        mLayout = (ControlViewGroup) parseLayout(layout);
        if (mLayout != null) {
            ControlView upperLeft = mLayout.findViewById(R.id.sample_control_object_1);
            upperLeft.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick() {
                    sendImage(R.id.sample_control_object_1, R.drawable.left_top_selected);
                    mHandler.postDelayed(new SelectToggler(R.id.sample_control_object_1,
                            R.drawable.left_top), SELECT_TOGGLER_MS);
                }
            });
            ControlView upperRight = mLayout.findViewById(R.id.sample_control_object_2);
            upperRight.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick() {
                    sendImage(R.id.sample_control_object_2, R.drawable.right_top_selected);
                    mHandler.postDelayed(new SelectToggler(R.id.sample_control_object_2,
                            R.drawable.right_top), SELECT_TOGGLER_MS);
                }
            });
            ControlView bottomLeft = mLayout.findViewById(R.id.sample_control_object_3);
            bottomLeft.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick() {
                    sendImage(R.id.sample_control_object_3, R.drawable.left_bottom_selected);
                    mHandler.postDelayed(new SelectToggler(R.id.sample_control_object_3,
                            R.drawable.left_bottom), SELECT_TOGGLER_MS);
                }
            });
            ControlView bottomRight = mLayout.findViewById(R.id.sample_control_object_4);
            bottomRight.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick() {
                    sendImage(R.id.sample_control_object_4, R.drawable.right_bottom_selected);
                    mHandler.postDelayed(new SelectToggler(R.id.sample_control_object_4,
                            R.drawable.right_bottom), SELECT_TOGGLER_MS);
                }
            });
        }
    }

    private class SelectToggler implements Runnable {

        private int mLayoutReference;
        private int mResourceId;

        SelectToggler(int layoutReference, int resourceId) {
            mLayoutReference = layoutReference;
            mResourceId = resourceId;
        }

        @Override
        public void run() {
            sendImage(mLayoutReference, mResourceId);
        }

    }

    /**
     * The animation class shows an animation on the accessory. The animation
     * runs until mHandler.removeCallbacks has been called.
     */
    private class Animation implements Runnable {

        private int mIndex = 1;
        private boolean mIsStopped = false;

        /**
         * Create animation.
         */
        Animation() {
            mIndex = 1;
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
            sendImage(R.id.animatedImage, resourceId);
        }
    };

}
