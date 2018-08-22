package com.friends.lmm.friends.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.friends.lmm.friends.R;
import com.friends.lmm.friends.adapter.CommentAdapter;
import com.friends.lmm.friends.bean.Comment;
import com.friends.lmm.friends.bean.Post;
import com.friends.lmm.friends.bean.User;
import com.friends.lmm.friends.utils.MyListview;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener{
    private float mFirstY;
    private float mCurrentY;
    private int direction;
    private int mTouchSlop;
    private ArrayList<Comment> list = new ArrayList();
    private CommentAdapter adapter;
    private MyListview listView;
    RoundedImageView head;
    private Post post = new Post();
    TextView tv_name, tv_time, tv_content,item_good_fabulous,item_good_comment;
    private NineGridView nineGridView;
    private Button btn_reply;
    private EditText repy_content,ed_comm;
    private User user;
    private String obj;
    private AlertDialog al;
    private ArrayList<String> picList = new ArrayList<>();
    private LinearLayout ly_opte,area_commit;
    private ImageView et_reply,back_deal,comm_share,comm_del;//返回
    private Boolean isHaven=false;//是否存在图片
    private String auhthor_url;//帖子作者id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        init();
        initListener();
        getUrl();
    }
    /*
    初始化数据
     */
    void init() {

        listView = findViewById(R.id.mylv);
        tv_name = findViewById(R.id.tv_comment_username);
        tv_time = findViewById(R.id.tv_comment_time);
        tv_content = findViewById(R.id.tv_comment_content);
        item_good_fabulous = findViewById(R.id.item_good_fabulous);
        item_good_comment = findViewById(R.id.item_good_comment);
        head = findViewById(R.id.comment_friend_icon);
        btn_reply = findViewById(R.id.btn_comm);
        repy_content = findViewById(R.id.ed_comm);
        nineGridView = findViewById(R.id.comm_nine);
        area_commit=findViewById(R.id.area_commit);
        back_deal=findViewById(R.id.back_deal);
        //et_reply=findViewById(R.id.comm_repy);
        ly_opte = findViewById(R.id.ly_opte);
        comm_share=findViewById(R.id.comm_share);
        comm_del=findViewById(R.id.comm_del);
        tv_name.setText(getIntent().getStringExtra("username"));
        tv_time.setText(getIntent().getStringExtra("time"));
        tv_content.setText(getIntent().getStringExtra("content"));
        String headurl = getIntent().getStringExtra("head");
        item_good_fabulous.setText(getIntent().getStringExtra("goods"));
        item_good_comment.setText(getIntent().getStringExtra("comment"));
        obj = getIntent().getStringExtra("obj");
        if (getIntent().getStringExtra("isHaven").equals("true")){
            isHaven=true;
        }
        post.setObjectId(obj);
        Glide.with(CommentActivity.this).load(headurl).into(head);
        user = BmobUser.getCurrentUser(User.class);
        //user=BmobUser.getCurrentUser(User.class);

        if (getIntent().getStringArrayListExtra("infoList") == null) {
            nineGridView.setVisibility(View.GONE);
        } else {
            picList = getIntent().getStringArrayListExtra("infoList");
            initPics(picList);
        }

        findComments();
        adapter = new CommentAdapter(list, this);
        listView.setAdapter(adapter);
        btn_reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //评论
                String content = repy_content.getText().toString();
                publishComment(content);
            }
        });

    }
  void initListener(){
      item_good_comment.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              reply2();
          }
      });
      item_good_fabulous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updates();//点赞
            }
        });
        back_deal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentActivity.this.finish();//关闭详情页
            }
        });
        comm_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share();
            }
        });
        comm_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //删除帖子
                   del();
            }
        });
  }
    private void toast(String date) {
        Toast.makeText(this, date, Toast.LENGTH_SHORT).show();
    }


    private void publishComment(String content) {

        if (user == null) {
            toast("发表评论前请先登陆");
            return;
        } else if (TextUtils.isEmpty(content)) {
            toast("发表评论不能为空");
            return;
        }
        showDialog_com();
        final Comment comment = new Comment();

        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);
        comment.setName(user.getUsername());
        comment.setTime(getTime());
        comment.setUserHead(user.getHead());
        comment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    al.dismiss();
                    findComments();
                    updatescomment();
                    toast("评论成功");
                    adapter.notifyDataSetInvalidated();
                    repy_content.setText("");
                    int comments=Integer.parseInt(item_good_comment.getText().toString())+1;
                    item_good_comment.setText(comments+"");
                } else {
                    toast(e.toString());
                    al.dismiss();
                }
            }
        });

    }

    /*
    查询评论
     */
    private void findComments() {
        showDialog();
        BmobQuery<Comment> query = new BmobQuery<Comment>();
        list.clear();
        Post post = new Post();
        post.setObjectId(obj);
        query.addWhereEqualTo("post", new BmobPointer(post));
        query.include("user,,author,post.author,comment.time,comment.user");
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(List<Comment> arg0, BmobException e) {
                if (e == null) {

                    list.addAll(arg0);
                    //com_num = list.size();
                    al.dismiss();
                    adapter.notifyDataSetInvalidated();

                } else {
                    al.dismiss();
                    toast("查询评论失败" + e.toString());
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    /*
         获取时间
     */
    public String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 hh点");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        return formatter.format(curDate);
    }

    private void showDialog_com() {
        LayoutInflater inflater = getLayoutInflater();
        al = new AlertDialog.Builder(this)
                .setTitle("回复评论中...")
                .setView(R.layout.dialog_com)
                .show();

    }
    /*
    隐藏输入框
     */
    private void hideSoftInput(){
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(ed_comm.getWindowToken(), 0);
    }
    /*
    加载帖子图片集合
     */
    public void initPics(List<String> picList) {

        if (picList.size() > 0) {//判断是否有图片
            ArrayList<ImageInfo> imageInfo = new ArrayList<>();
            for (int j = 0; j < picList.size(); j++) {
                ImageInfo info = new ImageInfo();
                info.setThumbnailUrl(picList.get(j));
                info.setBigImageUrl(picList.get(j));
                imageInfo.add(info);
            }
            nineGridView.setAdapter(new NineGridViewClickAdapter(CommentActivity.this, imageInfo));
        } else {
            nineGridView.setVisibility(View.GONE);
        }
    }
    /*
    弹出输入框
     */
    public void reply2(){
        repy_content.requestFocus();
        InputMethodManager imm = (InputMethodManager) repy_content.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }
    /*
    删除帖子
     */
    private void del(){
        Post p = new Post();
        p.setObjectId(obj);
        if (this.user.getObjectId().equals(auhthor_url)){
            p.delete(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        toast("删除成功");
                        CommentActivity.this.finish();
                    }else{
                        toast("失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }else {
            toast("您无权限删除别人发的帖子哦");
        }


    }


    /*
    获取帖子作者信息objid
     */
    public void getUrl() {
        final String[] obj_info = {""};
        final BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
        query.addWhereEqualTo("username", tv_name.getText().toString());
        query.findObjects(new FindListener<BmobUser>() {
            @Override
            public void done(List<BmobUser> list, BmobException e) {
                al.dismiss();
                for (BmobUser data : list) {
                    obj_info[0] = data.getObjectId();
                    auhthor_url = obj_info[0];
                }
            }

        });
    }
    /*
    点赞
     */
    public void updates() {
        Post post = new Post();
        post.setObjectId(obj);
        // TODO Auto-generated method stub
        post.increment("praise");
        //不知道什么原因点赞后图片会显消失，所以标记一下
        //post.setHaveIcon(isHaven);
        post.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //toast("点赞成功！");
                    int fabulous=Integer.parseInt(item_good_fabulous.getText().toString())+1;
                    item_good_fabulous.setText(fabulous+"");

                } else {
                    toast("点赞失败！");
                }
            }
        });

    }
    /*
    点赞
     */
    public void updatescomment() {
        Post post = new Post();
        post.setObjectId(obj);
        // TODO Auto-generated method stub
        post.increment("comment");
        //不知道什么原因点赞后图片会显消失，所以标记一下
        //post.setHaveIcon(isHaven);
        post.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                   // toast("点赞成功！");

                } else {
                    //toast("点赞失败！");
                }
            }
        });

    }
    /*
    分享
     */
    public void share(){
        Intent intent=new Intent(Intent.ACTION_SEND);
        intent.setType("text/*");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT,"www.baidu.com");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(Intent.createChooser(intent, "分享"));
    }

    /*
    加载框
     */
    private void showDialog() {
        // 首先得到整个View
        LayoutInflater inflater = getLayoutInflater();
        al = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("数据装载中...")
                .setView(R.layout.dialog_com)
                .setCancelable(true)
                .show();

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.ed_comm:
                   reply2();
                break;
        }
    }
}
