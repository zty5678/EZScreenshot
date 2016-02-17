package com.skyrimcloud.app.easyscreenshot.ui.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;


public class BaseBackableToolbarActivity extends BaseToolbarActivity {

    public Context getContext(){
        return this;
    }
    public Activity getAcitivity(){
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        if (getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

}
