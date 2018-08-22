package com.friends.lmm.friends.utils;

/**
 * Created by Andream on 2017/10/19.
 * 自定义listview
 */
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class MyListview extends ListView {

    public MyListview(Context context) {
        // TODO Auto-generated method stub
        super(context);
    }

    public MyListview(Context context, AttributeSet attrs) {
        // TODO Auto-generated method stub
        super(context, attrs);
    }

    public MyListview(Context context, AttributeSet attrs, int defStyle) {
        // TODO Auto-generated method stub
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
