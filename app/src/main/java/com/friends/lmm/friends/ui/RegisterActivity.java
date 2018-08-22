package com.friends.lmm.friends.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.friends.lmm.friends.R;
import com.friends.lmm.friends.bean.User;
import com.friends.lmm.friends.utils.ImageLoader;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.ninegrid.ImageInfo;

import java.io.File;
import java.util.ArrayList;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class RegisterActivity extends AppCompatActivity {
    private EditText reg_user,reg_pasd;
    String name,password;
    private String head_url_res = "";//获得后的头像url
    private ImageView userIcon;
    private ArrayList<ImageItem> imageItems;
    private Button btn_reg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reg_user = (EditText) findViewById(R.id.et_username);
        reg_pasd = (EditText) findViewById(R.id.et_pwd);
        userIcon = findViewById(R.id.reg_head);
        btn_reg=findViewById(R.id.reg_now);
        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                addHead();
            }
        });
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                regs();
            }
        });
    }

    private void regs() {
        // TODO Auto-generated method stub
        name=reg_user.getText().toString();
        password=reg_pasd.getText().toString();
        if (name.isEmpty()) {
            toast("用户名为空");
            return;
        }
        if (password.isEmpty()) {
            toast("密码为空");
            return;
        }
        if (head_url_res.isEmpty()) {
            toast("头像为空");
            return;
        }
        // isChecked = true;
        changeHead();


        }
    /**
     * 添加头像哦
     */
    private void addHead() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new ImageLoader());
        imagePicker.setMultiMode(false);   //多选
        imagePicker.setSelectLimit(1);    //最多选择X张
        imagePicker.setCrop(true);       //不进行裁剪
        Intent intent = new Intent(RegisterActivity.this, ImageGridActivity.class);
        startActivityForResult(intent, 100);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                ArrayList<ImageInfo> imageInfo = new ArrayList<>();
                imageItems = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                String[] filePaths = new String[imageItems.size()];
                filePaths[0] = imageItems.get(0).path;
                head_url_res = filePaths[0];
                Glide.with(RegisterActivity.this).load(head_url_res).into(userIcon);
            } else {
                toast("没有选择图片");
            }
        }
    }
    /*
       获取图片url然后上传
        */
    public void changeHead() {
        final String[] filePaths = new String[imageItems.size()];
        for (int i = 0; i < imageItems.size(); i++) {
            filePaths[i] = imageItems.get(i).path;
            head_url_res = filePaths[0];
            toast("注册中请稍等...");
            final BmobFile bmobFile = new BmobFile(new File(head_url_res));
            bmobFile.uploadblock(new UploadFileListener() {
               @Override
               public void done(BmobException e) {
                   if (e==null){
                       User user = new User();
                       user.setUsername(name);
                       user.setPassword(password);
                       user.setHead(bmobFile.getFileUrl());
                       user.signUp(new SaveListener<User>() {
                           @Override
                           public void done(User s, BmobException e) {
                               if(e==null){
                                   toast("注册成功:" +s.toString());
                                   Intent intent = new Intent();
                                   intent.setClass(RegisterActivity.this, MainActivity.class);
                                   startActivity(intent);
                                   finish();
                               }else{
                                   toast("注册失败，可能用户名已存在" );
                               }
                           }
                       });

                   }
               }
           });


        }

    }


    public void toast(String msg) {
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            }
