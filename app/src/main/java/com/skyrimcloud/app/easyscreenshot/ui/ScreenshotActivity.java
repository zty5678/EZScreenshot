package com.skyrimcloud.app.easyscreenshot.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;


import com.skyrimcloud.app.easyscreenshot.service.MyService;
import com.skyrimcloud.app.easyscreenshot.utils.LogUtil;
import com.skyrimcloud.app.easyscreenshot.utils.UIHandler;

import java.util.concurrent.atomic.AtomicInteger;

public class ScreenshotActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.LOGI("");

        handleIntent(getIntent());
    }

    public void closeActivity() {
        this.finish();
    }

    static AtomicInteger activeCount = new AtomicInteger(0);

    public static boolean isScreenshotActivityRunning() {
        return activeCount.get() == 1;
    }
    public void onInstanceActive() {
        activeCount.set(1);
    }

    public void onInstanceInactive() {
        activeCount.set(0);
    }

    @Override
    public void onStart() {
        super.onStart();
        onInstanceActive();
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.LOGI("");
        closeActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.LOGI("");
        onInstanceInactive();

    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.LOGI("");
    }


    private void handleIntent(Intent intent) {
        if (intent != null) {
            LogUtil.LOGI("intent not null");

            if (intent.hasExtra(BUNDLE_KEY_START_SCREENSHOT)) {
                LogUtil.LOGI("before post createScreenCaptureIntent runnable");

                createScreenCaptureIntent();

            }
        } else {
            LogUtil.LOGE("intent is null");

        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtil.LOGI("");
        setIntent(intent);
        handleIntent(intent);
    }

    public static final String BUNDLE_KEY_START_SCREENSHOT = "BUNDLE_KEY_START_SCREENSHOT";


    public void createScreenCaptureIntent() {
        LogUtil.LOGI("startActivityForResult");
        MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent intent = projectionManager.createScreenCaptureIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, PERMISSION_CODE_MEDIA_PROJECTION);


    }

    final int PERMISSION_CODE_MEDIA_PROJECTION = 100;

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        LogUtil.LOGI("requestCode="+requestCode+",resultCode="+resultCode+",");

        switch (requestCode) {
            case PERMISSION_CODE_MEDIA_PROJECTION: {
                LogUtil.LOGI("is result OK?" + (resultCode == RESULT_OK));
                if (resultCode == RESULT_OK) {


                    UIHandler.get().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MyService.startScreenShot(ScreenshotActivity.this, resultCode, data);

                        }
                    },100);
                    closeActivity();
                    onInstanceInactive();
                } else {
                    closeActivity();//说明用户点了取消，这时候也要关闭activity，否则就要多按一次返回键
                    onInstanceInactive();
                }

                break;
            }
            default:{
                closeActivity();
                onInstanceInactive();
            }
        }
    }


    public static Intent getScreenshotIntent(Context context) {

        Intent intent = new Intent(context, ScreenshotActivity.class);
        intent.putExtra(ScreenshotActivity.BUNDLE_KEY_START_SCREENSHOT, true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                        | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP

        );

        return intent;

    }
}
