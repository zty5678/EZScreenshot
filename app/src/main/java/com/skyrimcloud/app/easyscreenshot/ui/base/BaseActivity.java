package com.skyrimcloud.app.easyscreenshot.ui.base;

import android.app.Activity;

import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName:
 * @Description:
 * @date 2015/11/24 15:58
 */
public class BaseActivity extends Activity {
    public void closeActivity() {
        this.finish();
    }


    public void onResume() {
        super.onResume();
//        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
//        MobclickAgent.onPause(this);
    }

}
