package com.skyrimcloud.app.easyscreenshot.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;


import com.skyrimcloud.app.easyscreenshot.App;
import com.skyrimcloud.app.easyscreenshot.R;
import com.skyrimcloud.app.easyscreenshot.bean.BusEvents;
import com.skyrimcloud.app.easyscreenshot.ui.ScreenshotActivity;
import com.skyrimcloud.app.easyscreenshot.ui.SettingActivity;
import com.skyrimcloud.app.easyscreenshot.utils.BusProvider;
import com.skyrimcloud.app.easyscreenshot.utils.app.SettingHelper;
import com.skyrimcloud.app.easyscreenshot.utils.app.ShakeEventListener;
import com.skyrimcloud.app.easyscreenshot.utils.LogUtil;
import com.skyrimcloud.app.easyscreenshot.utils.UIHandler;
import com.skyrimcloud.app.easyscreenshot.utils.Utils;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName:
 * @Description:
 * @date 2015/11/9 19:11
 */
public class MyService extends Service {
    public static final String ACTION_BEGIN_SCREENSHOT = "ACTION_BEGIN_SCREENSHOT";/*通知栏的截屏按钮*/

    public static final String ACTION_EXIT_SCREENSHOT_SERVICE = "ACTION_EXIT_SCREENSHOT_SERVICE";/*退出截屏服务*/
    public static final String ACTION_PAUSE_SCREENSHOT_SERVICE = "ACTION_PAUSE_SCREENSHOT_SERVICE";/*暂停截屏服务*/
    public static final String ACTION_RESUME_SCREENSHOT_SERVICE = "ACTION_RESUME_SCREENSHOT_SERVICE";/*恢复截屏服务*/
    public static final String ACTION_HANDLE_SCREENSHOT_RESULT = "ACTION_HANDLE_SCREENSHOT_RESULT";/*让service来处理截屏的结果*/
    public static final String ACTION_FIRST_START_SERVICE = "ACTION_FIRST_START_SERVICE";/*第一次启动service*/


    private static final String EXTRA_SCREENSHOT_RESULT_CODE = "EXTRA_SCREENSHOT_RESULT_CODE";
    private static final String EXTRA_SCREENSHOT_RESULT_INTENT = "EXTRA_SCREENSHOT_RESULT_INTENT";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BusProvider.get().register(this);

        registerTheScreenListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        BusProvider.get().unregister(this);

