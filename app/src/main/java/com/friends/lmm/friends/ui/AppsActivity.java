package com.friends.lmm.friends.ui;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;

import com.friends.lmm.friends.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppsActivity extends AppCompatActivity {

    private GridView gridView;
    private List<Map<String, Object>> dataList;
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_apps);
        gridView = (GridView) findViewById(R.id.gridview);
        //初始化数据
        initData();

        String[] from={"img","text"};

        int[] to={R.id.img,R.id.text};

        adapter=new SimpleAdapter(this, dataList, R.layout.gridview_app_item, from, to);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                AlertDialog.Builder builder= new AlertDialog.Builder(AppsActivity.this);
                builder.setTitle("提示").setMessage(dataList.get(arg2).get("text").toString()).create().show();
            }
        });
    }

    void initData() {
        //图标
        int icno[] = { R.mipmap.icon, R.mipmap.icon, R.mipmap.icon,
                R.mipmap.icon, R.mipmap.icon, R.mipmap.icon, R.mipmap.icon,
                R.mipmap.icon, R.mipmap.icon, R.mipmap.icon, R.mipmap.icon, R.mipmap.icon };
        //图标下的文字
        String name[]={"时钟","信号","宝箱","秒钟","大象","FF","记事本","书签","印象","商店","主题","迅雷"};
        dataList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i <icno.length; i++) {
            Map<String, Object> map=new HashMap<String, Object>();
            map.put("img", icno[i]);
            map.put("text",name[i]);
            dataList.add(map);
        }
    }

}
