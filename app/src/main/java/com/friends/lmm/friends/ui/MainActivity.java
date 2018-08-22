package com.friends.lmm.friends.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.friends.lmm.friends.R;
import com.friends.lmm.friends.adapter.MyAdapter;
import com.friends.lmm.friends.bean.Post;
import com.friends.lmm.friends.bean.User;
import com.friends.lmm.friends.utils.GradScrollView;
import com.friends.lmm.friends.utils.ImageLoader;
import com.friends.lmm.friends.utils.MyListview;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.ninegrid.ImageInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;
import cn.bmob.v3.listener.UploadFileListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GradScrollView.ScrollViewListener {

    private ImageView backGroundImg;
    private GradScrollView scrollView;
    private RelativeLayout spaceTopChange;
    private int height;
    private MyAdapter adapter;
    private List<Post> list;
    private MyListview lv;
    private SwipeRefreshLayout refresh;
    private AlertDialog al;
    private com.makeramen.roundedimageview.RoundedImageView userIcon;
    private String headUrl = "";
    private ArrayList<ImageItem> imageItems;
    private BmobUser user;
    private String head_url_res = "";//获得后的头像url
    private String post_obj = "123";//查询帖子用户obiId
    private String post_head = "1234";//查询帖子用户头像地址url

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intiView();
        intiData(0);
        getUserInfo();
        initListeners();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                intentToComm(i);
            }
        });
    }


    /**
     * 初始化控件
     */
    private void intiView() {

        findViewById(R.id.add).setOnClickListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.quanzi).setOnClickListener(this);
        findViewById(R.id.apps).setOnClickListener(this);
        findViewById(R.id.other).setOnClickListener(this);
        findViewById(R.id.lives).setOnClickListener(this);
        findViewById(R.id.mine).setOnClickListener(this);
        lv = (MyListview) findViewById(R.id.lv);
        userIcon = findViewById(R.id.userIcon);
        user = BmobUser.getCurrentUser();
        backGroundImg = (ImageView) findViewById(R.id.headBkg);
        backGroundImg.setFocusable(true);
        backGroundImg.setFocusableInTouchMode(true);
        backGroundImg.requestFocus();
        scrollView = (GradScrollView) findViewById(R.id.scrollview);
        spaceTopChange = (RelativeLayout) findViewById(R.id.spaceTopChange);
        list = new ArrayList<>();
        adapter = new MyAdapter(MainActivity.this, list);
        lv.setAdapter(adapter);
        //点击头像的处理，我这里是注销与切换用户，我下边写了更换头像的方法，根据自己情况选择
        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quit();
            }
        });

    }

    /**
     * 查询数据
     */
    private void intiData(int tag) {
        if (tag == 0) {
            showDialog();
        }

        list.clear();
        BmobQuery<Post> query = new BmobQuery<>();
        query.order("-createdAt");
        query.setLimit(20);
        query.findObjects(new FindListener<Post>() {
            @Override
            public void done(List<Post> lists, BmobException e) {
                if (e == null) {

                   /* for (int i=0;i<lists.size();i++){
                        Post post =lists.get(i);
                        post.setUserIcon(getHeadUrl(queryHeadByName(lists.get(i).getUserName())));
                        toast(getHeadUrl(queryHeadByName(lists.get(i).getUserName())));
                        list.add(post);

                    }*/
                    list = lists;
                    adapter.addPost(list);
                    adapter.notifyDataSetChanged();
                    al.dismiss();

                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add:
                if (head_url_res.equals("")) {
                    toast("等待数据初始化中...");
                    return;
                }
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("headUrl", head_url_res);
                startActivity(intent);
                break;
            case R.id.back:
                finish();
                break;
            case R.id.userIcon:
                //打开相机选择头像
                addHead();
                break;
            case R.id.quanzi://圈子
                //intiData(0);
                break;
            case R.id.apps://应用
                startActivity(new Intent(MainActivity.this, AppsActivity.class));
                break;
            case R.id.other://其他

                break;
            case R.id.lives://直播

                break;
            case R.id.mine://我的

                break;
        }
    }

    /**
     * 获取顶部图片高度后，设置滚动监听
     */
    private void initListeners() {

        ViewTreeObserver vto = backGroundImg.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                spaceTopChange.getViewTreeObserver().removeGlobalOnLayoutListener(
                        this);
                height = backGroundImg.getHeight();

                scrollView.setScrollViewListener(MainActivity.this);
            }
        });
    }

    /**
     * 滑动监听
     * 根据滑动的距离动态改变标题栏颜色
     */
    @Override
    public void onScrollChanged(GradScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (y <= 0) {   //设置标题的背景颜色
            spaceTopChange.setBackgroundColor(Color.argb(0, 144, 151, 166));
        } else if (y > 0 && y <= height - 10) { //滑动距离小于banner图的高度时，设置背景和字体颜色颜色透明度渐变
            float scale = (float) y / height;
            float alpha = (255 * scale);
            spaceTopChange.setBackgroundColor(Color.argb((int) alpha, 130, 117, 140));
        } else {    //滑动到banner下面设置普通颜色
            spaceTopChange.setBackgroundColor(Color.parseColor("#584f60"));
        }
    }

    /*
      传递数据到详情页
     */
    public void intentToComm(int i) {
        //传递数据到评论页面
        Intent intent = new Intent(MainActivity.this, CommentActivity.class);
        intent.putExtra("username", list.get(i).getUserName());
        intent.putExtra("content", list.get(i).getContent());
        intent.putExtra("time", list.get(i).getCreatedAt());
        intent.putExtra("head", list.get(i).getUserIcon());
        Boolean isHaven = list.get(i).isHaveIcon();
        if (isHaven) {
            intent.putExtra("isHaven", "true");
        } else {
            intent.putExtra("isHaven", "false");
        }

        String good = list.get(i).getPraise().toString();
        intent.putExtra("goods", good);
        String comments = list.get(i).getComment().toString();
        intent.putExtra("comment", comments);

        //如果帖子没有图片就做处理 传入空
        if (list.get(i).getHeadImgUrl() != null) {
            intent.putStringArrayListExtra("infoList", (ArrayList<String>) list.get(i).getHeadImgUrl());
        } else {
            intent.putStringArrayListExtra("infoList", null);

        }
        intent.putExtra("obj", list.get(i).getObjectId());
        //  intent.putExtra("urlList", (Parcelable) list.get(i).getHeadImgUrl());

        startActivity(intent);
    }

    public void showDialog() {
        LayoutInflater inflater = getLayoutInflater();

        al = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setView(R.layout.dialog)

                .show();

    }

    /**
     * 添加头像哦 这个自己按情况选择是否使用
     */
    private void addHead() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new ImageLoader());
        imagePicker.setMultiMode(false);   //多选
        imagePicker.setSelectLimit(1);    //最多选择X张
        imagePicker.setCrop(true);       //不进行裁剪
        Intent intent = new Intent(MainActivity.this, ImageGridActivity.class);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == 100) {
                ArrayList<ImageInfo> imageInfo = new ArrayList<>();
                imageItems = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                changeHead();
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
            headUrl = filePaths[0];
            final User users = new User();
            final BmobFile bmobFile = new BmobFile(new File(headUrl));
            bmobFile.uploadblock(new UploadFileListener() {

                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        users.setHead(bmobFile.getFileUrl());
                        users.update(user.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    toast("修改成功");

                                }
                            }
                        });
                    } else {
                        toast("上传失败：" + e.getMessage());
                    }

                }

                @Override
                public void onProgress(Integer value) {
                    // 返回的上传进度（百分比）
                }
            });


        }

    }

    /*
    获取用户信息
     */
    public void getUserInfo() {
        BmobQuery<User> query = new BmobQuery<User>();
        query.getObject(user.getObjectId(), new QueryListener<User>() {

            @Override
            public void done(User object, BmobException e) {
                if (e == null) {
                    //获得USER的信息
                    head_url_res = object.getHead();
                    Glide.with(MainActivity.this).load(object.getHead()).into(userIcon);

                } else {

                }
            }

        });
    }

    /*
      根据用户名查询id
       */
    public String queryHeadByName(String usernameq) {
        final String[] res = {""};
        BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
        query.addWhereEqualTo("username", usernameq);
        query.findObjects(new FindListener<BmobUser>() {
            @Override
            public void done(List<BmobUser> object, BmobException e) {
                if (e == null) {
                    res[0] = object.get(0).getObjectId();

                } else {
                    toast("更新用户信息失败:" + e.getMessage());
                }
            }
        });

        return res[0];
    }

    /*
      根据用户id获取头像
     */
    public String getHeadUrl(String objId) {
        final String[] res_head = {""};
        BmobQuery<User> query = new BmobQuery<User>();
        query.getObject(objId, new QueryListener<User>() {

            @Override
            public void done(User object, BmobException e) {
                if (e == null) {
                    //获得USER的信息
                    res_head[0] = object.getHead();


                } else {

                }
            }

        });
        return res_head[0];
    }

    @Override
    protected void onResume() {
        super.onResume();
        intiData(1);
    }

    //Toast
    private void toast(String date) {
        Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
    }

    /*
    注销操作
     */
    public void quit() {
        al = new AlertDialog.Builder(this)
                .setTitle("确定注销吗")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        BmobUser.logOut();   //清除缓存用户对象
                        toast("注销成功");
                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }
}
