package com.friends.lmm.friends;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.friends.lmm.friends.utils.ImageLoader;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.lzy.ninegrid.NineGridView;

import cn.bmob.v3.Bmob;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //在这里填写你在Bmob申请的ID
        Bmob.initialize(this, "9e4fdb5cd599c05bec6f5aa03ae5ab0e");
        NineGridView.setImageLoader(new GlideImageLoader());
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new ImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);  //显示拍照按钮
        imagePicker.setCrop(true);        //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true); //是否按矩形区域保存
        imagePicker.setSelectLimit(9);    //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);//保存文件的高度。单位像素
    }
    private class GlideImageLoader implements NineGridView.ImageLoader {
        @Override
        public void onDisplayImage(Context context, ImageView imageView, String url) {
            Glide.with(context).load(url)//
                    .placeholder(R.drawable.ic_default_image)//
                    .error(R.drawable.ic_default_image)//
                    .into(imageView);

        }
        @Override
        public Bitmap getCacheImage(String url) {
            return null;
        }
    }

    /*
     final String[] filePaths = new String[imageItems.size()];
        for (int i = 0; i < imageItems.size(); i++) {
            filePaths[i] = imageItems.get(i).path;
            head_url_res = filePaths[0];
            final User user = new User();
            final BmobFile bmobFile = new BmobFile(new File(head_url_res));
            bmobFile.uploadblock(new UploadFileListener() {

                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        user.setHead(bmobFile.getFileUrl());
                        User user = new User();
                        user.setUsername(name);
                        user.setPassword(password);
                        user.signUp(new SaveListener<BmobUser>() {
                            @Override
                            public void done(BmobUser bmobUser, BmobException e) {
                                if (e == null) {
                                    toast("注册成功！");
                                    Intent intent = new Intent();
                                    intent.setClass(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    toast("注册失败！ " + e.toString());
                                }
                            }
     */
}
