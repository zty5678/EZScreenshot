package com.skyrimcloud.app.easyscreenshot.ui;

import android.os.Bundle;
import android.webkit.WebView;

import com.skyrimcloud.app.easyscreenshot.R;
import com.skyrimcloud.app.easyscreenshot.ui.base.BaseBackableToolbarActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HelpActivity extends BaseBackableToolbarActivity {

    @InjectView(R.id.webview)
    WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        ButterKnife.inject(this);

        String content= getString(R.string.app_help_string);
        webview.loadData(content,"text/html; charset=UTF-8",null);
    }
}
