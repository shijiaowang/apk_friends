package com.friends.lmm.friends.utils;

import android.content.Context;
import android.content.Intent;
import android.text.style.ClickableSpan;
import android.view.View;

import tbsplus.tbs.tencent.com.tbsplus.TbsPlus;

public class CustomUrlSpan extends ClickableSpan {

    private Context context;
    private String url;
    public CustomUrlSpan(Context context,String url){
        this.context = context;
        this.url = url;
    }

    @Override
    public void onClick(View widget) {
        // 在这里可以做任何自己想要的处理
        TbsPlus.openUrl(context, url, TbsPlus.eTBSPLUS_SCREENDIR.eTBSPLUS_SCREENDIR_SENSOR);
    }
}
