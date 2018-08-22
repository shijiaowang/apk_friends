package com.friends.lmm.friends.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.friends.lmm.friends.R;
import com.friends.lmm.friends.bean.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {
    private EditText log_user,log_pasd;
    private BmobUser user;
    private Button btn_log,btn_reg;
    String name,password;
    boolean ishave=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        log_user = (EditText) findViewById(R.id.et_username);
        log_pasd = (EditText) findViewById(R.id.et_pwd);
        btn_log=findViewById(R.id.btn_login);
        btn_reg=findViewById(R.id.btn_reg);
        user=BmobUser.getCurrentUser();
        if (user!=null){
            Intent intent = new Intent();
            intent.setClass(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               reg();
            }
        });
    }


    public void toast(String msg) {
        Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
    private void login() {
        // TODO Auto-generated method stub
        name=log_user.getText().toString();
        password=log_pasd.getText().toString();
        if (name.isEmpty()) {
            toast("用户名为空");
            return;
        }
        if (password.isEmpty()) {
            toast("密码为空");
            return;
        }
        // isChecked = true;


        User user = new User();
        user.setUsername(name);
        user.setPassword(password);
            user.login(new SaveListener<BmobUser>() {
                @Override
                public void done(BmobUser bmobUser, BmobException e) {
                    if(e==null){
                        toast("登录成功！");
                        Intent intent = new Intent();
                        intent.setClass(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        toast("登录失败！ " + e.toString());
                    }
                }
            });

       

    }
    void reg(){
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
    /*
    判断用户是否已经存在
     */
    private boolean isHaveUser(String user){

        BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
        query.addWhereEqualTo("username", user);
        query.findObjects(new FindListener<BmobUser>() {
            @Override
            public void done(List<BmobUser> object, BmobException e) {
                if(e==null){
                    toast("查询用户成功:"+object.size());

                    ishave=true;
                }else{
                    toast("查询用户信息失败:" + e.getMessage());
                }
            }
        });
        return ishave;
    }
}
