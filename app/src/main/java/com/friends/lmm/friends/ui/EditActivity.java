package com.friends.lmm.friends.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.friends.lmm.friends.R;
import com.friends.lmm.friends.bean.Post;
import com.friends.lmm.friends.utils.CircleTransform;
import com.friends.lmm.friends.utils.ImageLoader;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.ninegrid.ImageInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;


public class EditActivity extends AppCompatActivity {
    private EditText et_send;
    private GridView publishGridView;
    private GridAdapter gridAdapter;
    private TextView tv_upload,tv_cancle;
    private int size = 0;
    private String content;
    private BmobUser user;
    private ArrayList<ImageItem> imageItems;
    ProgressDialog dialog = null;//进度条

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_edit);
        
        intiView();
    }

    private void intiView() {
        et_send= (EditText) findViewById(R.id.et_content);
        tv_upload= (TextView) findViewById(R.id.tv_send);
        tv_cancle=findViewById(R.id.tv_cancle);
        user=BmobUser.getCurrentUser();
        publishGridView= (GridView) findViewById(R.id.publishGridView);
        gridAdapter = new GridAdapter();
        publishGridView.setAdapter(gridAdapter);

        findViewById(R.id.tv_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content = et_send.getText().toString();

                if (content.length() < 1 && size == 0) {
                    toast("发表不能为空");
                } else {
                    tv_upload.setEnabled(false);


                            tv_upload_database();

                }
            }
        });
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               EditActivity.this.finish();
            }
        });
    }

    /**
     * 上传图片
     */
    private void tv_upload_database() {
        //隐藏软硬盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);

        toast("发布中...");
        String username=user.getUsername();
        final Post post = new Post();
        post.setContent(content);
        post.setUserName(username);
        post.setPraise(0);
        post.setComment(0);
        post.setAnchorId(BmobUser.getCurrentUser().getObjectId());
        post.setTime(getTime());
        post.setUserIcon(getIntent().getStringExtra("headUrl"));
        if (size == 0) {
            post.setHaveIcon(false);
            post.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if(e==null)
                    {
                        et_send.setText("");
                        toast("发表成功");
                        finish();

                    }else {
                        toast("失败"+e.toString());
                    }
                }
            });
            return;
        }
        size = 0;
        final String[] filePaths = new String[imageItems.size()];
        for (int i = 0; i < imageItems.size(); i++) {
            filePaths[i] = imageItems.get(i).path;
        }
        dialog = new ProgressDialog(EditActivity.this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setTitle("上传图片中...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> list1) {
                if (list1.size() == filePaths.length) {//如果数量相等，则代表文件全部上传完成
                    post.setHeadImgUrl(list1);
                    post.setHaveIcon(true);
                    post.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {

                            if (e == null) {
                                toast("发表成功");
                                finish();

                            }
                        }
                    });
                }
            }

            @Override
            public void onProgress(int i, int i1, int i2, int i3) {
                dialog.setProgress(i2);
            }

            @Override
            public void onError(int i, String s) {
                dialog.dismiss();
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                ArrayList<ImageInfo> imageInfo = new ArrayList<>();
                imageItems = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                gridAdapter.notifyDataSetChanged();
                size=imageItems.size();
            } else {
                toast("没有选择图片");
            }
        }
    }
    private class GridAdapter extends BaseAdapter {
        public GridAdapter() {
        }

        @Override
        public int getCount() {
            if (imageItems == null)
                return 1;
            else
                return imageItems.size()+1;
        }

        @Override
        public Object getItem(int i) {
            return imageItems.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            GridAdapter.ViewHolder holder = null;
            if (view == null) {
                holder = new GridAdapter.ViewHolder();
                view = LayoutInflater.from(EditActivity.this).inflate(R.layout.grid_layout, null);
                holder.image_voice = (ImageView) view.findViewById(R.id.gird_img);
                view.setTag(holder);
            } else {
                holder = (GridAdapter.ViewHolder) view.getTag();
            }
            if (imageItems == null) {
                holder.image_voice.setImageResource(R.mipmap.add_icon);
            } else {
                if (i == imageItems.size()) {
                    holder.image_voice.setImageResource(R.mipmap.add_icon);
                } else {
                    File file = new File(imageItems.get(i).path);
                    if (file.exists()) {
                        Bitmap bm = BitmapFactory.decodeFile(imageItems.get(i).path);
                        holder.image_voice.setImageBitmap(CircleTransform.centerSquareScaleBitmap(bm,100));
                    }
                }
            }
            holder.image_voice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((imageItems != null && i == imageItems.size()) || imageItems == null) {
                        addImage();
                    }
                }
            });
            return view;
        }

        class ViewHolder {
            private ImageView image_voice;
        }
    }
    /**
     * 添加图片哦
     */
    private void addImage() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new ImageLoader());
        imagePicker.setMultiMode(true);   //多选
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setSelectLimit(6);    //最多选择X张
        imagePicker.setCrop(false);       //不进行裁剪
        Intent intent = new Intent(EditActivity.this, ImageGridActivity.class);
        startActivityForResult(intent, 100);
    }
    /*
        获取时间
    */
    public String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }
    //Toast
    private void toast(String date){
        Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
    }
}