        unregisterTheScreenListener();
    }

    BroadcastReceiver screenReceiver;

    private void registerTheScreenListener() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        screenReceiver = new ScreenReceiver();
        registerReceiver(screenReceiver, filter);
    }

    private void unregisterTheScreenListener() {
        if (screenReceiver != null) {
            unregisterReceiver(screenReceiver);
        }
    }



    class ScreenReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_OFF: {
                    LogUtil.LOGI("screen off");
                    tryTemporarilyPauseScreenshotService();
                    break;
                }
                case Intent.ACTION_SCREEN_ON: {
                    LogUtil.LOGI("screen on");
                    tryTemporarilyResumeScreenshotService();

                    break;
                }
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            LogUtil.LOGI("action=" + intent.getAction());
            switch (intent.getAction()) {
                case ACTION_FIRST_START_SERVICE: {
                    initScreenshotService();
                    break;
                }

                case ACTION_BEGIN_SCREENSHOT: {

                    if (!isAlreadyOneScreenshotTask()) {
                        if (SettingHelper.hideSystemDialogs()) {
                            closeSystemNotificationDrawer();
                        }
                        UIHandler.get().postDelayed(screenShotRunnable, SettingHelper.getDelayOfCloseSystemDialog());

                    }
                    break;
                }
                case ACTION_HANDLE_SCREENSHOT_RESULT: {
                    LogUtil.LOGI("begin !!! ");
                    int resultCode = intent.getIntExtra(EXTRA_SCREENSHOT_RESULT_CODE, -1);
                    Intent intentData = (Intent) intent.getParcelableExtra(EXTRA_SCREENSHOT_RESULT_INTENT);
                    if (intentData != null) {
                        handleScreenShotIntent(resultCode, intentData);
                    }
                    break;
                }


                case ACTION_PAUSE_SCREENSHOT_SERVICE: {
                    pauseScreenshotService();
                    break;
                }
                case ACTION_RESUME_SCREENSHOT_SERVICE: {
                    resumeScreenshotService();
                    break;
                }
                case ACTION_EXIT_SCREENSHOT_SERVICE: {
                    exitScreenshotService();
                    break;
                }
            }

        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void exitScreenshotService() {
        cancelNotificationbarTool();//关闭通知栏工具
        initShakeFunctionState(true);//停用摇一摇
        stopSelf();
    }

    private void pauseScreenshotService() {
        SettingHelper.pauseScreenshotService();

        initScreenshotService();
    }

    private void resumeScreenshotService() {
        SettingHelper.resumeScreenshotService();
        initScreenshotService();
    }

    private void tryTemporarilyPauseScreenshotService(){
        boolean servicePauseState=SettingHelper.isScreenshotServicePausing();

        SettingHelper.recordServicePauseStateWhenScreenOff(servicePauseState);

        pauseScreenshotService();
    }
    private void tryTemporarilyResumeScreenshotService(){

        if (SettingHelper.isServicePausingBeforeScreenOff()==true){

        }else{
            resumeScreenshotService();
        }

    }

    private void initScreenshotService() {

        initNotificationState(SettingHelper.isScreenshotServicePausing());
        initShakeFunctionState(SettingHelper.isScreenshotServicePausing());
    }

    ShakeEventListener shakeListener;

    /**
     * 初始化摇一摇
     */
    private void initShakeFunctionState(boolean isPausing) {
        if (isPausing) {
            if (shakeListener != null) {
                shakeListener.stop();
                shakeListener = null;
            }
        } else {
            if (SettingHelper.isShakeScreenshotFunctionOpen()) {
                if (shakeListener == null) {
                    shakeListener = new ShakeEventListener(this);
                    shakeListener.setOnShakeListener(new ShakeEventListener.OnShakeListener() {
                        @Override
                        public void onShake() {
                            LogUtil.LOGI("onshake ");

                            filterOnlyOneShake();
                        }
                    });

                }

            } else {
                if (shakeListener != null) {
                    shakeListener.stop();
                    shakeListener = null;
                }
            }
        }

    }

    /**
     * 初始化通知栏的工具栏通知
     */
    private void initNotificationState(boolean isPausing) {
        //通知栏
        showNotification(isPausing);
        /*if (SettingHelper.isNotificationbarToolFunctionOpen()) {
            showNotification(isPausing);
        } else {

            cancelNotificationbarTool();
        }*/
    }

    @Subscribe
    public void onSettingChanged(BusEvents.SettingChangedEvent event) {
        if (event != null && event.eventType != null) {
            LogUtil.LOGI("event.eventType=" + event.eventType);
            switch (event.eventType) {
                case HideStatusbarIconSetting: {
                    initNotificationState(SettingHelper.isScreenshotServicePausing());
                    break;
                }
                case ShakeFunctionSettingChanged: {
                    initShakeFunctionState(SettingHelper.isScreenshotServicePausing());
                    break;
                }
                case ShakeSpeedThresholdChanged: {
                    ShakeEventListener.setDefaultSensitivity(SettingHelper.getShakeSensitivity());
                    break;
                }
            }
        }
    }

    private void showNotification(boolean isPausing) {
        LogUtil.LOGI("");

        NotificationManager nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        if (isPausing) {
            {
                Intent pendingIntent = new Intent(this, MyService.class);
                pendingIntent.setAction(MyService.ACTION_RESUME_SCREENSHOT_SERVICE);

                NotificationCompat.Action action1 = new NotificationCompat.Action(R.mipmap.play_circle_outline, getString(R.string.resume_service),
                        PendingIntent.getService(this, 102, pendingIntent, PendingIntent.FLAG_CANCEL_CURRENT));
                builder.addAction(action1);
            }
            {
                Intent pendingIntent = new Intent(this, MyService.class);
                pendingIntent.setAction(MyService.ACTION_EXIT_SCREENSHOT_SERVICE);

                NotificationCompat.Action action1 = new NotificationCompat.Action(R.mipmap.exit_to_app, getString(R.string.exit_service),
                        PendingIntent.getService(this, 103, pendingIntent, PendingIntent.FLAG_CANCEL_CURRENT));
                builder.addAction(action1);
            }
        } else {
            {

                Intent pendingIntent = new Intent(this, MyService.class);
                pendingIntent.setAction(MyService.ACTION_BEGIN_SCREENSHOT);

                NotificationCompat.Action action1 = new NotificationCompat.Action(R.mipmap.mr_ic_media_route_off_holo_light, getString(R.string.make_a_screenshot),
                        PendingIntent.getService(this, 100, pendingIntent, PendingIntent.FLAG_CANCEL_CURRENT));
                builder.addAction(action1);
            }
            {
                Intent pendingIntent = new Intent(this, MyService.class);
                pendingIntent.setAction(MyService.ACTION_PAUSE_SCREENSHOT_SERVICE);

                NotificationCompat.Action action1 = new NotificationCompat.Action(R.mipmap.pause_circle_outline, getString(R.string.pause_service),
                        PendingIntent.getService(this, 101, pendingIntent, PendingIntent.FLAG_CANCEL_CURRENT));
                builder.addAction(action1);
            }
            {
                Intent pendingIntent = new Intent(this, MyService.class);
                pendingIntent.setAction(MyService.ACTION_EXIT_SCREENSHOT_SERVICE);

                NotificationCompat.Action action1 = new NotificationCompat.Action(R.mipmap.exit_to_app, getString(R.string.exit_service),
                        PendingIntent.getService(this, 103, pendingIntent, PendingIntent.FLAG_CANCEL_CURRENT));
                builder.addAction(action1);
            }
        }
        {
            Intent pendingIntent = new Intent(this, SettingActivity.class);
            builder.setContentIntent(PendingIntent.getActivity(this, 120, pendingIntent, PendingIntent.FLAG_CANCEL_CURRENT));
        }

        builder.setAutoCancel(false)
                .setContentTitle(isPausing ? getString(R.string.screenshot_service_is_pausing) : getString(R.string.screenshot_service_is_running))
                .setContentText("")
                .setOngoing(true)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_notification_logo);

        if (SettingHelper.shouldHideStatusbarIcon()) {
            builder.setPriority(NotificationCompat.PRIORITY_MIN);
        } else {
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        Notification notification = builder.build();
        nm.notify(NOTIFICATION_ID_TOOLBAR, notification);

    }


    private static final int NOTIFICATION_ID_TOOLBAR = 100;
    private static final int NOTIFICATION_ID_FREE_AUTO_INCREMENT_START_VALUE = 1000;

    private void cancelNotificationbarTool() {
        LogUtil.LOGI("");
        NotificationManager nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID_TOOLBAR);
    }


    private void filterOnlyOneShake() {

        if (isAlreadyOneScreenshotTask() == false) {
            screenShotRunnable.run();
        } else {
            LogUtil.LOGE("already one task");
        }
    }

    public Context getContext() {
        return MyService.this;
    }

    Runnable screenShotRunnable = new Runnable() {
        @Override
        public void run() {
            LogUtil.LOGI("");

            if (ScreenshotActivity.isScreenshotActivityRunning() == false) {
                Intent intent = ScreenshotActivity.getScreenshotIntent(getContext());
                startActivity(intent);
            } else {
                LogUtil.LOGI("isScreenshotActivityRunning=true.so do nothing");
            }
        }
    };

    /**
     * 关闭系统的通知抽屉
     */
    private void closeSystemNotificationDrawer() {
        this.sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

       /* int currentApiVersion = android.os.Build.VERSION.SDK_INT;
        Context context=this;
        Object sbservice = context.getSystemService("statusbar");
        try {
            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            if (currentApiVersion <= 16) {
                Method collapse = statusbarManager.getMethod("collapse");
                collapse.invoke(sbservice);
            } else {
                Method collapse2 = statusbarManager.getMethod("collapsePanels");
                collapse2.invoke(sbservice);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }


    public static Intent getStartScreenShotAction(Context context, int resultCode, Intent data) {
        Intent i = new Intent(context, MyService.class);
        i.setAction(MyService.ACTION_HANDLE_SCREENSHOT_RESULT);
        i.putExtra(MyService.EXTRA_SCREENSHOT_RESULT_CODE, resultCode);
        i.putExtra(MyService.EXTRA_SCREENSHOT_RESULT_INTENT, data);
        return i;
    }

    public static void startScreenShot(Context context, int resultCode, Intent data) {
        Intent i = MyService.getStartScreenShotAction(context, resultCode, data);
        context.startService(i);
    }

    public static void startService(Context context) {
        Intent i = new Intent(context, MyService.class);
        i.setAction(MyService.ACTION_FIRST_START_SERVICE);
        context.startService(i);
    }

    /**
     * https://github.com/mtsahakis/MediaProjectionDemo/blob/master/src/com/mtsahakis/mediaprojectiondemo/ScreenCaptureImageActivity.java
     * https://github.com/mattprecious/telescope/blob/c56d6fecacc26f560d87070775c61398b436c6b9/telescope/src/main/java/com/mattprecious/telescope/TelescopeLayout.java#L560-L661
     *
     * @param resultCode
     * @param data
     */
    private void handleScreenShotIntent(int resultCode, Intent data) {
        LogUtil.LOGI("");

        if (SettingHelper.isVibratorOpenWhenBeginScreenshot()) {
            Utils.vibrateGently(getContext());//震动
        }

        onScreenshotTaskBegan();

        final MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        final MediaProjection mProjection = projectionManager.getMediaProjection(resultCode, data);
        Point size = Utils.getScreenSize(this);
        final int mWidth = size.x;
        final int mHeight = size.y;

        LogUtil.LOGI("width=" + mWidth + ",height=" + mHeight);

        final ImageReader mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 2);
        final VirtualDisplay display = mProjection.createVirtualDisplay("screen-mirror", mWidth, mHeight, DisplayMetrics.DENSITY_MEDIUM,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION, mImageReader.getSurface(), null, null);

        mImageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader mImageReader) {

                LogUtil.LOGI("");
                Image image = null;
                try {
                    image = mImageReader.acquireLatestImage();
                    if (image != null) {
                        final Image.Plane[] planes = image.getPlanes();
                        if (planes.length > 0) {
                            final ByteBuffer buffer = planes[0].getBuffer();
                            int pixelStride = planes[0].getPixelStride();
                            int rowStride = planes[0].getRowStride();
                            int rowPadding = rowStride - pixelStride * mWidth;


                            LogUtil.LOGI("pixelStride=" + pixelStride + ",rowStride=" + rowStride + ",rowPadding=" + rowPadding);
                            // create bitmap
                            Bitmap bmp = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                            LogUtil.LOGI("bmp.getByteCount1=" + bmp.getByteCount());
                            bmp.copyPixelsFromBuffer(buffer);
                            LogUtil.LOGI("bmp.getByteCount2=" + bmp.getByteCount());

                            Bitmap croppedBitmap = Bitmap.createBitmap(bmp, 0, 0, mWidth, mHeight);

                            saveBitmap(croppedBitmap);//保存图片

                            if (croppedBitmap != null) {
                                croppedBitmap.recycle();
                            }
                            if (bmp != null) {
                                bmp.recycle();
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    showErrorNotification(Utils.getStackTrace(e));
                } finally {
                    if (image != null) {
                        image.close();
                    }
                    if (mImageReader != null) {
                        mImageReader.close();
                    }
                    if (display != null) {
                        display.release();
                    }

                    mImageReader.setOnImageAvailableListener(null, null);
                    mProjection.stop();

                    onScreenshotTaskOver();
                }

            }
        }, getBackgroundHandler());


    }
    public int getAutoIncrementNotifyId() {
        return SettingHelper.getAutoIncrementNotifyId(NOTIFICATION_ID_FREE_AUTO_INCREMENT_START_VALUE);
    }
    private void showErrorNotification(String errorMessage) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        String errorText= String.format(getString(R.string.error_occur_reason_is),errorMessage);
        builder.setAutoCancel(true)
                .setContentTitle(getString(R.string.screenshot_generate_fail))
                .setContentText(errorText)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(errorText))
                .setSmallIcon(R.mipmap.ic_action_error)
        ;
        Notification notification = builder.build();

        NotificationManager nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        nm.notify(getAutoIncrementNotifyId(), notification);
    }


    private void showSuccessNotification(File file) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        Bitmap screenshot= BitmapFactory.decodeFile(file.getAbsolutePath());
        int imageWidth = screenshot.getWidth();
        int imageHeight = screenshot.getHeight();
        final int shortSide = imageWidth < imageHeight ? imageWidth : imageHeight;

        Bitmap preview = Bitmap.createBitmap(shortSide, shortSide,
                screenshot.getConfig() == null ? Bitmap.Config.ARGB_8888 : screenshot.getConfig());
        Canvas c = new Canvas(preview);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.25f);
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        Matrix matrix = new Matrix();
        matrix.postTranslate((shortSide - imageWidth) / 2, (shortSide - imageHeight) / 2);
        c.drawBitmap(screenshot, matrix, paint);
        c.drawColor(0x40FFFFFF);

        if (SettingHelper.shouldHideStatusbarIcon()) {
            builder.setPriority(Notification.PRIORITY_MIN);
        } else {
            builder.setPriority(Notification.PRIORITY_DEFAULT);
        }

        Intent shareIntent=new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        shareIntent.setType("image/*");
        PendingIntent sharePendingIntent=PendingIntent.getActivity(this, 0, shareIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        builder.setContentTitle(getString(R.string.screenshot_generate_success))
                .setContentText(String.format(getString(R.string.output_img_already_saved_to),file.getAbsolutePath()))
                .setContentIntent(sharePendingIntent)
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(preview))
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_action_info)
                ;

        Notification notification=builder.build();
        NotificationManager nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        nm.notify(getAutoIncrementNotifyId(), notification);
    }

    private void saveBitmap(Bitmap bmp) throws IOException {

        File childFolder = SettingHelper.getOutputFoler();
        LogUtil.LOGI("childFolder=" + childFolder.getAbsolutePath());

        File imageFile = new File(childFolder.getAbsolutePath()+"/"+ SettingHelper.getOutputScreenshotFileName());


        LogUtil.LOGI("imagefile=" + imageFile.getAbsolutePath());

        OutputStream fOut = new FileOutputStream(imageFile);
        int quality = SettingHelper.getOutputScreenshotQuality();
        LogUtil.LOGI("quality=" + quality);
        bmp.compress(Bitmap.CompressFormat.JPEG, quality, fOut);//将bg输出至文件

        fOut.flush();
        fOut.close(); // do not forget to close the stream

        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));


        if (SettingHelper.showNotificationWhenGeneratedSuccessfully()) {
            showSuccessNotification(imageFile);
        }

    }
    //在后台线程里保存文件
    Handler backgroundHandler;
    private Handler getBackgroundHandler() {
        if (backgroundHandler == null) {
            HandlerThread backgroundThread =
                    new HandlerThread("easyscreenshot", android.os.Process.THREAD_PRIORITY_BACKGROUND);
            backgroundThread.start();
            backgroundHandler = new Handler(backgroundThread.getLooper());
        }

        return backgroundHandler;
    }


    static AtomicInteger oneScreenshot = new AtomicInteger(0);

    private boolean isAlreadyOneScreenshotTask() {
        return oneScreenshot.get() == 1;
    }
    private synchronized void onScreenshotTaskBegan() {
        oneScreenshot.set(1);
    }

    private synchronized void onScreenshotTaskOver() {
        oneScreenshot.set(0);
    }




}
