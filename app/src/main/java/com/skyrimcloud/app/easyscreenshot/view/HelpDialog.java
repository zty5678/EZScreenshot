package com.skyrimcloud.app.easyscreenshot.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.ArrayRes;
import android.util.Log;
import android.util.Pair;
import android.webkit.WebView;

import com.skyrimcloud.app.easyscreenshot.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: 
 * @Description:
 * @date 2015/11/25 10:35
 */
public class HelpDialog {
    private static final String TAG = "HelpDialog";
    private final Context mContext;
    private String mStyle = "* {   } "
            + ".title { font-size: 12pt; display: block; padding:4px; color: #232323; }"
            + ".content { font-size: 10pt; display: block; padding:4px; color: #767676;line-height: 120%; }";

    protected DialogInterface.OnDismissListener mOnDismissListener;

    public HelpDialog(final Context context) {
        mContext = context;
    }

    protected Context getContext() {
        return mContext;
    }

    //CSS style for the html
    private String getStyle() {
        return String.format("<style type=\"text/css\">%s</style>", mStyle);
    }

    public void setStyle(final String style) {
        mStyle = style;
    }

    public String getHtmlContent(@ArrayRes int arrayResId) {
        String array[] = getContext().getResources().getStringArray(arrayResId);
        List<Pair<String, String>> arrays = new ArrayList<>();
        for (int i = 0; i < array.length; i += 2) {
            int j = i + 1;
            if (j < array.length) {
                String first = array[i];
                String second = array[j];
                arrays.add(new Pair<String, String>(first, second));
            }
        }


        final StringBuilder changelogBuilder = new StringBuilder();
        changelogBuilder
                .append("<!DOCTYPE html><html><head>")
                .append(getStyle())
                .append("</head><body>");

        for (Pair<String, String> pair : arrays) {
            changelogBuilder.append("<span class='title' >" + pair.first + "</span>");
            changelogBuilder.append("<span class='content' >" + pair.second + "</span>");
        }

        changelogBuilder.append("</body></html>");

        return changelogBuilder.toString();

    }

    public void show(String title,@ArrayRes int arrayResId) {

        String htmlContent = getHtmlContent(arrayResId);
        Log.i(TAG, "htmlContent =   " + htmlContent);

        //Create web view and load html
        final WebView webView = new WebView(mContext);
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "utf-8", null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setView(webView)
                .setPositiveButton(getContext().getString(R.string.close), new Dialog.OnClickListener() {
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface dialog) {
                if (mOnDismissListener != null) {
                    mOnDismissListener.onDismiss(dialog);
                }
            }
        });
        dialog.show();
    }

}
