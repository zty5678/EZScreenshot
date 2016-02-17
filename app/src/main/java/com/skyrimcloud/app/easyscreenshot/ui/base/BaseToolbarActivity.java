package com.skyrimcloud.app.easyscreenshot.ui.base;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.skyrimcloud.app.easyscreenshot.R;
import com.skyrimcloud.app.easyscreenshot.utils.Utils;
import com.umeng.analytics.MobclickAgent;

/**
 * @ClassName:
 * @Description:
 * @date 2015/11/16 15:29
 */
public class BaseToolbarActivity extends AppCompatActivity {

    public void closeActivity(){
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


    Toolbar toolbar;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            if (setToolbarElevation()) {
                ViewCompat.setElevation(toolbar, Utils.dip2px(getContext(), 4));

            }
            setSupportActionBar(toolbar);

        }

    }

    public Context getContext() {
        return this;
    }

    public boolean setToolbarElevation(){
        return true;
    }
    public Toolbar getToolbar(){
        return toolbar;
    }

}
